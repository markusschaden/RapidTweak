package com.zuehlke.carrera.javapilot.akka.rapidtweak.optimizer;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.MonitoringMessage;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.power.PowerNotifier;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.service.ServiceManager;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Duration;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Race;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.SpeedMeasureTrackElement;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.TrackElement;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.trackmodel.HeuristicElements;
import com.zuehlke.carrera.relayapi.messages.PenaltyMessage;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import com.zuehlke.carrera.relayapi.messages.VelocityMessage;
import lombok.Data;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markus on 25.09.2015.
 */
@Data
@ToString
public class TrackOptimizer implements PowerNotifier {

    private final Logger LOGGER = LoggerFactory.getLogger(TrackOptimizer.class);

    private List<Optimizer> optimizers = new ArrayList<>();
    private TrackElement currentTrackElement;
    private Race race;
    private HeuristicElements heuristicElements;
    private int power;
    private long startTrackElement;
    private int segementCounter = 0;
    private long timeRoundBegin;
    private int velocityCounter = 0;

    public TrackOptimizer(Race race) {
        this.race = race;
        StraightOptimizer straightOptimizer = new StraightOptimizer(race);
        HalfCurveOptimizer halfCurveOptimizer = new HalfCurveOptimizer(race);
        straightOptimizer.setActive(true);
        heuristicElements = new HeuristicElements();

        optimizers.add(straightOptimizer);
        optimizers.add(halfCurveOptimizer);
    }


    /***
     * Start optimizing
     * Must be called at the begin of a round
     */
    public void optimize(RoundTimeMessage message) {
        currentTrackElement = race.getTrack().get(segementCounter);
        startTrackElement = message.getTimestamp();
        timeRoundBegin = message.getTimestamp();
        velocityCounter = 0;
    }


    public void onRoundTimeMessage(RoundTimeMessage message) {
        timeRoundBegin = message.getTimestamp();

        com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.RoundTimeMessage roundTimeMessage = new com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.RoundTimeMessage();
        roundTimeMessage.setRoundTime(message.getRoundDuration());
        roundTimeMessage.setRace(race);
        ServiceManager.getInstance().getMessageDispatcher().sendMessage(roundTimeMessage);
    }


    public void onSensorEvent(SensorEvent sensorEvent) {

        if (currentTrackElement != null) {

            TrackElement newTrackElement = (heuristicElements.getHeuristicElement(sensorEvent.getG()[2]));

            if (newTrackElement != null && !currentTrackElement.getClass().equals(newTrackElement.getClass())) {

                long end = sensorEvent.getTimeStamp();
                currentTrackElement.getDurations().add(new Duration(power, end - startTrackElement));
                currentTrackElement.setLatestDuration(end - startTrackElement);

                TrackElement nextTrackElement = null;
                //Get the next matching track element from the race track
                do {
                    segementCounter++;
                    nextTrackElement = race.getTrack().get(segementCounter);

                } while (nextTrackElement.getClass().equals(newTrackElement));

                LOGGER.info("Recognsied TrackElement: " + nextTrackElement.toString());
                fireOptimizerEvents(nextTrackElement, segementCounter);

                MonitoringMessage monitoringMessage = new MonitoringMessage(nextTrackElement);
                ServiceManager.getInstance().getMessageDispatcher().sendMessage(monitoringMessage);

                nextTrackElement.getPositions().put(power, end - timeRoundBegin);

                currentTrackElement = nextTrackElement;
                startTrackElement = end;
            }
        }
    }


    private void fireOptimizerEvents(TrackElement trackElement, int elementPosition) {

        for (Optimizer optimizer : optimizers) {
            optimizer.onTrackElementChange(trackElement, elementPosition);
        }


    }


    public void onPenalyMessage(PenaltyMessage message) {
        for (Optimizer optimizer : optimizers) {
            optimizer.onPenalityEvent(message);
        }

        SpeedMeasureTrackElement element = race.getSpeedMeasureTrackElements().get(message.getBarrier());
        element.setSpeedLimit(message.getSpeedLimit());
    }

    public void onVelocityMessage(VelocityMessage message) {

        String sourceId = "";
        try {
            Field field = message.getClass().getDeclaredField("sourceId");
            field.setAccessible(true);
            sourceId = (String) field.get(message);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        long end = message.getTimeStamp();
        SpeedMeasureTrackElement element = race.getSpeedMeasureTrackElements().get(sourceId);
        //SpeedMeasureTrackElement element = race.getSpeedMeasureTrackElements().get(velocityCounter);

        element.getSpeeds().put(power, message.getVelocity());
        element.getPositions().put(power, end - timeRoundBegin);
        element.setLastSpeed(message.getVelocity());

        velocityCounter++;

        LOGGER.info("Updated SpeedMeasureTrackElement: " + element.toString());

        MonitoringMessage monitoringMessage = new MonitoringMessage(element);
        ServiceManager.getInstance().getMessageDispatcher().sendMessage(monitoringMessage);


    }

    @Override
    public void onNewPower(int power) {
        //TODO: BUG: power is set back to 100 after acceleration. => Wrong position and distance data for key 100
        this.power = power;
    }
}
