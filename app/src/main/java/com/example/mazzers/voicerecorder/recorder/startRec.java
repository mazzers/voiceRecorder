package com.example.mazzers.voicerecorder.recorder;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import com.example.mazzers.voicerecorder.R;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

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
    //String name;


    public startRec(MediaRecorder r, int rgOut, boolean quality, String fileAudioName, Long startTime) {
        Log.d(TAG_LOG, "start cons");
        this.recorder = r;
        this.rgOut = rgOut;
        this.quality = quality;
        this.fileAudioName = fileAudioName;
        this.startTime = startTime;


    }

    public void run() {
        // TODO Auto-generated method stub
        Log.d(TAG_LOG, "IN RUN start Recording");
        startRecording();
    }
//    public String getFileAudioName(){
//        return fileAudioName;
//    }


    public void startRecording() {
//        try {
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
//            Date now = new Date();
//            fileAudioName = formatter.format(now);
//        } catch (Exception e) {
//            Log.d(TAG_LOG, e.toString());
//        }
        //releaseRecorder();
        //startTime = System.currentTimeMillis();
        Log.d(TAG_LOG, "IN Method start Recording");
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        } catch (Exception e) {
            Log.d(TAG_LOG, e.toString());
        }
        //recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //filePathAudio = Environment.getExternalStorageDirectory() + "/" + fileAudioName + ".3gpp";
        Log.d(TAG_LOG, "Set mic source");
        switch (rgOut) {
            case R.id.btn3GPP:
                Log.d(TAG_LOG, "3gpp");
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                filePathAudio = Environment.getExternalStorageDirectory() + "/voicerecorder/" + fileAudioName + ".3gpp";
                break;
            case R.id.btnAMR:
                Log.d(TAG_LOG, "Amr");
                recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                filePathAudio = Environment.getExternalStorageDirectory() + "/voicerecorder" + fileAudioName + ".amr";

                break;
            default:
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                filePathAudio = Environment.getExternalStorageDirectory() + "/voicerecorder" + fileAudioName + ".3gpp";
                break;
        }

        if (quality) {
            Log.d(TAG_LOG, "Quality checked");
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        } else {
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }


        fileAudio = new File(filePathAudio);
        Log.d(TAG_LOG, "blalalala");
        Log.d(TAG_LOG, filePathAudio);
        recorder.setOutputFile(filePathAudio);

        Log.d(TAG_LOG, "Try to prepare");
        try {
            Log.d(TAG_LOG, "In try");
            recorder.prepare();
            Log.d(TAG_LOG, "After prepare");
        } catch (IOException e) {
            Log.d(TAG_LOG, "Prerape fail");
            Log.d(TAG_LOG, e.toString());
        }
        Log.d(TAG_LOG, "Prepare OK");
        recorder.start();
        //createXML();
        //doTimerTask();
        //MainActivity.startChrono();

    }

    private void createXML() {
        filePathBook = Environment.getExternalStorageDirectory() + "/" + fileAudioName + ".xml";
        fileBook = new File(filePathBook);
        try {
            outputStream = new FileOutputStream(fileBook);
            outputStream.write((fillXML() + "\n").getBytes());
            Log.d(TAG_LOG, "In try createBookmarkFile: write");
            outputStream.close();
            Log.d(TAG_LOG, "Closed");

        } catch (Exception e) {

            Log.d(TAG_LOG, "create xml error");
            Log.d(TAG_LOG, e.toString());

        }
    }

    public static String getFilePathAudio() {
        return filePathAudio;

    }

//    public static String getAudioName(){
//        return fileAudioName;
//    }

    public String fillXML() throws IllegalArgumentException, IllegalStateException, IOException {
        Log.d(TAG_LOG, "fillXML");


        XmlSerializer xmlSt = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        xmlSt.setOutput(writer);

        xmlSt.startDocument("UTF-8", true);

        xmlSt.startTag("", "bookmark");
        xmlSt.startTag("", "path");
        xmlSt.attribute("", "value", filePathAudio);
        xmlSt.endTag("", "path");
        xmlSt.endTag("", "bookmark");


        xmlSt.endDocument();


        return writer.toString();

    }
}
