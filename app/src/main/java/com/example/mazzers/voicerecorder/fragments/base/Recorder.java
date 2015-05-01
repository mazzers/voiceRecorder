package com.example.mazzers.voicerecorder.fragments.base;

import android.media.MediaRecorder;
import android.os.SystemClock;

import java.io.IOException;

/**
 * Created by mazzers on 28. 4. 2015.
 */
public class Recorder {
    private MediaRecorder mediaRecorder;
    private boolean prepared = false;
    private boolean recording = false;
    private long base;

    public Recorder() {
        mediaRecorder = new MediaRecorder();
    }

    public void setMediaRecorder(String file, int sampleRate, int format) {
        prepared = false;
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setOutputFormat(format);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setAudioEncodingBitRate(16);
        mediaRecorder.setAudioSamplingRate(sampleRate);
        mediaRecorder.setOutputFile(file);
        try {
            mediaRecorder.prepare();
            prepared = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startRecording() {
        if (mediaRecorder != null) {
            if (prepared) {
                mediaRecorder.start();
                base = SystemClock.elapsedRealtime();
                recording = true;
            }
        }
    }

    public void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            recording = false;
        }
    }

    public boolean isRecording() {
        return recording;
    }

    public void releaseRecorder() {
        mediaRecorder.release();
    }

    public long getBase() {
        return base;
    }


}
