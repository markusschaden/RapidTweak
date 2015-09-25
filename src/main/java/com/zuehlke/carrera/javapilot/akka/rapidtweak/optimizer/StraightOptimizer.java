package com.zuehlke.carrera.javapilot.akka.rapidtweak.optimizer;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.power.PowerService;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Race;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.StraightTrackElement;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.TrackElement;
import com.zuehlke.carrera.relayapi.messages.PenaltyMessage;

/**
 * Created by Markus on 22.09.2015.
 */
public class StraightOptimizer implements Optimizer {

    private Race race;
    private boolean active = false;

    public StraightOptimizer(Race race) {
        this.race = race;
    }

    @Override
    public void onPenalityEvent(PenaltyMessage message) {

    }

    @Override
    public void onTrackElementChange(TrackElement trackElement, int position) {
        if(trackElement instanceof StraightTrackElement) {

            int power = 100;
            int newPower = 130;

            double duration = trackElement.getAverageDuration(power);

            double diff = power / newPower;
            double newDuration = diff * duration;   

            PowerService.getInstance().setPower(130);
        } else {
            PowerService.getInstance().setPower(100);
        }
    }

    @Override
    public void setActive(boolean active) {

    }

}
