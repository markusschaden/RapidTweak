package com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString(callSuper = true)
public class RaceDrawerMessage extends Message {

    private List<Coordinate> track = new ArrayList<>();

    public RaceDrawerMessage(List<Double[]> track) {
        track.forEach(c -> this.track.add(new Coordinate(c)));
    }
}
