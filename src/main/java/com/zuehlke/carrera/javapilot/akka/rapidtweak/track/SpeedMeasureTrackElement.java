package com.zuehlke.carrera.javapilot.akka.rapidtweak.track;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Data;
import lombok.ToString;

/**
 * Created by Markus on 25.09.2015.
 */
@Data
@ToString(callSuper = true)
public class SpeedMeasureTrackElement extends Element {

    protected Multimap<Integer, Double> speeds = ArrayListMultimap.create();


    public Double getAveragePosition(int power) {

        return getAverageOfListDouble(speeds.get(power));
    }
}
