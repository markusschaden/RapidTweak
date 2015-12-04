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

    public void cancel() {
        watchdog.canceled = true;
    }


    private void fallout() {

        LOGGER.error("Fallout");

        System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM");
        System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM");
        System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM");
        System.out.println("MMMMMMMMMMMMMMmMMMMMMMMMMMMMMMMMMMMmMMMMMMMMMMMMMM");
        System.out.println("MMMMMMMMMMMms:-+NMMMMMMMMMMMMMMMMN+-:smMMMMMMMMMMM");
        System.out.println("MMMMMMMMMm+-----/NMMMMMMMMMMMMMMN/-----omMMMMMMMMM");
        System.out.println("MMMMMMMMy--------:mMMMMMMMMMMMMm:--------yMMMMMMMM");
        System.out.println("MMMMMMM+-----------dMMMMMMMMMMd-----------+MMMMMMM");
        System.out.println("MMMMMM+-------------yMMMMMMMMy-------------+MMMMMM");
        System.out.println("MMMMMy---------------yMMMMMMs---------------yMMMMM");
        System.out.println("MMMMM---------------yMNhsshNMy---------------MMMMM");
        System.out.println("MMMMd--------------dMs------sMd--------------dMMMM");
        System.out.println("MMMMmsssssssssssssyMM--------MMysssssssssssssmMMMM");
        System.out.println("MMMMMMMMMMMMMMMMMMMMMs------sMMMMMMMMMMMMMMMMMMMMM");
        System.out.println("MMMMMMMMMMMMMMMMMMMMMMNhsshNMMMMMMMMMMMMMMMMMMMMMM");
        System.out.println("MMMMMMMMMMMMMMMMMMMMMy+syys+yMMMMMMMMMMMMMMMMMMMMM");
        System.out.println("MMMMMMMMMMMMMMMMMMMMo--------oMMMMMMMMMMMMMMMMMMMM");
        System.out.println("MMMMMMMMMMMMMMMMMMN+----------+NMMMMMMMMMMMMMMMMMM");
        System.out.println("MMMMMMMMMMMMMMMMMN/------------/NMMMMMMMMMMMMMMMMM");
        System.out.println("MMMMMMMMMMMMMMMMm:--------------:mMMMMMMMMMMMMMMMM");
        System.out.println("MMMMMMMMMMMMMMMd------------------dMMMMMMMMMMMMMMM");
        System.out.println("MMMMMMMMMMMMMMMs/----------------/sMMMMMMMMMMMMMMM");
        System.out.println("MMMMMMMMMMMMMMMMMMmhyso++++osyhmMMMMMMMMMMMMMMMMMM");
        System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM");
        System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM");


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
