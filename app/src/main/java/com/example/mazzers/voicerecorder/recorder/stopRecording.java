package com.example.mazzers.voicerecorder.recorder;

import android.media.MediaRecorder;
import android.util.Log;


public class stopRecording implements Runnable {
    private String TAG_LOG = "myLogs";

    private MediaRecorder recorder;

    public stopRecording(MediaRecorder recorder) {
        this.recorder = recorder;
    }

    public void run() {
        Log.d(TAG_LOG, "stopRecording: stop rec run start");

        stopRec();
        Log.d(TAG_LOG, "stopRecording: end of run stop");

    }

    private void stopRec() {
        Log.d(TAG_LOG, "stopRecording: stop method");
        //stopChrono();
        if (recorder != null) {
            try {


                recorder.stop();
            }catch (Exception e){
                Log.e(TAG_LOG,e.toString());
            }

        }
    }
}
