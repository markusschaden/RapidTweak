package com.zuehlke.carrera.javapilot.akka.rapidtweak.track;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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

    protected Multimap<Integer, Long> durations = ArrayListMultimap.create();
    protected List<SpeedMeasureTrackElement> speedMeasureTrackElements = new ArrayList<>();
    protected List<Integer> speeds = new ArrayList<>();
    protected boolean penaltyOccured;
    protected double latestDuration;

    public Double getAverageDuration(int power) {

        return getAverageOfList(durations.get(power));
    }

}
