package com.example.mazzers.voicerecorder.fragments.base;

import android.media.MediaRecorder;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;

/**
 * voiceRecorder application
 *
 * @author Vitaliy Vashchenko A11B0529P
 *         Recorder base for application uage
 */
public class Recorder implements MediaRecorder.OnInfoListener {
    private final int MAX_DURATION = 5400000; // hour and half records max
    private final String TAG_LOG = "viewLessRecorder";
    private MediaRecorder mediaRecorder;
    private boolean prepared = false;
    private boolean recording = false;
    private boolean maxReached = false;
    private long base;

    /**
     * Recorder constructor
     */
    public Recorder() {
        mediaRecorder = new MediaRecorder();
    }

    /**
     * Set recorder from arguments
     *
     * @param file       output file path
     * @param sampleRate record quality
     * @param format     record format
     */
    public void setMediaRecorder(String file, int sampleRate, int format) {
        prepared = false;
        mediaRecorder.reset();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setOutputFormat(format);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setAudioEncodingBitRate(16);
        mediaRecorder.setAudioSamplingRate(sampleRate);
        mediaRecorder.setOnInfoListener(this);
        mediaRecorder.setMaxDuration(MAX_DURATION);
        mediaRecorder.setOutputFile(file);
        try {
            mediaRecorder.prepare();
            prepared = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start recording
     */
    public void startRecording() {
        if (mediaRecorder != null) {
            if (prepared) {
                mediaRecorder.start();
                base = SystemClock.elapsedRealtime();
                recording = true;
                maxReached = false;
            }
        }
    }

    /**
     * Stop recording
     */
    public void stopRecording() {
        if (mediaRecorder != null && recording) {
            mediaRecorder.stop();
            recording = false;
        }
    }

    /**
     * Check if recorder is active
     *
     * @return recording state
     */
    public boolean isRecording() {
        return recording;
    }

    /**
     * Get recording start time
     *
     * @return record start time
     */
    public long getBase() {
        return base;
    }

    /**
     * Check if MAX_DURATION is reached
     *
     * @return true if recording reached max duration, otherwise false
     */
    public boolean isMaxReached() {
        return maxReached;
    }

    /**
     * MediaRecorder listener
     *
     * @param mr    active MediaRecorder
     * @param what  info state
     * @param extra info extra data
     */
    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            Log.d(TAG_LOG, "reached");
            maxReached = true; // stop recording after reaching the limit
            stopRecording();
        }
    }
}
