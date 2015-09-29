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
public class Race {

    private List<TrackElement> track = new CircularArrayList<>();
    private List<SpeedMeasureTrackElement> speedMeasureTrackElements = new ArrayList<>();

}
