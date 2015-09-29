package com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Race;
import lombok.Data;

@Data
public class RoundTimeMessage extends Message {

    long roundTime;
    Race race;
}
