package com.zuehlke.carrera.javapilot.akka.rapidtweak.optimizer;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Race;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.TrackElement;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.trackmodel.HeuristicElements;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;

/**
 * Created by Markus on 12.11.2015.
 */
public class TrackRecognizer {

    private Race race;
    private PositionVerifier positionVerifier;
    private HeuristicElements heuristicElements = new HeuristicElements();
    private TrackElement currentTrackElement;
    private boolean positionLost = false;

    public TrackRecognizer(Race race) {
        this.race = race;
        positionVerifier = new PositionVerifier(race);
    }

    public TrackElement getTrackElement(SensorEvent sensorEvent) {

        TrackElement newTrackElement = (heuristicElements.getHeuristicElement(sensorEvent.getG()[2]));
        if (newTrackElement == null) return null;
        if (currentTrackElement == null) currentTrackElement = race.getTrack().get(0);

        if (!currentTrackElement.getClass().equals(newTrackElement.getClass())) { //unterschiedliche track elements

            if (positionVerifier.predictFast(currentTrackElement, newTrackElement)) {
                currentTrackElement = newTrackElement;

            } else {
                positionLost = true;
            }


        }


        return currentTrackElement;
    }
}
