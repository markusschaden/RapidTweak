package com.zuehlke.carrera.javapilot.akka.rapidtweak.power;

/**
 * Created by Markus on 25.09.2015.
 */
public class PowerExecutor {

    public static void setPowerFor(int power, int duration, int oldPower) {

        Thread thread = new Thread() {

            @Override
            public void run() {

                PowerService.getInstance().setPower(power);
                try {
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
