package com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Markus on 09.10.2015.
 */
@Data
@ToString
public class PowerMessage extends Message {
    int power;

    public PowerMessage(int power) {
        this.power = power;
    }
}
