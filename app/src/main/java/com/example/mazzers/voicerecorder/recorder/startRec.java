package com.example.mazzers.voicerecorder.recorder;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

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
            isRecording=true;
        } catch (Exception e) {
            Log.e(TAG_LOG, e.toString());
        }


    }

    public static boolean isRecording(){
        return isRecording;
    }

    public static void stpRecording(){
        isRecording=false;

    }


    public static String getFilePathAudio() {
        return filePathAudio;

    }


}
