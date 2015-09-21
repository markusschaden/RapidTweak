package com.zuehlke.carrera.javapilot.akka.rapidtweak;

/**
 * Created by Markus on 21.09.2015.
 */
public class HeuristicElements {

    private HeuristicElement straight = new HeuristicElement(-500,500, new StraightTrackElement());
    private HeuristicElement rightCurve = new HeuristicElement(1000,4000, new RightCurveTrackElement());
    private HeuristicElement leftCurve = new HeuristicElement(-4000,-1000, new LeftCurveTrackElement());

    public TrackElement getHeuristicElement(int value) {
        if(straight.min < value && straight.max > value) {
            return new StraightTrackElement();
        } else if(rightCurve.min < value && rightCurve.max > value) {
            return new RightCurveTrackElement();
        } else if(leftCurve.min < value && leftCurve.max > value) {
            return new LeftCurveTrackElement();
        } else {
            return null;
        }
    }


    public class HeuristicElement {

        long min;
        long max;
        TrackElement trackElement;

        public HeuristicElement(long min, long max, TrackElement trackElement) {
            this.min = min;
            this.max = max;
            this.trackElement = trackElement;
        }

        public TrackElement getTrackElement() {
            return trackElement;
        }

        public void setTrackElement(TrackElement trackElement) {
            this.trackElement = trackElement;
        }
    }

}
