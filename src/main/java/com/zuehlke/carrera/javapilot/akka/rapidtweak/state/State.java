package com.zuehlke.carrera.javapilot.akka.rapidtweak.state;

import com.zuehlke.carrera.relayapi.messages.PenaltyMessage;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import com.zuehlke.carrera.relayapi.messages.VelocityMessage;

/**
 * Created by Markus on 03.12.2015.
 */
public interface State {

    void onSensorEvent(SensorEvent sensorEvent);

    void onVelocityMessage(VelocityMessage velocityMessage);

    void onPenaltyMessage(PenaltyMessage penaltyMessage);

    void onRoundTimeMessage(RoundTimeMessage roundTimeMessage);
}
