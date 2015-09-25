package com.zuehlke.carrera.javapilot.akka.rapidtweak.track;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Data;
import lombok.ToString;

import java.util.Collection;

/**
 * Created by Markus on 25.09.2015.
 */
@Data
@ToString(callSuper = true)
public abstract class Element {

    protected Multimap<Integer, Long> positions = ArrayListMultimap.create();

    public Double getAveragePosition(int power) {

        return getAverageOfList(positions.get(power));
    }

    protected Double getAverageOfList(Collection<Long> values) {

        Double result = 0d;
        if (values.size() == 0) {
            return result;
        }

        int count = 0;
        for (Long d : values) {
            result += d;
            count++;
        }

        return result / count;
    }

    protected Double getAverageOfListDouble(Collection<Double> values) {

        Double result = 0d;
        if (values.size() == 0) {
            return result;
        }

        int count = 0;
        for (Double d : values) {
            result += d;
            count++;
        }

        return result / count;
    }


}
