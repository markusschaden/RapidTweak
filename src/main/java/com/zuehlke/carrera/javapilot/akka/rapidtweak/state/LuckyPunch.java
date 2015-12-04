package com.zuehlke.carrera.javapilot.akka.rapidtweak.state;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.service.ServiceManager;
import com.zuehlke.carrera.relayapi.messages.PenaltyMessage;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import com.zuehlke.carrera.relayapi.messages.VelocityMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Markus on 03.12.2015.
 */
public class LuckyPunch implements State {

    private final Logger LOGGER = LoggerFactory.getLogger(LuckyPunch.class);

    LuckyPunchThread luckyPunchThread;
    int waittime = 1000 * 10;

    StateCallback callback;

    public LuckyPunch(StateCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onSensorEvent(SensorEvent sensorEvent) {

    }

    @Override
    public void onVelocityMessage(VelocityMessage velocityMessage) {

    }

    @Override
    public void onPenaltyMessage(PenaltyMessage penaltyMessage) {

    }

    @Override
    public void onRoundTimeMessage(RoundTimeMessage roundTimeMessage) {

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

    private void startLuckyPunch() {
        luckyPunchThread = new LuckyPunchThread();
        luckyPunchThread.start();
    }

    class LuckyPunchThread extends Thread {

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
