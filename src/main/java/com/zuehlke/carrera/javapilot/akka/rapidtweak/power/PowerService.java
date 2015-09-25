package com.zuehlke.carrera.javapilot.akka.rapidtweak.power;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.zuehlke.carrera.javapilot.akka.PowerAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Markus on 25.09.2015.
 */
public class PowerService {

    private final Logger LOGGER = LoggerFactory.getLogger(PowerService.class);

    private static PowerService instance;
    private ActorRef pilot;
    private UntypedActor actor;

    private PowerService() {

    }

    public void init(ActorRef pilot, UntypedActor actor) {
        this.pilot = pilot; this.actor = actor;
    }

    public void setPower(int power) {
        LOGGER.info("Set Power to: " + power);
        pilot.tell(new PowerAction(power), actor.getSelf());
    }

    public static PowerService getInstance() {
        if (instance == null) {
            synchronized (PowerService.class) {
                if (instance == null) {
                    instance = new PowerService();
                }
            }
        }
        return instance;
    }


}
