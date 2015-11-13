package com.zuehlke.carrera.javapilot.akka.rapidtweak.optimizer;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.MonitoringMessage;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.RacePositionMessage;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.coordinates.TrackCoordinateCalculator;
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

import java.util.ArrayList;
import java.util.Date;
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
    private TrackCoordinateCalculator trackCoordinateCalculator;
    private PositionUpdateThread positionUpdateThread;
    private SpeedMeasureTrackElement currentSpeedMeasureElement;

    public TrackOptimizer(Race race) {
        this.race = race;
        StraightOptimizer straightOptimizer = new StraightOptimizer(race);
        HalfCurveOptimizer halfCurveOptimizer = new HalfCurveOptimizer(race);
        ExperimentOptimizer experimentOptimizer = new ExperimentOptimizer(race);
        ExperimentCurveOptimizer curveExperimentOptimizer = new ExperimentCurveOptimizer(race);
        straightOptimizer.setActive(true);
        heuristicElements = new HeuristicElements();

        //optimizers.add(straightOptimizer);
        //optimizers.add(halfCurveOptimizer);
        optimizers.add(experimentOptimizer);
        optimizers.add(curveExperimentOptimizer);

        this.trackCoordinateCalculator = new TrackCoordinateCalculator();
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

    public void optimize() {
        currentTrackElement = race.getTrack().get(segementCounter);
        startTrackElement = new Date().getTime();
        timeRoundBegin = new Date().getTime();
        velocityCounter = 0;
    }






    public void onRoundTimeMessage(RoundTimeMessage message) {
        timeRoundBegin = message.getTimestamp();

        com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.RoundTimeMessage roundTimeMessage = new com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.RoundTimeMessage();
        roundTimeMessage.setRoundTime(message.getRoundDuration());
        //roundTimeMessage.setRace(race);
        ServiceManager.getInstance().getMessageDispatcher().sendMessage(roundTimeMessage);

        trackCoordinateCalculator = new TrackCoordinateCalculator();
    }


    public void onSensorEvent(SensorEvent sensorEvent) {
        trackCoordinateCalculator.calculatePosition(sensorEvent);
        if (positionUpdateThread == null) {
            positionUpdateThread = new PositionUpdateThread();
            positionUpdateThread.start();
        }

        if (currentTrackElement != null) {

            TrackElement newTrackElement = (heuristicElements.getHeuristicElement(sensorEvent.getG()[2]));

            if (newTrackElement != null && !currentTrackElement.getClass().equals(newTrackElement.getClass())) {

                long end = sensorEvent.getTimeStamp();

                if (currentTrackElement.getBegin() < 1) {
                    currentTrackElement.getDurations().add(new Duration(power, end - startTrackElement));
                    currentTrackElement.setLatestDuration(end - startTrackElement);
                } else {
                    currentTrackElement.getDurations().add(new Duration(power, end - currentTrackElement.getBegin()));
                    currentTrackElement.setLatestDuration(end - currentTrackElement.getBegin());
                    currentTrackElement.setBegin(0);
                }
                MonitoringMessage monitoringMessage = new MonitoringMessage(currentTrackElement);
                ServiceManager.getInstance().getMessageDispatcher().sendMessage(monitoringMessage);


                TrackElement nextTrackElement = null;
                do {
                    segementCounter++;
                    nextTrackElement = race.getTrack().get(segementCounter);

                } while (nextTrackElement.getClass().equals(newTrackElement));

                LOGGER.info("Recognsied TrackElement: " + nextTrackElement.toString());
                fireOptimizerEvents(nextTrackElement, segementCounter);

                //MonitoringMessage monitoringMessage2 = new MonitoringMessage(nextTrackElement);
                //ServiceManager.getInstance().getMessageDispatcher().sendMessage(monitoringMessage2);

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

        //SpeedMeasureTrackElement element = race.getSpeedMeasureTrackElements().get(message.getBarrier());
        SpeedMeasureTrackElement element = currentSpeedMeasureElement;
        element.setSpeedLimit(message.getSpeedLimit());

        com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.PenaltyMessage penaltyMessage = new com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.PenaltyMessage(message.getBarrier());
        ServiceManager.getInstance().getMessageDispatcher().sendMessage(penaltyMessage);

    }

    public void onVelocityMessage(VelocityMessage message) {

        String sourceId = "";
        /*try {
            Field field = message.getClass().getDeclaredField("sourceId");
            field.setAccessible(true);
            sourceId = (String) field.get(message);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }*/

        int sizeSpeedMeasure = race.getSpeedMeasureTrackElements().size();
        sourceId = "" + velocityCounter;
        velocityCounter = (velocityCounter + 1) % sizeSpeedMeasure;

        long end = message.getTimeStamp();
        SpeedMeasureTrackElement element = race.getSpeedMeasureTrackElements().get(sourceId);
        //SpeedMeasureTrackElement element = race.getSpeedMeasureTrackElements().get(velocityCounter);

        element.getSpeeds().add(message.getVelocity());
        element.getPositions().put(power, end - timeRoundBegin);
        element.setLastSpeed(message.getVelocity());

        currentSpeedMeasureElement = element;
        //velocityCounter++;

        LOGGER.info("Updated SpeedMeasureTrackElement: " + element.toString());

        MonitoringMessage monitoringMessage = new MonitoringMessage(element);
        ServiceManager.getInstance().getMessageDispatcher().sendMessage(monitoringMessage);
    }

    @Override
    public void onNewPower(int power) {
        //TODO: BUG: power is set back to 100 after acceleration. => Wrong position and distance data for key 100
        this.power = power;
    }


    class PositionUpdateThread extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                RacePositionMessage racePositionMessage = new RacePositionMessage(trackCoordinateCalculator.getLastPosition());
                ServiceManager.getInstance().getMessageDispatcher().sendMessage(racePositionMessage);
            }
        }
    }
}
