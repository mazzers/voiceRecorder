package com.example.mazzers.voicerecorder.recorder;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Vashchenko Vitaliy A11B0529P
 * PRJ5 - Voice bookmarks
 * Recording thread.
 */
public class startRec implements Runnable {
    private MediaRecorder recorder;
    Boolean quality;
    private String fileAudioName, filePathBook;
    private static String filePathAudio;
    String TAG_LOG = "myLogs";
    Long startTime;
    File fileAudio, fileBook;
    //FileOutputStream outputStream;
    public static boolean isRecording = false;


    /**
     * Constructor wor recorded file
     *
     * @param r
     * @param quality
     * @param fileAudioName
     * @param startTime
     */
    public startRec(MediaRecorder r, boolean quality, String fileAudioName, Long startTime) {
        Log.d(TAG_LOG, "startRec: start cons");
        this.recorder = r;
        this.quality = quality;
        this.fileAudioName = fileAudioName;
        this.startTime = startTime;


    }

    public void run() {
        Log.d(TAG_LOG, "startRec: IN RUN start Recording");
        startRecording();
    }

    /**
     * start recording
     */
    public void startRecording() {
        //todo settings debug
        Log.d(TAG_LOG, "startRec: IN Method start Recording");
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        } catch (Exception e) {
            Log.d(TAG_LOG, e.toString());
        }
        Log.d(TAG_LOG, "startRec: Set mic source");

        if (quality) {
            Log.d(TAG_LOG, "startRec: Quality checked");
            filePathAudio = Environment.getExternalStorageDirectory() + "/voicerecorder/" + fileAudioName + ".aac";
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        } else {
            filePathAudio = Environment.getExternalStorageDirectory() + "/voicerecorder/" + fileAudioName + ".3gpp";
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }


        fileAudio = new File(filePathAudio);
        Log.d(TAG_LOG, "startRec: " + filePathAudio);
        recorder.setOutputFile(filePathAudio);

        Log.d(TAG_LOG, "startRec: Try to prepare");
        try {
            Log.d(TAG_LOG, "startRec: In try");
            recorder.prepare();
            Log.d(TAG_LOG, "startRec: After prepare");
        } catch (IOException e) {
            Log.d(TAG_LOG, "startRec: Prepare fail");
            Log.d(TAG_LOG, e.toString());
        }
        Log.d(TAG_LOG, "startRec: Prepare OK");
        try {
            recorder.start();
            isRecording = true;
        } catch (Exception e) {
            Log.e(TAG_LOG, e.toString());
        }


    }

    /**
     * Check recording state
     *
     * @return
     */
    public static boolean isRecording() {
        return isRecording;
    }

    /**
     * Stop recording
     */
    public static void stpRecording() {
        isRecording = false;

    }

    /**
     * get audioFile path
     *
     * @return
     */
    public static String getFilePathAudio() {
        return filePathAudio;

    }


}
