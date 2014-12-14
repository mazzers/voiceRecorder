package com.example.mazzers.voicerecorder.recorder;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class startRec implements Runnable {
    private MediaRecorder recorder;
    int rgOut;
    Boolean quality;
    private String fileAudioName, filePathBook;
    private static String filePathAudio;
    String TAG_LOG = "myLogs";
    Long startTime;
    File fileAudio, fileBook;
    FileOutputStream outputStream;


    public startRec(MediaRecorder r, int rgOut, boolean quality, String fileAudioName, Long startTime) {
        Log.d(TAG_LOG, "startRec: start cons");
        this.recorder = r;
        this.rgOut = rgOut;
        this.quality = quality;
        this.fileAudioName = fileAudioName;
        this.startTime = startTime;


    }

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
        //recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //filePathAudio = Environment.getExternalStorageDirectory() + "/" + fileAudioName + ".3gpp";
        Log.d(TAG_LOG, "startRec: Set mic source");
//        switch (rgOut) {
//            case R.id.btn3GPP:
//                Log.d(TAG_LOG, "startRec: 3gpp");
//                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                filePathAudio = Environment.getExternalStorageDirectory() + "/voicerecorder/" + fileAudioName + ".3gpp";
//                break;
//            case R.id.btnAMR:
//                Log.d(TAG_LOG, "startRec: Amr");
//                recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
//                filePathAudio = Environment.getExternalStorageDirectory() + "/voicerecorder" + fileAudioName + ".amr";
//
//                break;
//            default:
//                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                filePathAudio = Environment.getExternalStorageDirectory() + "/voicerecorder" + fileAudioName + ".3gpp";
//                break;
//        }

        if (quality) {
            Log.d(TAG_LOG, "startRec: Quality checked");
            filePathAudio = Environment.getExternalStorageDirectory() + "/voicerecorder" + fileAudioName + ".aac";
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        } else {
            filePathAudio = Environment.getExternalStorageDirectory() + "/voicerecorder" + fileAudioName + ".3gpp";
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
        } catch (Exception e) {
            Log.e(TAG_LOG, e.toString());
        }


    }

//    private void createXML() {
//        filePathBook = Environment.getExternalStorageDirectory() + "/" + fileAudioName + ".xml";
//        fileBook = new File(filePathBook);
//        try {
//            outputStream = new FileOutputStream(fileBook);
//            outputStream.write((fillXML() + "\n").getBytes());
//            Log.d(TAG_LOG, "startRec: In try createBookmarkFile: write");
//            outputStream.close();
//            Log.d(TAG_LOG, "startRec: Closed");
//
//        } catch (Exception e) {
//
//            Log.d(TAG_LOG, "startRec: create xml error");
//            Log.d(TAG_LOG, e.toString());
//
//        }
//    }

    public static String getFilePathAudio() {
        return filePathAudio;

    }


//    public String fillXML() throws IllegalArgumentException, IllegalStateException, IOException {
//        Log.d(TAG_LOG, "startRec: fillXML");
//
//
//        XmlSerializer xmlSt = Xml.newSerializer();
//        StringWriter writer = new StringWriter();
//        xmlSt.setOutput(writer);
//
//        xmlSt.startDocument("UTF-8", true);
//
//        xmlSt.startTag("", "bookmark");
//        xmlSt.startTag("", "path");
//        xmlSt.attribute("", "value", filePathAudio);
//        xmlSt.endTag("", "path");
//        xmlSt.endTag("", "bookmark");
//
//
//        xmlSt.endDocument();
//
//
//        return writer.toString();
//
//    }
}
