package com.zuehlke.carrera.javapilot.akka.rapidtweak.power;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.zuehlke.carrera.javapilot.akka.PowerAction;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.PowerMessage;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markus on 25.09.2015.
 */
public class PowerService {

    private final Logger LOGGER = LoggerFactory.getLogger(PowerService.class);

    private ActorRef pilot;
    private UntypedActor actor;
    private List<PowerNotifier> powerNotifierList = new ArrayList<>();
    private PowerThread powerThread;

    public void init(ActorRef pilot, UntypedActor actor) {
        this.pilot = pilot;
        this.actor = actor;
    }

    public void setPowerThread(PowerThread powerThread) {
        if (this.powerThread != null) {
            this.powerThread.cancel();
        }
        this.powerThread = powerThread;

        powerThread.start();
    }

    public void setPower(int power) {
        //LOGGER.info("Set Power to: " + power);
        for (PowerNotifier powerNotifier : powerNotifierList) {
            powerNotifier.onNewPower(power);
        }
        pilot.tell(new PowerAction(power), actor.getSelf());
        ServiceManager.getInstance().getMessageDispatcher().sendMessage(new PowerMessage(power));
    }

    public void addPowerNotifier(PowerNotifier powerNotifier) {
        this.powerNotifierList.add(powerNotifier);
    }

    public void reset() {
        this.powerNotifierList.clear();
    }
}
