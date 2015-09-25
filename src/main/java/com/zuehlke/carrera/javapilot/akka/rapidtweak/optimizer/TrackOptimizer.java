package com.zuehlke.carrera.javapilot.akka.rapidtweak.optimizer;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.power.PowerNotifier;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.TrackElement;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Race;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.trackmodel.HeuristicElements;
import com.zuehlke.carrera.relayapi.messages.PenaltyMessage;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import com.zuehlke.carrera.relayapi.messages.VelocityMessage;
import lombok.Data;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.management.Sensor;

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

    public TrackOptimizer(Race race) {
        this.race = race;
        StraightOptimizer straightOptimizer = new StraightOptimizer(race);
        straightOptimizer.setActive(true);
        heuristicElements = new HeuristicElements();

        optimizers.add(straightOptimizer);
    }


    /***
     * Start optimizing
     * Must be called at the begin of a round
     */
    public void optimize(RoundTimeMessage message) {
        currentTrackElement = race.getTrack().get(segementCounter);
        startTrackElement = message.getTimestamp();
        timeRoundBegin = message.getTimestamp();
    }


    public void onRoundTimeMessage(RoundTimeMessage message) {
        timeRoundBegin = message.getTimestamp();
    }


    public void onSensorEvent(SensorEvent sensorEvent) {

        if (currentTrackElement != null) {

            TrackElement newTrackElement = (heuristicElements.getHeuristicElement(sensorEvent.getG()[2]));

            if (newTrackElement != null && !currentTrackElement.getClass().equals(newTrackElement.getClass())) {

                long end = sensorEvent.getTimeStamp();
                currentTrackElement.getDurations().put(power, end - startTrackElement);

                TrackElement nextTrackElement = null;
                //Get the next matching track element from the race track
                do {
                    segementCounter++;
                    nextTrackElement = race.getTrack().get(segementCounter);

                } while (nextTrackElement.getClass().equals(newTrackElement));

                LOGGER.info("Recognsied TrackElement: " + nextTrackElement.toString());
                fireOptimizerEvents(nextTrackElement, segementCounter);


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
    }

    public void onVelocityMessage(VelocityMessage message) {


    }

    @Override
    public void onNewPower(int power) {
        this.power = power;
    }
}
