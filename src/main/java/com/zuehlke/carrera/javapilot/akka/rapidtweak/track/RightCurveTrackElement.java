package com.zuehlke.carrera.javapilot.akka.rapidtweak.track;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Markus on 25.09.2015.
 */
@Data
@ToString(callSuper = true)
public class RightCurveTrackElement extends TrackElement {

    private final static String ELEMENT_NAME = "Right curve ";
    //TODO: reset on new start
    private static int elementCounter = 1;

    public String getTrackName() {
        return ELEMENT_NAME + elementCounter++;
    }

    public static void resetCounter() {
        elementCounter = 0;
    }
}
