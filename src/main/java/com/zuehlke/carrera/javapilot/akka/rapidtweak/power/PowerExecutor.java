package com.zuehlke.carrera.javapilot.akka.rapidtweak.power;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Markus on 25.09.2015.
 */
public class PowerExecutor {

    private final static Logger LOGGER = LoggerFactory.getLogger(PowerExecutor.class);

    public static void setPowerFor(int power, int duration, int oldPower) {

        Thread thread = new Thread() {

            @Override
            public void run() {

                PowerService.getInstance().setPower(power);
                try {
                    LOGGER.info("Waiting for " + duration + "ms");
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                PowerService.getInstance().setPower(oldPower);
            }
        };

        thread.start();

    }


}
