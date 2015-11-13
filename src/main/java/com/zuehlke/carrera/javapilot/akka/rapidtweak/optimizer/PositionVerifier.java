package com.zuehlke.carrera.javapilot.akka.rapidtweak.optimizer;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Race;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.TrackElement;

/**
 * Created by Markus on 12.11.2015.
 */
public class PositionVerifier {

    private Race race;

    public PositionVerifier(Race race) {
        this.race = race;
    }

    public boolean predictSlow(TrackElement currentElement, TrackElement nextElement) {
        int index = race.getTrack().indexOf(currentElement);
        if (index != -1) {
            return race.getTrack().get(index + 1).getClass().equals(nextElement.getClass());
        }
        return false;
    }

    public boolean predictFast(TrackElement currentElement, TrackElement nextElement) {

        int index = race.getTrack().indexOf(currentElement);
        if (index != -1) {

            return race.getTrack().get(index + 1).getClass().equals(nextElement.getClass()) || race.getTrack().get(index + 2).getClass().equals(nextElement.getClass());
        }
        return false;
    }
}
