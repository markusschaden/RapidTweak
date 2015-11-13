package com.zuehlke.carrera.javapilot.akka.rapidtweak.routing;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.race.RaceStatus;

/**
 * Created by Markus on 12.11.2015.
 */
public interface ChangeRaceStatus {

    void changeRaceStatus(RaceStatus raceStatus);
}
