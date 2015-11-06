package com.zuehlke.carrera.javapilot.akka.rapidtweak;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.zuehlke.carrera.javapilot.akka.PowerAction;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.routing.RoutingService;
import com.zuehlke.carrera.relayapi.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            routingService.onPenaltyMessage((PenaltyMessage) message);
        } else if (message instanceof RoundTimeMessage) {
            routingService.onRoundTimeMessage((RoundTimeMessage)message);
        } else if (message instanceof RaceStopMessage) {
            routingService.onRaceStop((RaceStopMessage) message);
        } else if (message instanceof RaceStartMessage){
            pilot.tell(new PowerAction(START_VELOCITY), getSelf());
            routingService.onRaceStart((RaceStartMessage) message);
        } else {

        }
    }

}
