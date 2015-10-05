package com.zuehlke.carrera.javapilot.akka.rapidtweak.trackmodel;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.TrackElement;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HeuristicElement {

    long min;
    long max;
    Class<? extends TrackElement> trackElement;

    public HeuristicElement(long min, long max, Class<? extends TrackElement> trackElement) {
        this.min = min;
        this.max = max;
        this.trackElement = trackElement;
    }
}
