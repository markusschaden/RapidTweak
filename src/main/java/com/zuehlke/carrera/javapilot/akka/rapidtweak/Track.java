package com.zuehlke.carrera.javapilot.akka.rapidtweak;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markus on 21.09.2015.
 */
public class Track {

    public int speed;
    public List<TrackElement> trackElements = new CircularArrayList<>();
    public List<SpeedMeasureTrackElement> speedTrackers = new ArrayList<>();

    public Track() {

    }

    public Track(int startVelocity) {
        this.speed = startVelocity;
    }


    @Override
    public String toString() {
        return "Track{" +
                "speed=" + speed +
                ", trackElements=" + trackElements +
                ", speedTrackers=" + speedTrackers +
                '}';
    }
}
