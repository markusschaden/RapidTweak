package com.zuehlke.carrera.javapilot.akka.rapidtweak;

import com.zuehlke.carrera.javapilot.akka.Configuration;
import com.zuehlke.carrera.simulator.model.racetrack.FloatingAverage;

/**
 * Created by Markus on 21.09.2015.
 */
public abstract class TrackElement {

    protected FloatingAverage floatingAverage = new FloatingAverage(Configuration.NUMBER_OF_ROUND_PER_VELOCITY);

    public double getTime() {
        return floatingAverage.currentAverage();
    }

    @Override
    public String toString() {
        return "TrackElement{" +
                "time=" + getTime() +
                '}';
    }
}
