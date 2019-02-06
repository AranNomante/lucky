package com.sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

class MemoryTrack {
    private PrintStream printStream;

    MemoryTrack(ArrayList<Double> data) {
        createDirectory("memLog");
        createDirectory("memAlertLog");
        try {
            Timestamp ts = new Timestamp(new Date().getTime());
            String stamp = ("" + ts).substring(0, ("" + ts).lastIndexOf(":"));
            stamp = stamp.replace(":", "-");
            printStream = new PrintStream("memLog/" + "Memory log " + stamp + ".txt");
            boolean alert = false;
            int start = 0;
            String mib = " MiB";
            String message = "There was a rise until time: ";
            String slopeString = "Slope: ";
            String average = "Average: ";
            String min = "Minimum: ";
            String max = "Maximum: ";
            String end = "-----------";
            long totalSum = 0;
            for (int index = 0; index < data.size() - 1; index++) {
                totalSum += (data.get(index)).longValue();
                if (data.get(index) > data.get(index + 1)) {
                    long sum = 0;
                    int elements = 0;
                    for (int i = start; i < index + 1; i++) {
                        sum += data.get(i);
                        elements++;
                    }
                    long avg = sum / elements;
                    double slopeStep = (data.get(index) - data.get(start)) / (double) (index - start);
                    String slope = (new Double(slopeStep)).longValue() + "%";
                    long minimum = (data.get(start)).longValue();
                    long maximum = (data.get(index)).longValue();
                    if ((avg > 200 || maximum > 400) && !alert) {
                        alert = true;
                        printStream = new PrintStream("memAlertLog/" + "Alert log " + stamp + ".txt");
                    }
                    printStream.println(end);
                    printStream.println(message + index + " from: " + start);
                    printStream.println(slopeString + slope);
                    printStream.println(average + avg + mib);
                    printStream.println(min + minimum + mib);
                    printStream.println(max + maximum + mib);
                    printStream.println(end);
                    start = index + 1;
                    index++;
                }
            }
            printStream.println(end);
            printStream.println(end);
            printStream.println("Total " + average + (totalSum / data.size()));
            printStream.println("Global " + min + (Collections.min(data)));
            printStream.println("Global " + max + (Collections.max(data)));
            printStream.println(end);
            printStream.println(end);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (printStream != null) {
                printStream.close();
            }
        }
    }

    private void createDirectory(String pathName) {
        final File dir = new File(pathName);
        if (!dir.exists()) {
            System.out.println("Creating directory");
            boolean created = dir.mkdirs();
            System.out.println("created: " + created);
        } else {
            System.out.println("directory exists");
            System.out.println("running cleaning routine");
            listFilesForFolder(dir);
        }
    }

    private void listFilesForFolder(final File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            if (files.length != 0) {
                System.out.println(files.length);
                if (files.length > 9) {
                    System.out.println("cleaning initiated");
                    for (int i = 0; i < files.length - 4; i++) {
                        System.out.println(files[i].delete());
                    }
                } else {
                    System.out.println("no cleaning required");
                }
            }
        }
    }

}
