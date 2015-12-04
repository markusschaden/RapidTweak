package com.zuehlke.carrera.javapilot.akka.rapidtweak.state;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Race;
import com.zuehlke.carrera.relayapi.messages.PenaltyMessage;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import com.zuehlke.carrera.relayapi.messages.VelocityMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created by Markus on 03.12.2015.
 */
public class StateHandler implements StateCallback {

    private final Logger LOGGER = LoggerFactory.getLogger(StateHandler.class);

    State currentState;
    StateType currentStateType;
    Context context;
    HashMap<StateType, State> states = new HashMap<>();

    public StateHandler(Context context) {
        this.context = context;

        states.put(StateType.EXPERIMENT, new Experiment(context, this));
        states.put(StateType.LUCKYPUNCH, new LuckyPunch(this));
        states.put(StateType.SPEEDUP, new SpeedUp(context, this));
        states.put(StateType.ROUNDDETECTION, new Rounddetection(context, this));

        setState(StateType.SPEEDUP);
    }

    @Override
    public void setState(StateType state) {
        if (state == StateType.RESET) {
            LOGGER.warn("Reset");
            synchronized (context) {
                context.setRace(new Race());
                states.put(StateType.ROUNDDETECTION, new Rounddetection(context, this));
                setState(StateType.ROUNDDETECTION);
            }
            return;
        }

        LOGGER.info("State changed from: " + currentStateType + " to: " + state);
        currentStateType = state;
        currentState = states.get(state);
    }

    public void onPenaltyMessage(PenaltyMessage message) {
        if (currentState != null) currentState.onPenaltyMessage(message);
    }

    public void onVelocityMessage(VelocityMessage message) {
        if (currentState != null) currentState.onVelocityMessage(message);
    }

    public void onSensorEvent(SensorEvent event) {
        if (currentState != null) currentState.onSensorEvent(event);
    }

    public void onRoundTimeMessage(RoundTimeMessage message) {
        if (currentState != null) currentState.onRoundTimeMessage(message);
    }

}
