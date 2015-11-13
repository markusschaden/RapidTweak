package com.zuehlke.carrera.javapilot.akka.rapidtweak.trackmodel;

import com.zuehlke.carrera.javapilot.akka.Configuration;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.MonitoringMessage;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.RaceDrawerMessage;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.coordinates.TrackCoordinateCalculator;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.power.PowerNotifier;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.race.RaceStatus;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.routing.ChangeRaceStatus;
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
    private int power = Configuration.START_VELOCITY;
    private long timeRoundBegin;
    private ModelerStatus modelerStatus = ModelerStatus.UNDEFINED;
    private TrackCoordinateCalculator trackCoordinateCalculator;
    private int speedElementIndex = 0;
    private RoundDetection roundDetection = new RoundDetection();
    private boolean first = true;
    private ChangeRaceStatus changeRaceStatus;

    public TrackModeler(Race race, TrackCoordinateCalculator trackCoordinateCalculator, ChangeRaceStatus changeRaceStatus) {
        this.race = race;
        heuristicElements = new HeuristicElements();
        this.trackCoordinateCalculator = trackCoordinateCalculator;
        this.changeRaceStatus = changeRaceStatus;
    }

    public void onSensorEvent(SensorEvent sensorEvent) {

        if (modelerStatus == ModelerStatus.ROUNDDETECTION) {


            //case RUNNING:
            trackCoordinateCalculator.onSensorEvent(sensorEvent);


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
                if (end - startTrackElement > 200) {
                    //ignore the first track element after round detection phase has started
                    if (first) {
                        first = false;
                    } else {
                        race.getTrack().add(currentTrackElement);
                        //LOGGER.info("Added TrackElement: " + currentTrackElement.toString());
                        MonitoringMessage monitoringMessage = new MonitoringMessage(currentTrackElement);
                        ServiceManager.getInstance().getMessageDispatcher().sendMessage(monitoringMessage);

                        if (roundDetection.isRound(race)) {
                            modelerStatus = ModelerStatus.STOPPED;
                            roundDetection.createRoundTrack(race);
                            changeRaceStatus.changeRaceStatus(RaceStatus.RACE);
                        }
                    }
                    startTrackElement = end;
                } else {
                    //LOGGER.info("Ignored Element" + currentTrackElement.toString());
                }
                currentTrackElement = newTrackElement;


            }

        }
    }


    public void onRoundTimeMessage(RoundTimeMessage roundTimeMessage) {
        if (true)
            return;

        timeRoundBegin = roundTimeMessage.getTimestamp();

        switch (modelerStatus) {

            case RUNNING:
                //Add Last track element
                long end = roundTimeMessage.getTimestamp();
                if (race.getTrack().get(0).getClass().getCanonicalName().equals(currentTrackElement.getClass().getCanonicalName())) {

                    TrackElement beginElement = race.getTrack().get(0);
                    beginElement.setBegin(startTrackElement);
                    long firstRound = beginElement.getLatestDuration();
                    beginElement.getDurations().clear();
                    //beginElement.getDurations().add(new Duration(power, end - startTrackElement + firstRound));
                    //beginElement.setLatestDuration(end - startTrackElement + firstRound);

                    LOGGER.info("Merge start and end: " + beginElement.toString());

                    //MonitoringMessage monitoringMessage = new MonitoringMessage(beginElement);
                    //ServiceManager.getInstance().getMessageDispatcher().sendMessage(monitoringMessage);

                    RaceDrawerMessage raceDrawerMessage = new RaceDrawerMessage(trackCoordinateCalculator.getTrack());
                    ServiceManager.getInstance().getMessageDispatcher().sendMessage(raceDrawerMessage);
                } else {
                    //currentTrackElement.getDurations().add(new Duration(power, end - startTrackElement));
                    //currentTrackElement.setLatestDuration(end - startTrackElement);
                    currentTrackElement.updateTrackElementName();
                    currentTrackElement.setId();
                    race.getTrack().add(currentTrackElement);

                    LOGGER.info("Added TrackElement: " + currentTrackElement.toString());

                    //MonitoringMessage monitoringMessage = new MonitoringMessage(currentTrackElement);
                    //ServiceManager.getInstance().getMessageDispatcher().sendMessage(monitoringMessage);

                }

                //currentTrackElement = null;
                //startTrackElement = end;

                com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.RoundTimeMessage roundMessage = new com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.RoundTimeMessage();
                roundMessage.setRoundTime(roundTimeMessage.getRoundDuration());
                ServiceManager.getInstance().getMessageDispatcher().sendMessage(roundMessage);

                modelerStatus = ModelerStatus.STOPPED;
                LOGGER.info("Stopping TrackModeler | modelerStatus: " + modelerStatus);

                //TODO: add measurepoint to correct trackelement

                break;
        }
    }


    public void onVelocityMessage(VelocityMessage velocityMessage) {

        switch (modelerStatus) {

            case UNDEFINED:
                modelerStatus = ModelerStatus.SPEEDUP;
                LOGGER.info("Starting TrackModeler | modelerStatus: " + modelerStatus);

                checkLearningSpeed(velocityMessage);

                break;


            case SPEEDUP:
                checkLearningSpeed(velocityMessage);

                break;

            case ROUNDDETECTION:

                long end = velocityMessage.getTimeStamp();

                String sourceId = "" + speedElementIndex;
                speedElementIndex++;

                SpeedMeasureTrackElement speedMeasureTrackElement = new SpeedMeasureTrackElement();
                speedMeasureTrackElement.getSpeeds().add(velocityMessage.getVelocity());
                speedMeasureTrackElement.getPositions().put(power, end - timeRoundBegin);
                speedMeasureTrackElement.setLastSpeed(velocityMessage.getVelocity());
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

    private void checkLearningSpeed(VelocityMessage velocityMessage) {
        if (velocityMessage != null) {
            if (velocityMessage.getVelocity() < 200) {
                power += 10;
                LOGGER.info("Speed to low, set power to " + power);
                ServiceManager.getInstance().getPowerService().setPower(power);

            } else {
                modelerStatus = ModelerStatus.ROUNDDETECTION;
                LOGGER.info("TrackModeler | modelerStatus: " + modelerStatus);
            }
        }
    }


    @Override
    public void onNewPower(int power) {
        this.power = power;
    }
}
