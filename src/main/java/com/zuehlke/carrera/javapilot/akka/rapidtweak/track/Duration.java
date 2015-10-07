package com.zuehlke.carrera.javapilot.akka.rapidtweak.track;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by Markus on 25.09.2015.
 */
@Data
@ToString
public class Duration implements Serializable {

    int power;
    long time;

    public Duration(int power, long time) {
        this.power = power;
        this.time = time;
    }
}
