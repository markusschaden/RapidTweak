package com.zuehlke.carrera.javapilot.akka;

/**
 * Created by Markus on 21.09.2015.
 */
public class Configuration {

    public static final int START_POWER = 100;
    public static final int MAX_POWER_STRAIGHT = 255;
    public static final int MAX_POWER_CURVE = 180;
    public static final int MIN_VELOCITY_FOR_ROUNDDETECTION = 200;

    public static final long DELTA_TIME_ROUNDDETECTION = 500;

    public static final int INCREASE_ACCELERATION_TIME_CURVE = 40;
    public static final int INCREASE_ACCELERATION_TIME_STRAIGHT = 40;

    public static final int DECREASE_ACCELERATION_TIME_STRAIGHT = 10;
    public static final int DECREASE_ACCELERATION_TIME_CURVE = 10;

    public static final int LUCKY_PUNCH_INTERVAL_ACCELERATION_TIME = 150;

    public static final int LUCKY_PUNCH_WAITTIME = 1000 * 60 * 4;
    public static final int TIMEOUT_WATCHDOG_ROUNDDETECTION = 1000 * 100;


}
