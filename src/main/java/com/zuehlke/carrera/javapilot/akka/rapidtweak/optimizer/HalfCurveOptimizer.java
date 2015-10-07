package com.zuehlke.carrera.javapilot.akka.rapidtweak.optimizer;

import com.zuehlke.carrera.javapilot.akka.Configuration;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.power.PowerExecutor;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.*;
import com.zuehlke.carrera.relayapi.messages.PenaltyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Markus on 22.09.2015.
 */
public class HalfCurveOptimizer implements Optimizer {

    private Race race;
    private boolean active = false;
    private final Logger LOGGER = LoggerFactory.getLogger(HalfCurveOptimizer.class);
    private TrackElement lastTrackElement;

    public HalfCurveOptimizer(Race race) {
        this.race = race;
    }

    @Override
    public void onPenalityEvent(PenaltyMessage message) {
        if (lastTrackElement instanceof StraightTrackElement) {
            lastTrackElement.setPenaltyOccured(true);
        }
    }

    @Override
    public void onTrackElementChange(TrackElement trackElement, int position) {
        lastTrackElement = trackElement;
        if (trackElement instanceof RightCurveTrackElement || trackElement instanceof LeftCurveTrackElement) {

            int power = 100;
            int newPower = 0;
            int maxPower = 150;

            Double duration = trackElement.getAverageDuration();

            if (trackElement.getSpeeds().size() == 0) {
                newPower = Configuration.START_VELOCITY + calculatePowerIncrease(trackElement);
            } else {
                newPower = trackElement.getSpeeds().get(trackElement.getSpeeds().size() - 1) + calculatePowerIncrease(trackElement);
            }

            double diff = (double) power / (double) newPower;
            LOGGER.debug("Diff: " + diff);
            long waitTime = (long) (diff * duration / 2);

            if(newPower > maxPower) {
                newPower = maxPower;
            }
            trackElement.getSpeeds().add(newPower);

            PowerExecutor powerExecutor = new PowerExecutor();
            powerExecutor.setPowerAfterTime(newPower, waitTime);
            LOGGER.info("Power Settings, power: " + newPower + ", waitTime: " + waitTime);
        }
        //PowerService.getInstance().setPower(140);
    }

    @Override
    public void setActive(boolean active) {

    }


    private int calculatePowerIncrease(TrackElement trackElement) {

        //TODO: correct power
        double duration = trackElement.getAverageDuration();

        return 10;
    }

}
