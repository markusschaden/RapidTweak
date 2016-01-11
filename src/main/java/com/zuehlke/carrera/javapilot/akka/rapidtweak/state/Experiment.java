package com.zuehlke.carrera.javapilot.akka.rapidtweak.state;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.MonitoringMessage;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.RacePositionMessage;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.coordinates.TrackCoordinateCalculator;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.optimizer.ExperimentCurveOptimizer;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.optimizer.ExperimentOptimizer;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.optimizer.Optimizer;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.service.ServiceManager;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.SpeedMeasureTrackElement;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.StraightTrackElement;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.TrackElement;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.trackmodel.HeuristicElements;
import com.zuehlke.carrera.relayapi.messages.PenaltyMessage;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import com.zuehlke.carrera.relayapi.messages.VelocityMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Experiment implements State {

    private final Logger LOGGER = LoggerFactory.getLogger(Experiment.class);

    private List<Optimizer> optimizers = new ArrayList<>();
    private TrackElement currentTrackElement;
    private HeuristicElements heuristicElements = new HeuristicElements();
    private int power;
    private int segementCounter = 0;
    private long timeRoundBegin;
    private int velocityCounter = 0;
    private TrackCoordinateCalculator trackCoordinateCalculator = new TrackCoordinateCalculator();
    private PositionUpdateThread positionUpdateThread;
    private SpeedMeasureTrackElement currentSpeedMeasureElement;


    StateCallback callback;
    Context context;
    private boolean penaltyOccured = false;
    private int penaltyTimeMilliSecond = 0;

    public Experiment(Context context, StateCallback callback) {
        this.context = context;
        this.callback = callback;

        ExperimentOptimizer experimentOptimizer = new ExperimentOptimizer(context.getRace());
        ExperimentCurveOptimizer curveExperimentOptimizer = new ExperimentCurveOptimizer(context.getRace());
        optimizers.add(experimentOptimizer);
        optimizers.add(curveExperimentOptimizer);
    }

    @Override
    public void onSensorEvent(SensorEvent sensorEvent) {
        trackCoordinateCalculator.calculatePosition(sensorEvent);
        if (positionUpdateThread == null) {
            positionUpdateThread = new PositionUpdateThread();
            //positionUpdateThread.start();
        }
        if (currentTrackElement == null) {
            currentTrackElement = context.getCurrentTrackElement();
        }

        if (currentTrackElement != null) {
            TrackElement newTrackElement = (heuristicElements.getHeuristicElement(sensorEvent.getG()[2]));

            if (newTrackElement != null && !currentTrackElement.getClass().equals(newTrackElement.getClass())) {

                if (penaltyOccured) {

                    currentTrackElement.calculateDurationWithPenalty(sensorEvent.getTimeStamp(), penaltyTimeMilliSecond);
                    penaltyTimeMilliSecond = 0;
                    penaltyOccured = false;
                } else {
                    currentTrackElement.calculateDuration(sensorEvent.getTimeStamp());
                }

                //recognize the correct current track element
                long latestDuration = currentTrackElement.getLatestDuration();
                int maxTries = context.getRace().getTrack().size();
                if (currentTrackElement instanceof StraightTrackElement && currentTrackElement.getDurations().get(0).getTime() > 500 && currentTrackElement.getDurations().size() > 2) {
                    if (Math.abs(currentTrackElement.getLatestDuration() - currentTrackElement.getDurations().get(currentTrackElement.getDurations().size() - 2).getTime()) > 500) {
                        if (penaltyOccured) {
                            penaltyOccured = false;
                        } else {
                            LOGGER.warn("Wrong current track element");
                            int counter = 0;
                            int index = segementCounter;
                            do {
                                currentTrackElement = context.getRace().getTrack().get(index);
                                if (index > 0) {
                                    index = -index;
                                } else {
                                    index = -index;
                                    index++;
                                }
                                LOGGER.debug("Check trackelement id: " + index + ": " + currentTrackElement);

                                if (counter == maxTries) {
                                    LOGGER.error("Couldn't match track element, skip");
                                    break;
                                }
                                counter++;
                            }
                            while (Math.abs(latestDuration - currentTrackElement.getDurations().get(currentTrackElement.getDurations().size() - 2).getTime()) > 500);
                            LOGGER.info("New current trackelement: " + currentTrackElement);
                        }
                    }
                }

                MonitoringMessage monitoringMessage = new MonitoringMessage(currentTrackElement);
                ServiceManager.getInstance().getMessageDispatcher().sendMessage(monitoringMessage);


                TrackElement nextTrackElement;
                do {
                    segementCounter++;
                    nextTrackElement = context.getRace().getTrack().get(segementCounter);

                } while (nextTrackElement.getClass().equals(newTrackElement));

                //LOGGER.info("Recognized TrackElement: " + nextTrackElement.toString());
                fireOptimizerEvents(nextTrackElement, segementCounter);

                //MonitoringMessage monitoringMessage2 = new MonitoringMessage(nextTrackElement);
                //ServiceManager.getInstance().getMessageDispatcher().sendMessage(monitoringMessage2);

                //nextTrackElement.getPositions().put(power, end - timeRoundBegin);

                currentTrackElement = nextTrackElement;
                currentTrackElement.setStartTimestamp(sensorEvent.getTimeStamp());
            }
        } else {
            LOGGER.error("Current track element null");
        }
    }

    private void fireOptimizerEvents(TrackElement trackElement, int elementPosition) {

        if (!penaltyOccured) {
            for (Optimizer optimizer : optimizers) {
                optimizer.onTrackElementChange(trackElement, elementPosition);
            }
        }
    }


    @Override
    public void onVelocityMessage(VelocityMessage message) {
        if (context.getRace().getSpeedMeasureTrackElements().size() == 0) return;
        int sizeSpeedMeasure = context.getRace().getSpeedMeasureTrackElements().size();
        String sourceId = "" + velocityCounter;
        velocityCounter = (velocityCounter + 1) % sizeSpeedMeasure;

        long end = message.getTimeStamp();
        SpeedMeasureTrackElement element = context.getRace().getSpeedMeasureTrackElements().get(sourceId);
        //SpeedMeasureTrackElement element = race.getSpeedMeasureTrackElements().get(velocityCounter);

        element.getSpeeds().add(message.getVelocity());
        element.getPositions().put(power, end - timeRoundBegin);
        element.setLastSpeed(message.getVelocity());

        currentSpeedMeasureElement = element;
        //velocityCounter++;

        //LOGGER.info("Updated SpeedMeasureTrackElement: " + element.toString());

        MonitoringMessage monitoringMessage = new MonitoringMessage(element);
        ServiceManager.getInstance().getMessageDispatcher().sendMessage(monitoringMessage);
    }

    @Override
    public void onPenaltyMessage(PenaltyMessage message) {
        for (Optimizer optimizer : optimizers) {
            optimizer.onPenalityEvent(message);
        }

        //SpeedMeasureTrackElement element = race.getSpeedMeasureTrackElements().get(message.getBarrier());
        SpeedMeasureTrackElement element = currentSpeedMeasureElement;
        element.setSpeedLimit(message.getSpeedLimit());

        com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.PenaltyMessage penaltyMessage = new com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.PenaltyMessage(message.getBarrier());
        ServiceManager.getInstance().getMessageDispatcher().sendMessage(penaltyMessage);

        penaltyTimeMilliSecond = message.getPenalty_ms();
        penaltyOccured = true;
    }

    @Override
    public void onRoundTimeMessage(RoundTimeMessage message) {
        timeRoundBegin = message.getTimestamp();

        com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.RoundTimeMessage roundTimeMessage = new com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.RoundTimeMessage();
        roundTimeMessage.setRoundTime(message.getRoundDuration());
        //roundTimeMessage.setRace(race);
        ServiceManager.getInstance().getMessageDispatcher().sendMessage(roundTimeMessage);

        trackCoordinateCalculator = new TrackCoordinateCalculator();
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
