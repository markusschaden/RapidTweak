package com.zuehlke.carrera.javapilot.akka.rapidtweak.optimizer;

import com.zuehlke.carrera.javapilot.akka.Configuration;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.power.PowerExecutor;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.power.PowerService;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Race;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.StraightTrackElement;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.TrackElement;
import com.zuehlke.carrera.relayapi.messages.PenaltyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Markus on 22.09.2015.
 */
public class StraightOptimizer implements Optimizer {

    private Race race;
    private boolean active = false;
    private final Logger LOGGER = LoggerFactory.getLogger(StraightOptimizer.class);

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
            int newPower = 100;
            if(trackElement.getSpeeds().size()==0) {
                newPower = Configuration.START_VELOCITY + Configuration.WARMUP_VELOCITY_INCREASE;
            } else {
                newPower = trackElement.getSpeeds().get(trackElement.getSpeeds().size()-1) + Configuration.WARMUP_VELOCITY_INCREASE;
            }

            trackElement.getSpeeds().add(newPower);

            double duration = trackElement.getAverageDuration(power);

            double diff = (double)power / (double)newPower;
            LOGGER.debug("Diff: " + diff);
            double newDuration = diff * duration;

            PowerExecutor powerExecutor = new PowerExecutor();
            powerExecutor.setPowerFor(newPower, (int) newDuration, power);
            LOGGER.info("Power Settings, newPower: " + newPower + ", duration: " + duration + ", newDuration: " + newDuration);

            //PowerService.getInstance().setPower(140);
        } else {
            //PowerService.getInstance().setPower(100);
        }
    }

    @Override
    public void setActive(boolean active) {

    }

}
