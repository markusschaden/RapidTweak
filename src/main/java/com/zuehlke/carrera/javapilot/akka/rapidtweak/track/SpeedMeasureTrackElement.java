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
public class SpeedMeasureTrackElement extends Element {

    protected List<Double> speeds = new ArrayList<>();
    private final static String ELEMENT_NAME = "SpeedMeasure ";
    private static int elementCounter = 1;
    private double speedLimit;
    private double lastSpeed;
    //private String sourceId;

    public void updateTrackElementName() {
        elementName = ELEMENT_NAME + elementCounter++;
    }

    public static void resetCounter() {
        elementCounter = 1;
    }

    public Double getAveragePosition(int power) {

        return getAverageOfListDouble(speeds);
    }
}
