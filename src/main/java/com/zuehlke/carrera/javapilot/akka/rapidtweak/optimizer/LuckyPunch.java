package com.zuehlke.carrera.javapilot.akka.rapidtweak.optimizer;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Markus on 03.12.2015.
 */
public class LuckyPunch {

    private final Logger LOGGER = LoggerFactory.getLogger(LuckyPunch.class);

    SpeedUpThread speedUpThread;
    int waittime = 1000 * 10;

    private void startLuckyPunch() {
        speedUpThread = new SpeedUpThread();
        speedUpThread.start();
    }

    public void setup() {
        new Thread() {
            @Override
            public void run() {
                try {
                    LOGGER.info("Setup SpeedUp Algo, wait until start: " + waittime + "ms");
                    Thread.sleep(waittime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                startLuckyPunch();
            }
        }.start();
    }

    class SpeedUpThread extends Thread {

        int speed = 140;

        @Override
        public void run() {

            while (speed < 250) {
                LOGGER.info("SpeedUp: " + speed);
                ServiceManager.getInstance().getPowerService().setPower(speed);
                speed += 10;
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
