package com.zuehlke.carrera.javapilot.akka.rapidtweak.power;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Markus on 25.09.2015.
 */
public class PowerExecutor {

    private final static Logger LOGGER = LoggerFactory.getLogger(PowerExecutor.class);

    public void setPowerFor(int power, int duration, int oldPower) {

        PowerThread powerThread = new TemporarySpeedThread(power, duration, oldPower);

        ServiceManager.getInstance().getPowerService().setPowerThread(powerThread);
    }



    class TemporarySpeedThread extends PowerThread {
        private boolean canceled = false;

        private int power;
        private int duration;
        private int oldPower;

        public TemporarySpeedThread(int power, int duration, int oldPower) {
            this.power = power;
            this.duration = duration;
            this.oldPower = oldPower;
        }

        @Override
        public void run() {

            ServiceManager.getInstance().getPowerService().setPower(power);
            try {
                LOGGER.info("Waiting for " + duration + "ms");
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!canceled) {
                ServiceManager.getInstance().getPowerService().setPower(oldPower);
            }
        }

        public void cancel() {
            canceled = true;
        }
    }


}
