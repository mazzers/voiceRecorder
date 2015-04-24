package com.example.mazzers.voicerecorder.utils;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Vashchenko Vitaliy A11B0529P
 * PRJ5 - Voice bookmarks
 * Utils class
 */
public class Utils {
    private static String TAG_LOG = "myLogs";

    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     */
    public static String milliSecondsToTimer(long milliseconds) {
        //Log.d(TAG_LOG,"Utils: milliSecondsToTimer ");
        //String finalTimerString = "";
        //String secondsString = "";
        StringBuffer buffer = new StringBuffer();

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
//        if (hours > 0) {
//            finalTimerString = hours + ":";
//        }
//
//        // Prepending 0 to seconds if it is one digit
//        if (seconds < 10) {
//            secondsString = "0" + seconds;
//        } else {
//            secondsString = "" + seconds;
//        }
//
//        finalTimerString = finalTimerString + minutes + ":" + secondsString;
//
//        // return timer string
//        return finalTimerString;
        buffer
                .append(String.format("%02d", hours))
                .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));
        return buffer.toString();
    }

    /**
     * Function to get Progress percentage
     *
     * @param currentDuration
     * @param totalDuration
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
        //Log.d(TAG_LOG,"Utils: progressToTimer");
        int currentDuration = 0;
        totalDuration = (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

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

    public static String timeToString(long i) {
        String time;
        int hours = (int) i / 3600;
        int remainder = (int) i - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;
        time = hours + ":" + mins + ":" + secs + "";
        return time;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String durationToString(long i) {
        int duration = (int) (i / 1000);
        return timeToString(duration);
    }

    public static String dateToString(Long date) {
        Date d = new Date(date);
        return d.toString();
    }

}
