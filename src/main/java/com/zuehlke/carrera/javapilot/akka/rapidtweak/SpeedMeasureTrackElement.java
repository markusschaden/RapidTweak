package com.zuehlke.carrera.javapilot.akka.rapidtweak;

/**
 * Created by Markus on 21.09.2015.
 */
public class SpeedMeasureTrackElement {

    private long position;
    private double velocity;

    @Override
    public String toString() {
        return "SpeedMeasureTrackElement{" +
                "position=" + position +
                ", velocity=" + velocity +
                '}';
    }

    public SpeedMeasureTrackElement(long position, double velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

}
