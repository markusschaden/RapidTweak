package com.zuehlke.carrera.javapilot.akka.rapidtweak.optimizer;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.TrackElement;
import com.zuehlke.carrera.relayapi.messages.PenaltyMessage;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;

/**
 * Created by Markus on 22.09.2015.
 */
public interface Optimizer {

    void onPenalityEvent(PenaltyMessage message);
    void onTrackElementChange(TrackElement trackElement, int position);
    void setActive(boolean active);
}
