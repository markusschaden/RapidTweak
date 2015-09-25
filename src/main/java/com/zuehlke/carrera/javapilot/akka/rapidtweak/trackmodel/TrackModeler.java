package com.zuehlke.carrera.javapilot.akka.rapidtweak.trackmodel;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.power.PowerNotifier;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Race;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.SpeedMeasureTrackElement;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.TrackElement;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import com.zuehlke.carrera.relayapi.messages.VelocityMessage;
import lombok.Data;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Markus on 25.09.2015.
 */
@Data
@ToString
public class TrackModeler implements PowerNotifier {

    private final Logger LOGGER = LoggerFactory.getLogger(TrackModeler.class);

    private TrackElement currentTrackElement;
    private Race race;
    private HeuristicElements heuristicElements;
    private long startTrackElement;
    private int power;
    private long timeRoundBegin;
    private ModelerStatus modelerStatus = ModelerStatus.UNDEFINED;

    public TrackModeler(Race race) {
        this.race = race;
        heuristicElements = new HeuristicElements();
    }

    public void onSensorEvent(SensorEvent sensorEvent) {

        if (currentTrackElement == null) {
            currentTrackElement = (heuristicElements.getHeuristicElement(sensorEvent.getG()[2]));
            startTrackElement = sensorEvent.getTimeStamp();
            currentTrackElement.getPositions().put(power, 0L);
        }

        TrackElement newTrackElement = (heuristicElements.getHeuristicElement(sensorEvent.getG()[2]));

        if (newTrackElement != null && !currentTrackElement.getClass().equals(newTrackElement.getClass())) {


            long end = sensorEvent.getTimeStamp();
            newTrackElement.getPositions().put(power, end - timeRoundBegin);
            currentTrackElement.getDurations().put(power, end - startTrackElement);
            race.getTrack().add(currentTrackElement);

            LOGGER.info("Added TrackElement: " + currentTrackElement.toString());

            currentTrackElement = newTrackElement;

            startTrackElement = end;
        }
    }


    public void onRoundTimeMessage(RoundTimeMessage roundTimeMessage) {
        timeRoundBegin = roundTimeMessage.getTimestamp();

        switch (modelerStatus) {

            case UNDEFINED:
                modelerStatus = ModelerStatus.RUNNING;
                LOGGER.info("Starting TrackModeler | modelerStatus: " + modelerStatus);
                break;


            case RUNNING:
                //Add Last track element
                long end = roundTimeMessage.getTimestamp();
                currentTrackElement.getDurations().put(power, end - startTrackElement);
                race.getTrack().add(currentTrackElement);

                LOGGER.info("Added TrackElement: " + currentTrackElement.toString());
                currentTrackElement = null;
                startTrackElement = end;


                modelerStatus = ModelerStatus.STOPPED;
                LOGGER.info("Stopping TrackModeler | modelerStatus: " + modelerStatus);


                break;
        }
    }


    public void onVelocityMessage(VelocityMessage velocityMessage) {

        switch (modelerStatus) {

            case RUNNING:

                long end = velocityMessage.getTimeStamp();

                SpeedMeasureTrackElement speedMeasureTrackElement = new SpeedMeasureTrackElement();
                speedMeasureTrackElement.getSpeeds().put(power, velocityMessage.getVelocity());
                speedMeasureTrackElement.getPositions().put(power, end - timeRoundBegin);
                race.getSpeedMeasureTrackElements().add(speedMeasureTrackElement);

                LOGGER.info("Added SpeedMeasureTrackElement: " + speedMeasureTrackElement.toString());


                break;
        }


    }


    @Override
    public void onNewPower(int power) {
        this.power = power;
    }
}
