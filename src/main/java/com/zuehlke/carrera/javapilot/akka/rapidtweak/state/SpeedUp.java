package com.zuehlke.carrera.javapilot.akka.rapidtweak.state;

import com.zuehlke.carrera.javapilot.akka.Configuration;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.service.ServiceManager;
import com.zuehlke.carrera.relayapi.messages.PenaltyMessage;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import com.zuehlke.carrera.relayapi.messages.VelocityMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Markus on 03.12.2015.
 */
public class SpeedUp implements State {

    private final Logger LOGGER = LoggerFactory.getLogger(SpeedUp.class);

    StateCallback callback;
    Context context;
    private int power = Configuration.START_VELOCITY;

    public SpeedUp(Context context, StateCallback callback) {
        this.callback = callback;
        this.context = context;

        ServiceManager.getInstance().getPowerService().setPower(Configuration.START_VELOCITY);
    }

    @Override
    public void onSensorEvent(SensorEvent sensorEvent) {

    }

    @Override
    public void onVelocityMessage(VelocityMessage velocityMessage) {
        if (velocityMessage != null) {
            if (velocityMessage.getVelocity() < 200) {
                power += 5;
                LOGGER.info("Speed to low, set power to " + power);
                ServiceManager.getInstance().getPowerService().setPower(power);

            } else {
                context.setMinimumPower(power);
                callback.setState(StateType.ROUNDDETECTION);
            }
        }
    }

    @Override
    public void onPenaltyMessage(PenaltyMessage penaltyMessage) {

    }

    @Override
    public void onRoundTimeMessage(RoundTimeMessage roundTimeMessage) {

    }
}
