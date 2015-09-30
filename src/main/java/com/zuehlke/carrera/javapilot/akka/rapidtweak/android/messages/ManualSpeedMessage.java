package com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ManualSpeedMessage extends Message {

    int speed;
}
