package com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Markus on 20.10.2015.
 */
@Data
@ToString(callSuper = true)
public class RacePositionMessage extends Message {

    Coordinate position;

    public RacePositionMessage(Double[] position) {
        this.position = new Coordinate(position);
    }
}
