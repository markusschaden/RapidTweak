package com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Markus on 20.10.2015.
 */
@Data
@ToString
public class Coordinate {

    double x, y, z;

    public Coordinate(Double[] d) {
        x = d[0];
        y = d[1];
        z = d[2];
    }
}
