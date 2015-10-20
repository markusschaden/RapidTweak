package com.zuehlke.carrera.javapilot.akka.rapidtweak.coordinates;

import java.util.ArrayList;
import java.util.Scanner;

public class ReadCSV {

    private ArrayList<ArrayList<Double>> data;

    public ReadCSV() {
        data = new ArrayList<ArrayList<Double>>();

        Scanner scanner = null;
        scanner = new Scanner(getClass().getResourceAsStream("/data.csv"));
        scanner.useDelimiter(",|\r\n");

        while (scanner.hasNextDouble()) {
            ArrayList<Double> line = new ArrayList<Double>();
            line.add(scanner.nextDouble());
            line.add(scanner.nextDouble());
            line.add(scanner.nextDouble());
            line.add(scanner.nextDouble());
            line.add(scanner.nextDouble());
            line.add(scanner.nextDouble());
            line.add(scanner.nextDouble());
            line.add(scanner.nextDouble());
            line.add(scanner.nextDouble());
            line.add(scanner.nextDouble());

            //System.out.println(Arrays.toString(line.toArray()));
            data.add(line);
        }
    }

    public double[][] getData() {
        double[][] result = new double[data.size()][10];
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(i).size(); j++) {
                result[i][j] = data.get(i).get(j);
            }
        }
        return result;
    }
}
