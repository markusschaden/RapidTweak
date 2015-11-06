package com.zuehlke.carrera.javapilot.akka.rapidtweak.coordinates;

import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by Markus on 20.10.2015.
 */
public class TrackCoordinateCalculator {

    private final static Logger LOGGER = LoggerFactory.getLogger(TrackCoordinateCalculator.class);

    private static boolean simulation = false;

    private QuaternionCalculator quaternionCalculator;
    private int index = 0;
    private static double[][] values = {
            //0-2 acce
            //3-5 gyro
            //6-8 mag
            //9 time
    };

    static {
        if (simulation) {
            values = new ReadCSV().getData();
            LOGGER.info("CSV Load complete");
        }
    }

    public TrackCoordinateCalculator() {

        quaternionCalculator = new QuaternionCalculator();
    }

    public void onSensorEvent(SensorEvent sensorEvent) {
        if (simulation) {
            SensorEvent sensorEvent1 = new SensorEvent("test", new int[]{(int) values[index][0], (int) values[index][1], (int) values[index][2]}, new int[]{(int) values[index][3], (int) values[index][4], (int) values[index][5]}, new int[]{(int) values[index][6], (int) values[index][7], (int) values[index][8]}, new Date().getTime());
            index++;

            quaternionCalculator.updateTrack(sensorEvent1);
        } else {
            quaternionCalculator.updateTrack(sensorEvent);
        }
    }

    public Double[] calculatePosition(SensorEvent sensorEvent) {
        if (simulation) {
            SensorEvent sensorEvent1 = new SensorEvent("test", new int[]{(int) values[index][0], (int) values[index][1], (int) values[index][2]}, new int[]{(int) values[index][3], (int) values[index][4], (int) values[index][5]}, new int[]{(int) values[index][6], (int) values[index][7], (int) values[index][8]}, new Date().getTime());
            index++;

            return quaternionCalculator.calculatePosition(sensorEvent1);
        } else {
            return quaternionCalculator.calculatePosition(sensorEvent);
        }
    }

    public List<Double[]> getTrack() {
        return quaternionCalculator.getRaceTrackCoordinates();
    }


    public Double[] getLastPosition() {
        return quaternionCalculator.getLastPosition();
    }
}
