package com.example.mazzers.voicerecorder.recorder;

import android.media.AudioFormat;

/**
 * Created by mazzers on 2. 3. 2015.
 */
public class AudioFormatInfo {
    private int sampleRateInHz;
    private int channelConfig;
    private int audioFormat;

    public int getAudioFormat() {
        return audioFormat;
    }

    public void setAudioFormat(int audioFormat) {
        this.audioFormat = audioFormat;
    }

    public int getChannelConfig() {

        return channelConfig;
    }

    public void setChannelConfig(int channelConfig) {
        this.channelConfig = channelConfig;
    }

    public int getSampleRateInHz() {

        return sampleRateInHz;
    }

    public void setSampleRateInHz(int sampleRateInHz) {
        this.sampleRateInHz = sampleRateInHz;
    }

    public AudioFormatInfo(int sampleRateInHz,int channelConfig, int audioFormat) {
        this.sampleRateInHz = sampleRateInHz;
        this.channelConfig = channelConfig;
        this.audioFormat = audioFormat;

    }

    public AudioFormatInfo(){
        this.sampleRateInHz = 8000;
        this.channelConfig = AudioFormat.CHANNEL_IN_MONO;
        this.audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    }
}
