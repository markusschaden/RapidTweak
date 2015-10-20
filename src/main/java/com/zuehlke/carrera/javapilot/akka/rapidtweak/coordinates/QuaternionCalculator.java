package com.zuehlke.carrera.javapilot.akka.rapidtweak.coordinates;

import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markus on 20.10.2015.
 */
public class QuaternionCalculator {

    @Getter
    private List<Double[]> raceTrackCoordinates = new ArrayList<>();
    private Quaternion quat = null;
    double[] Z = {0d, 0d, 1d};
    private Double[] lastCoordinate = {0d, 0d, 0d};
    private MadgwickAHRS madgwickAHRS = null;

    public QuaternionCalculator() {
        raceTrackCoordinates.add(lastCoordinate);
        madgwickAHRS = new MadgwickAHRSIMU(0.1d, new double[]{1, 0, 0, 0}, 50d);

    }

    private void onSensorEvent(SensorEvent sensorEvent, boolean updateRaceTrack) {
        if (sensorEvent == null) return;

        /*Calculate and set quaternion!!*/
        /*Scale the values*/
        double[] imuData = new double[9];

        imuData[0] = ((double) sensorEvent.getA()[0]) / 256d;
        imuData[1] = ((double) sensorEvent.getA()[1]) / 256d;
        imuData[2] = ((double) sensorEvent.getA()[2]) / 256d;
        imuData[3] = ((((double) sensorEvent.getG()[0]) / 14.375d) * (Math.PI / 180.0));
        imuData[4] = ((((double) sensorEvent.getG()[1]) / 14.375d) * (Math.PI / 180.0));
        imuData[5] = ((((double) sensorEvent.getG()[2]) / 14.375d) * (Math.PI / 180.0));
        imuData[6] = ((double) sensorEvent.getM()[0]);
        imuData[7] = ((double) sensorEvent.getM()[1]);
        imuData[8] = ((double) sensorEvent.getM()[2]);

        double initTheta;
        double[] rotAxis;
        /*The initial round*/
        if (quat == null) {
            //Set the initial orientation according to first sample of accelerometry
            System.out.println("X " + Double.toString(imuData[0]) + " Y " + Double.toString(imuData[1]) + " Z " + Double.toString(imuData[2]));
            initTheta = Math.acos(dot(normalize(new double[]{imuData[0], imuData[1], imuData[2]}), Z));
            rotAxis = cross(new double[]{imuData[0], imuData[1], imuData[2]}, Z);
            //System.out.println("X "+Double.toString(rotAxis[0]) +" Y "+Double.toString(rotAxis[1])+" Z "+Double.toString(rotAxis[2])+" norm "+Double.toString(norm(rotAxis))+" cos "+Double.toString(Math.cos(initTheta/2d))+" "+Double.toString(initTheta));
            if (norm(rotAxis) != 0) {
                rotAxis = normalize(rotAxis);
                //quat = new Quaternion(Math.cos(initTheta/2d),-Math.sin(initTheta/2d)*rotAxis[0],-Math.sin(initTheta/2d)*rotAxis[1],-Math.sin(initTheta/2d)*rotAxis[2]);
                quat = new Quaternion(Math.cos(initTheta / 2d), Math.sin(initTheta / 2d) * rotAxis[0], Math.sin(initTheta / 2d) * rotAxis[1], Math.sin(initTheta / 2d) * rotAxis[2]);
            } else {
                quat = new Quaternion(1d, 0d, 0d, 0d);
            }
            madgwickAHRS.setOrientationQuaternion(quat.getDouble());
            //System.out.println(Double.toString(initTheta) +" "+Double.toString(Math.cos(initTheta/2d))+" "+Double.toString(-Math.sin(initTheta/2d)*rotAxis[0])+" "+Double.toString(-Math.sin(initTheta/2d)*rotAxis[1])+" "+Double.toString(-Math.sin(initTheta/2d)*rotAxis[2]) );
            System.out.println(quat.toString());
        } else {
            /*Use Madgwick AHRS IMU algorithm*/
            madgwickAHRS.AHRSUpdate(new double[]{imuData[3], imuData[4], imuData[5], imuData[0], imuData[1], imuData[2], imuData[6], imuData[7], imuData[8]});
            double[] tempQ = madgwickAHRS.getOrientationQuaternion();
            quat = new Quaternion(tempQ[0], tempQ[1], tempQ[2], tempQ[3]);
        }

        if (quat != null) {

            //Calculated rotated values

            //Quaternion grf = new Quaternion(0d, imuData[0], imuData[1], imuData[2]);
            float axisRotation[] = {-90f, 1f, 0f, 0f};
            double rotAngle = axisRotation[0] / 180.0 * Math.PI;
            Quaternion grf = new Quaternion(Math.cos(rotAngle / 2.0), Math.sin(rotAngle / 2.0) * axisRotation[1], Math.sin(rotAngle / 2.0) * axisRotation[2], Math.sin(rotAngle / 2) * axisRotation[3]);
            //Quaternion rotatedQ = ((quat.conjugate()).times(grf)).times(quat);
            Quaternion rotatedQ = (quat.times(grf)).times(quat.conjugate());
            double[] rotatedVals = rotatedQ.getAxis();
            //System.out.println("Got to rotating data X " + rotatedVals[0] + " Y " + rotatedVals[1] + " Z " + rotatedVals[2]);
            Double[] newCoords = {(lastCoordinate[0] + rotatedVals[0]), (lastCoordinate[1] + rotatedVals[1]), (lastCoordinate[2] + rotatedVals[2])};
            //System.out.println("new Coords: " + newCoords[0] + ", " + newCoords[1] + ", " + newCoords[2]);
            float scaleFactor = 0.07f;
            Double[] tempCoords = {newCoords[0] * scaleFactor, newCoords[1] * scaleFactor, newCoords[2] * scaleFactor};
            if (updateRaceTrack) {
                //only update track in learning phase
                raceTrackCoordinates.add(tempCoords);
            }
            lastCoordinate = newCoords;

        }

    }


    private double[] normalize(double[] a) {
        double magnitude = norm(a);
        for (int i = 0; i < a.length; ++i) {
            a[i] = (a[i] / magnitude);
        }
        return a;
    }

    private double[] diff(double[] arrIn) {
        double[] arrOut = new double[arrIn.length - 1];
        for (int i = 0; i < arrIn.length - 1; ++i) {
            arrOut[i] = arrIn[i + 1] - arrIn[i];
        }
        return arrOut;
    }

    private double mean(double[] a) {
        double b = 0;
        for (int i = 0; i < a.length; ++i) {
            b += a[i] / ((double) a.length);
        }
        return b;
    }

    private double[] cross(double[] a, double[] b) {
        double[] c = new double[3];
        c[0] = (a[1] * b[2] - a[2] * b[1]);
        c[1] = (a[2] * b[0] - a[0] * b[2]);
        c[2] = (a[0] * b[1] - a[1] * b[0]);
        return c;
    }

    private double norm(double[] a) {
        double b = 0;
        for (int i = 0; i < a.length; ++i) {
            b += a[i] * a[i];
        }
        return Math.sqrt(b);
    }

    private double dot(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }


    public void updateTrack(SensorEvent sensorEvent) {
        onSensorEvent(sensorEvent, true);
    }

    public Double[] calculatePosition(SensorEvent sensorEvent) {
        onSensorEvent(sensorEvent, false);
        return lastCoordinate;
    }

    public Double[] getLastPosition() {
        return lastCoordinate;
    }
}
