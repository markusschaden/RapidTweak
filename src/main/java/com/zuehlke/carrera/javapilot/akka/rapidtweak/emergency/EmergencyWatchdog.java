package com.zuehlke.carrera.javapilot.akka.rapidtweak.emergency;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.service.ServiceManager;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Markus on 26.11.2015.
 */
public class EmergencyWatchdog {

    private final Logger LOGGER = LoggerFactory.getLogger(EmergencyWatchdog.class);

    private final int TIMEOUT = 200;
    private long lastUpdate = 0;
    private int[] sensorValues = new int[5];
    private Watchdog watchdog;

    public void onSensorEvent(SensorEvent sensorEvent) {

        if (watchdog != null) {
            watchdog.canceled = true;
        }
        watchdog = new Watchdog();
        watchdog.start();
    }


    private void fallout() {

        LOGGER.error("Fallout");
        LOGGER.error("Reseting race");
        ServiceManager.getInstance().getPowerService().setPower(100);
    }


    @Data
    private class Watchdog extends Thread {

        private boolean canceled = false;

        @Override
        public void run() {
            try {
                Thread.sleep(TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!canceled) {
                fallout();
            }
        }
    }

}
