package com.zuehlke.carrera.javapilot.akka.rapidtweak.trackmodel;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.MonitoringMessage;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.power.PowerNotifier;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.service.ServiceManager;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Duration;
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

import java.lang.reflect.Field;

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
            currentTrackElement.getDurations().add(new Duration(power, end - startTrackElement));
            currentTrackElement.setLatestDuration(end - startTrackElement);
            currentTrackElement.updateTrackElementName();
            currentTrackElement.setId();
            race.getTrack().add(currentTrackElement);

            LOGGER.info("Added TrackElement: " + currentTrackElement.toString());

            MonitoringMessage monitoringMessage = new MonitoringMessage(currentTrackElement);
            ServiceManager.getInstance().getMessageDispatcher().sendMessage(monitoringMessage);

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
                if (race.getTrack().get(0).getClass().getCanonicalName().equals(currentTrackElement.getClass().getCanonicalName())) {

                    TrackElement beginElement = race.getTrack().get(0);
                    double firstRound = beginElement.getLatestDuration();
                    beginElement.getDurations().clear();
                    beginElement.getDurations().add(new Duration(power, (long) (end - startTrackElement + firstRound)));
                    beginElement.setLatestDuration(end - startTrackElement + firstRound);

                    LOGGER.info("Merge start and end: " + beginElement.toString());

                    MonitoringMessage monitoringMessage = new MonitoringMessage(beginElement);
                    ServiceManager.getInstance().getMessageDispatcher().sendMessage(monitoringMessage);

                } else {
                    currentTrackElement.getDurations().add(new Duration(power, end - startTrackElement));
                    currentTrackElement.setLatestDuration(end - startTrackElement);
                    currentTrackElement.updateTrackElementName();
                    currentTrackElement.setId();
                    race.getTrack().add(currentTrackElement);

                    LOGGER.info("Added TrackElement: " + currentTrackElement.toString());

                    MonitoringMessage monitoringMessage = new MonitoringMessage(currentTrackElement);
                    ServiceManager.getInstance().getMessageDispatcher().sendMessage(monitoringMessage);

                }

                currentTrackElement = null;
                startTrackElement = end;

                modelerStatus = ModelerStatus.STOPPED;
                LOGGER.info("Stopping TrackModeler | modelerStatus: " + modelerStatus);

                //TODO: add measurepoint to correct trackelement

                break;
        }
    }


    public void onVelocityMessage(VelocityMessage velocityMessage) {

        switch (modelerStatus) {

            case RUNNING:

                long end = velocityMessage.getTimeStamp();

                String sourceId = "";
                try {
                    Field field = velocityMessage.getClass().getDeclaredField("sourceId");
                    field.setAccessible(true);
                    sourceId = (String) field.get(velocityMessage);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                SpeedMeasureTrackElement speedMeasureTrackElement = new SpeedMeasureTrackElement();
                speedMeasureTrackElement.getSpeeds().put(power, velocityMessage.getVelocity());
                speedMeasureTrackElement.getPositions().put(power, end - timeRoundBegin);
                speedMeasureTrackElement.updateTrackElementName();
                speedMeasureTrackElement.setSourceId(sourceId);
                speedMeasureTrackElement.setId();
                race.getSpeedMeasureTrackElements().put(sourceId, speedMeasureTrackElement);

                LOGGER.info("Added SpeedMeasureTrackElement: " + speedMeasureTrackElement.toString());

                MonitoringMessage monitoringMessage = new MonitoringMessage(speedMeasureTrackElement);
                ServiceManager.getInstance().getMessageDispatcher().sendMessage(monitoringMessage);

                break;
        }


    }


    @Override
    public void onNewPower(int power) {
        this.power = power;
    }
}
