package com.zuehlke.carrera.javapilot.akka.rapidtweak;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.zuehlke.carrera.javapilot.akka.Configuration;
import com.zuehlke.carrera.javapilot.akka.PowerAction;
import com.zuehlke.carrera.javapilot.akka.experimental.ThresholdConfiguration;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.routing.RoutingService;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Race;
import com.zuehlke.carrera.relayapi.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.zuehlke.carrera.javapilot.akka.Configuration.START_VELOCITY;

/**
 * A very simple actor that determines the power value by a configurable Threshold on any of the 10 observables
 */
public class Speeeeeeeeedyyyyyyy extends UntypedActor {

    private int power;
    private ActorRef pilot;

    private final Logger LOGGER = LoggerFactory.getLogger(Speeeeeeeeedyyyyyyy.class);

    private RoutingService routingService;

    public Speeeeeeeeedyyyyyyy(ActorRef pilot) {
        this.pilot = pilot;
        this.power = START_VELOCITY;
        routingService = new RoutingService(pilot, this);
    }

    public static Props props(ActorRef pilot) {
        return Props.create(Speeeeeeeeedyyyyyyy.class, () -> new Speeeeeeeeedyyyyyyy(pilot));
    }

    private int trackElementCounter = 0;

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof SensorEvent) {
            routingService.onSensorEvent((SensorEvent)message);
        } else if (message instanceof VelocityMessage) {
            routingService.onVelocityMessage((VelocityMessage)message);
        } else if (message instanceof PenaltyMessage) {
            routingService.onPenalyMessage((PenaltyMessage) message);
        } else if (message instanceof RoundTimeMessage) {
            routingService.onRoundTimeMessage((RoundTimeMessage)message);
        } else if (message instanceof RaceStartMessage){

            pilot.tell(new PowerAction(100), getSelf());
        } else {

        }
    }

}
