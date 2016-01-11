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

    public void start() {
        luckyPunchThread = new LuckyPunchThread();
        luckyPunchThread.start();
    }

    public void stop() {
        luckyPunchThread.halt();
    }

    class LuckyPunchThread extends Thread {

        int speed = 140;
        boolean running = true;

        public void halt() {
            LOGGER.info("Stopping SpeedUp Thread");
            running = false;
        }

        @Override
        public void run() {

            while (running) {
                LOGGER.info("SpeedUp: " + speed);
                ServiceManager.getInstance().getPowerService().setPower(speed);
                if(speed < 250) {
                    speed += 2;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
