package com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Race;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RoundTimeMessage extends Message {

    long roundTime;
    Race race;
}
