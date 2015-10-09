package com.zuehlke.carrera.javapilot.akka.rapidtweak.track;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markus on 25.09.2015.
 */
@Data
@ToString(callSuper = true)
public abstract class TrackElement extends Element {

    protected List<Duration> durations = new ArrayList<>();
    protected List<SpeedMeasureTrackElement> speedMeasureTrackElements = new ArrayList<>();
    protected List<Integer> speeds = new ArrayList<>();
    protected boolean penaltyOccured;
    protected long latestDuration;
    private long begin;

    public long getBestTime() {
        long time = 999999;
        for (Duration t : durations) {
            if (t.getTime() < time) time = t.getTime();
        }
        return time;
    }

    public Double getAverageDuration() {

        Double result = 0d;
        if (durations.size() == 0) {
            return result;
        }

        int count = 0;
        for (Duration d : durations) {
            result += d.getTime();
            count++;
        }

        return result / count;

    }
}
