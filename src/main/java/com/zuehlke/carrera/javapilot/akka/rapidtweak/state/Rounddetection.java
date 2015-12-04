package com.zuehlke.carrera.javapilot.akka.rapidtweak.state;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.MonitoringMessage;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.coordinates.TrackCoordinateCalculator;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.service.ServiceManager;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.SpeedMeasureTrackElement;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.TrackElement;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.trackmodel.HeuristicElements;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.trackmodel.RoundDetection;
import com.zuehlke.carrera.relayapi.messages.PenaltyMessage;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import com.zuehlke.carrera.relayapi.messages.VelocityMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Markus on 03.12.2015.
 */
public class Rounddetection implements State {

    private final Logger LOGGER = LoggerFactory.getLogger(Rounddetection.class);

    private int TIMEOUT_WATCHDOG_MODELER = 1000 * 100;
    private Watchdog watchdog;
    private RoundDetection roundDetection = new RoundDetection();
    private boolean first = true;
    private int speedElementIndex = 0;
    private TrackElement currentTrackElement;
    private HeuristicElements heuristicElements = new HeuristicElements();
    private TrackCoordinateCalculator trackCoordinateCalculator = new TrackCoordinateCalculator();

    StateCallback callback;
    Context context;


    public Rounddetection(Context context, StateCallback callback) {
        this.callback = callback;
        this.context = context;
    }

    boolean isReady = false;

    @Override
    public void onSensorEvent(SensorEvent sensorEvent) {
        if (watchdog == null) {
            watchdog = new Watchdog();
            watchdog.start();
        }

        trackCoordinateCalculator.onSensorEvent(sensorEvent);

        if (currentTrackElement == null) {

            currentTrackElement = (heuristicElements.getHeuristicElement(sensorEvent.getG()[2]));
            currentTrackElement.setStartTimestamp(sensorEvent.getTimeStamp());
            return;
        }

        //skip trackelement after speed barrier
        if (!isReady) {

            TrackElement newTrackElement = (heuristicElements.getHeuristicElement(sensorEvent.getG()[2]));

            if (newTrackElement != null && !currentTrackElement.getClass().equals(newTrackElement.getClass())) {
                isReady = true;
                currentTrackElement = newTrackElement;
                currentTrackElement.setStartTimestamp(sensorEvent.getTimeStamp());
            }
            return;
        }


        TrackElement newTrackElement = (heuristicElements.getHeuristicElement(sensorEvent.getG()[2]));

        if (newTrackElement != null && !currentTrackElement.getClass().equals(newTrackElement.getClass())) {

            long end = sensorEvent.getTimeStamp();

            currentTrackElement.calculateDuration(end);
            currentTrackElement.updateTrackElementName();
            currentTrackElement.setId();


            context.getRace().getTrack().add(currentTrackElement);

            if (currentTrackElement.getLatestDuration() > 200) {

                context.getRace().getFilteredTrack().add(currentTrackElement);
                //LOGGER.debug("Added TrackElement: " + currentTrackElement.toString());
                MonitoringMessage monitoringMessage = new MonitoringMessage(currentTrackElement);
                ServiceManager.getInstance().getMessageDispatcher().sendMessage(monitoringMessage);

                if (roundDetection.isRound(context.getRace())) {
                    watchdog.active = false;
                    roundDetection.createRoundTrack(context.getRace());

                    newTrackElement.setStartTimestamp(sensorEvent.getTimeStamp());
                    currentTrackElement = newTrackElement;
                    context.setCurrentTrackElement(currentTrackElement);
                    callback.setState(StateType.EXPERIMENT);

                    return;
                }

            } else {
                //LOGGER.info("Ignored Element" + currentTrackElement.toString());
            }
            newTrackElement.setStartTimestamp(sensorEvent.getTimeStamp());
            currentTrackElement = newTrackElement;

        }
    }

    @Override
    public void onVelocityMessage(VelocityMessage velocityMessage) {

        SpeedMeasureTrackElement speedMeasureTrackElement = new SpeedMeasureTrackElement();
        speedMeasureTrackElement.getSpeeds().add(velocityMessage.getVelocity());
        speedMeasureTrackElement.setLastSpeed(velocityMessage.getVelocity());
        speedMeasureTrackElement.updateTrackElementName();
        speedMeasureTrackElement.setId();
        context.getRace().getSpeedMeasureTrackElements().put("" + speedElementIndex, speedMeasureTrackElement);
        speedElementIndex++;

        //LOGGER.debug("Added SpeedMeasureTrackElement: " + speedMeasureTrackElement.toString());

        MonitoringMessage monitoringMessage = new MonitoringMessage(speedMeasureTrackElement);
        ServiceManager.getInstance().getMessageDispatcher().sendMessage(monitoringMessage);
    }

    @Override
    public void onPenaltyMessage(PenaltyMessage penaltyMessage) {

    }

    @Override
    public void onRoundTimeMessage(RoundTimeMessage roundTimeMessage) {

    }


    private class Watchdog extends Thread {

        public boolean active = true;

        @Override
        public void run() {
            try {
                Thread.sleep(TIMEOUT_WATCHDOG_MODELER);
                if (active) {
                    LOGGER.warn("Track couldn't be identified, delete track");
                    callback.setState(StateType.RESET);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}
