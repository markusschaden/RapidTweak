package com.zuehlke.carrera.javapilot.akka.rapidtweak;

/**
 * Created by Markus on 21.09.2015.
 */
public abstract class TrackElement {

    protected long time;

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "TrackElement{" +
                "time=" + time +
                '}';
    }
}
