package com.zuehlke.carrera.javapilot.akka.rapidtweak.state;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Race;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.TrackElement;
import lombok.Data;

@Data
public class Context {

    private Race race;
    private TrackElement currentTrackElement;
    private int minimumPower = 100;
}
