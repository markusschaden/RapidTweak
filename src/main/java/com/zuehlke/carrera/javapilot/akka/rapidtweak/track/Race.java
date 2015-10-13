package com.zuehlke.carrera.javapilot.akka.rapidtweak.track;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Markus on 25.09.2015.
 */
@Data
@ToString(callSuper = true)
public class Race implements Serializable {

    private String trackId;
    private long time = new Date().getTime();
    private List<TrackElement> track = new CircularArrayList<>();
    private Map<String, SpeedMeasureTrackElement> speedMeasureTrackElements = new HashMap<>();
    private HashMap<Integer, List<ExperimentEntry>> straightExperiment = new HashMap<>();
}
