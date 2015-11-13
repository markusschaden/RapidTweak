package com.zuehlke.carrera.javapilot.akka.rapidtweak.trackmodel;


import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.LeftCurveTrackElement;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.RightCurveTrackElement;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.StraightTrackElement;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.TrackElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HeuristicElements {

    private final Logger LOGGER = LoggerFactory.getLogger(HeuristicElements.class);

    private HeuristicElement straight = new HeuristicElement(-500, 500, StraightTrackElement.class);
    private HeuristicElement rightCurve = new HeuristicElement(1000, 10000, RightCurveTrackElement.class);
    private HeuristicElement leftCurve = new HeuristicElement(-10000, -1000, LeftCurveTrackElement.class);

    private List<HeuristicElement> heuristicElements = new ArrayList<>(Arrays.asList(new HeuristicElement[]{straight, rightCurve, leftCurve}));

    public TrackElement getHeuristicElement(int value) {

        for(HeuristicElement heuristicElement : heuristicElements) {
            if(value >= heuristicElement.getMin() && value <= heuristicElement.getMax()) {
                try {
                    return heuristicElement.getTrackElement().newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

}
