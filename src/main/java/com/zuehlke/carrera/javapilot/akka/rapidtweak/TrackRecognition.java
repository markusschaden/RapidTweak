package com.zuehlke.carrera.javapilot.akka.rapidtweak;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.zuehlke.carrera.javapilot.akka.Configuration;
import com.zuehlke.carrera.javapilot.akka.PowerAction;
import com.zuehlke.carrera.javapilot.akka.experimental.ThresholdConfiguration;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import com.zuehlke.carrera.relayapi.messages.VelocityMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A very simple actor that determines the power value by a configurable Threshold on any of the 10 observables
 */
public class TrackRecognition extends UntypedActor {

    private ThresholdConfiguration configuration;
    private int power;
    private ActorRef pilot;
    private boolean trace = false;

    private Map<Integer, List<SensorEvent>> data = new ConcurrentHashMap<>();
    int round = 0;
    private List<SensorEvent> events = new ArrayList<>();
    private final Logger LOGGER = LoggerFactory.getLogger(TrackRecognition.class);

    private TrackElement currentTrackElement = null;
    private long startTrackElement = 0;
    private long startSpeedMeasureElement = 0;
    private Track track = new Track();

    private HeuristicElements heuristicElements = new HeuristicElements();
    private boolean lastSpeedMeasure = false;

    public TrackRecognition(ActorRef pilot) {
        this.pilot = pilot;
        this.power = Configuration.START_VELOCITY;
    }

    public static Props props(ActorRef pilot) {
        return Props.create(TrackRecognition.class, () -> new TrackRecognition(pilot));
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof SensorEvent) {

            if (trace) {

                handleSensorEvent((SensorEvent) message);
                events.add((SensorEvent) message);

                if (currentTrackElement == null) {
                    currentTrackElement = (heuristicElements.getHeuristicElement(((SensorEvent) message).getG()[2]));
                    startTrackElement = ((SensorEvent) message).getTimeStamp();
                }

                TrackElement newTrackElement = (heuristicElements.getHeuristicElement(((SensorEvent) message).getG()[2]));

                if (newTrackElement != null && !currentTrackElement.getClass().equals(newTrackElement.getClass())) {
                    long end = ((SensorEvent) message).getTimeStamp();
                    currentTrackElement.setTime(end - startTrackElement);
                    track.trackElements.add(currentTrackElement);
                    currentTrackElement = newTrackElement;
                    startTrackElement = end;
                }

            }
        } else if (message instanceof VelocityMessage) {
            handleVelocityMessage((VelocityMessage) message);

        } else if (message instanceof RoundTimeMessage) {

            synchronized (TrackRecognition.this) {
                if (!trace) {
                    trace = true;
                    events.clear();
                } else {
                    data.put(round, events);
                    round++;
                    LOGGER.info("Round finish, list entries: " + events.size());
                    events = new ArrayList<>();
                    if (round <= Configuration.WARMUP_NUMBER) {
                        this.power += Configuration.WARMUP_VELOCITY_INCREASE;
                    }

                    finishTrack(message);
                }
            }
        } else {
            unhandled(message);
        }
    }

    private void finishTrack(Object message) {

        long end = ((RoundTimeMessage) message).getTimestamp();
        currentTrackElement.setTime(end - startTrackElement);
        track.trackElements.add(currentTrackElement);
        startTrackElement = end;

        if ((track.trackElements.get(0).getClass().equals(track.trackElements.get(track.trackElements.size() - 1).getClass()))) {
            //merge start and end
            LOGGER.info("Merge is necessary");

        }
        LOGGER.info(track.trackElements.toString());
        lastSpeedMeasure = true;

        track.trackElements.clear();
    }

    private void handleVelocityMessage(VelocityMessage message) {

        if(startSpeedMeasureElement == 0) {
            startSpeedMeasureElement = message.getTimeStamp();
        } else {
            long end = message.getTimeStamp();
            track.speedTrackers.add(new SpeedMeasureTrackElement(end-startSpeedMeasureElement, message.getVelocity()));
            startSpeedMeasureElement = end;

            if(lastSpeedMeasure) {
                lastSpeedMeasure = false;
                LOGGER.info(track.speedTrackers.toString());
                track.speedTrackers.clear();
            }

        }

    }

    private void handleSensorEvent(SensorEvent event) {
        pilot.tell(new PowerAction(power), getSelf());
    }
}
