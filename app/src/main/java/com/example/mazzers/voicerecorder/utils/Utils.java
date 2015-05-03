package com.example.mazzers.voicerecorder.utils;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * voiceRecorder application
 * @author Vitaliy Vashchenko A11B0529P
 * Utils for data representation
 */
public class Utils {
    private static String TAG_LOG = "myLogs";


    /**
     * Function to convert milliseconds time to Timer Format
     * @param milliseconds milliseconds
     * @return time to string
     */
    public static String milliSecondsToTimer(long milliseconds) {
        String stringBuffer = "";
        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        stringBuffer += String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        return stringBuffer;
    }

    /**
     * Function to get Progress percentage
     *
     * @param currentDuration mediaPlayer current position
     * @param totalDuration mediaPlayer total length
     */
    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        //Log.d(TAG_LOG,"Utils: get progressPercentage");
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     *
     * @param progress      -
     * @param totalDuration returns current duration in milliseconds
     */
    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    /**
     * System time to string
     *
     * @param i time
     * @return sting from time
     */
    public static String timeToString(int i) {
        String time;
        int hours = i / 3600;
        int remainder = i - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;
        time = hours + ":" + mins + ":" + secs + "";
        return time;
    }

    /**
     * File size to string
     *
     * @param size file size
     * @return file size in string
     */
    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * Date to string
     * @param date actual date
     * @return date in string
     */
    public static String dateToString(Long date) {
        Date d = new Date(date);
        return d.toString();
    }

}
