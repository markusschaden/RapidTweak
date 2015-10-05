package com.zuehlke.carrera.javapilot.akka.rapidtweak.track;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Markus on 25.09.2015.
 */
@Data
@ToString(callSuper = true)
public class StraightTrackElement extends TrackElement {

    private final static String ELEMENT_NAME = "Straight ";
    //TODO: reset on new start
    private static int elementCounter = 1;

    @Override
    public String getTrackName() {
        return ELEMENT_NAME + elementCounter++;
    }
}
