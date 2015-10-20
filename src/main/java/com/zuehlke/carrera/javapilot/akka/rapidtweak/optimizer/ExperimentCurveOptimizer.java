package com.zuehlke.carrera.javapilot.akka.rapidtweak.optimizer;

import com.zuehlke.carrera.javapilot.akka.Configuration;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.power.PowerExecutor;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.*;
import com.zuehlke.carrera.relayapi.messages.PenaltyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markus on 22.09.2015.
 */
public class ExperimentCurveOptimizer implements Optimizer {

    private Race race;
    private boolean active = false;
    private final Logger LOGGER = LoggerFactory.getLogger(ExperimentCurveOptimizer.class);
    private TrackElement lastTrackElement;
    private final int MAX_POWER = 150;
    private final int MIN_POWER = 130;


    public ExperimentCurveOptimizer(Race race) {
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
            int id = trackElement.getId();
            long duration = trackElement.getDurations().get(0).getTime();

            int accelerationTime = (int) (duration / 4);

            List<ExperimentEntry> list = race.getStraightExperiment().get(id);
            if (list == null) {
                list = new ArrayList<>();
            } else {
                ExperimentEntry experimentEntry = list.get(list.size() - 1);
                if (experimentEntry.isPenaltyOccured()) {
                    accelerationTime = (int) (experimentEntry.getAcceleartionTime() - 100);
                } else {
                    int adder = 40;
                    long newPower;
                    do {
                        adder -= 10;
                        newPower = experimentEntry.getAcceleartionTime() + adder;


                    } while (!checkSpeed(list, newPower) && adder > 0);


                    accelerationTime = (int) (experimentEntry.getAcceleartionTime() + 30);
                }
            }
            list.add(new ExperimentEntry(accelerationTime, duration));

            race.getStraightExperiment().put(id, list);

            PowerExecutor powerExecutor = new PowerExecutor();
            powerExecutor.setPowerFor(MAX_POWER, accelerationTime, MIN_POWER);
        }
        //PowerService.getInstance().setPower(140);
    }

    @Override
    public void setActive(boolean active) {

    }

    private boolean checkSpeed(List<ExperimentEntry> list, long newPower) {
        for (ExperimentEntry ee : list) {
            if (ee.getAcceleartionTime() <= newPower && ee.isPenaltyOccured()) {
                return false;
            }
        }
        return true;
    }


    private int calculatePowerIncrease(TrackElement trackElement) {

        //TODO: correct power
        double duration = trackElement.getAverageDuration();

        return 10;
    }

}