package com.zuehlke.carrera.javapilot.akka.rapidtweak.optimizer;

import com.zuehlke.carrera.javapilot.akka.Configuration;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.power.PowerExecutor;
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
    private TrackElement lastTrackElement;
    private final int MAX_POWER = 255;

    public StraightOptimizer(Race race) {
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
        if (trackElement instanceof StraightTrackElement) {

            int power = 100;
            int newPower = 100;

            if (trackElement.isPenaltyOccured()) {
                int speed1 = trackElement.getSpeeds().get(trackElement.getSpeeds().size() - 1);
                int speed2 = trackElement.getSpeeds().get(trackElement.getSpeeds().size() - 2);
                newPower = (speed1 + speed2) / 2;
                LOGGER.info("Speed difference between last two speeds : " + newPower);


                trackElement.getSpeeds().remove(trackElement.getSpeeds().size() - 1);
                trackElement.getSpeeds().remove(trackElement.getSpeeds().size() - 1);

            } else {

                if (trackElement.getSpeeds().size() == 0) {
                    newPower = Configuration.START_VELOCITY + calculatePowerIncrease(trackElement);
                } else {
                    newPower = trackElement.getSpeeds().get(trackElement.getSpeeds().size() - 1) + calculatePowerIncrease(trackElement);
                }
            }
            if(newPower > MAX_POWER) {
                newPower = MAX_POWER;
            }

            trackElement.getSpeeds().add(newPower);

            double duration = trackElement.getAverageDuration(power);

            double diff = (double) power / (double) newPower;
            LOGGER.debug("Diff: " + diff);
            double newDuration = diff * duration;

            PowerExecutor powerExecutor = new PowerExecutor();
            powerExecutor.setPowerFor(newPower, (int) newDuration, power);
            LOGGER.info("Power Settings, newPower: " + newPower + ", duration: " + duration + ", newDuration: " + newDuration);
        }
        //PowerService.getInstance().setPower(140);
    }




    @Override
    public void setActive(boolean active) {

    }

    private int calculatePowerIncrease(TrackElement trackElement) {

        //TODO: correct power
        double duration = trackElement.getLatestDuration();

        if (duration > 3000) {
            return 25;
        } else if (duration > 2000) {
            return 20;
        } else if (duration > 1000) {
            return 15;
        } else {
            return 10;
        }
    }


}
