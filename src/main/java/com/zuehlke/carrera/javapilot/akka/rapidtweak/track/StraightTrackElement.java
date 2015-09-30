package com.zuehlke.carrera.javapilot.akka.rapidtweak.track;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Markus on 25.09.2015.
 */
@Data
@ToString(callSuper = true)
public class StraightTrackElement extends TrackElement {

    public StraightTrackElement() {
        clazz = getClass().getCanonicalName();
    }
}
