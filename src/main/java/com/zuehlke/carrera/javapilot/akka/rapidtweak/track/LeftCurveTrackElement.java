package com.zuehlke.carrera.javapilot.akka.rapidtweak.track;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Markus on 25.09.2015.
 */
@Data
@ToString(callSuper = true)
public class LeftCurveTrackElement extends TrackElement {

    private final static String ELEMENT_NAME = "Left curve ";
    //TODO: reset on new start
    private static int elementCounter = 1;

    public void updateTrackElementName() {
        elementName = ELEMENT_NAME + elementCounter++;
    }

    public static void resetCounter() {
        elementCounter = 1;
    }
}
