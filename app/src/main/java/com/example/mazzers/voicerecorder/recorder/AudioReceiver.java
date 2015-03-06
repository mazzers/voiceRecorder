package com.example.mazzers.voicerecorder.recorder;

import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by mazzers on 2. 3. 2015.
 */
public class AudioReceiver {

    public boolean ismIsRecording() {
        return mIsRecording;
    }

    public boolean mIsRecording;
    private AudioFormatInfo format;
    private AudioRecord mRecord;
    private String TAG_LOG = "myLogs";
    private Thread recordingThread = null;
    int bufferSize;
    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format

    private final int BUFF_COUNT = 32;

    public AudioReceiver(AudioFormatInfo format) {
        this.format = format;
        this.bufferSize = AudioRecord.getMinBufferSize(format.getSampleRateInHz(),
                format.getChannelConfig(), format.getAudioFormat());

    }



    public void stopRecording()
    {
        if (null != mRecord) {
            mIsRecording = false;
            mRecord.stop();
            mRecord.release();
            mRecord = null;
            recordingThread = null;
        }
    }




    public byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }
    public void startRecording() {

        mRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                format.getSampleRateInHz(), format.getChannelConfig(),
                format.getAudioFormat(), BufferElements2Rec * BytesPerElement);

        mRecord.startRecording();
        mIsRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    private void writeAudioDataToFile() {
        // Write the output audio in byte

        String filePath = "/sdcard/voice8K16bitmono.pcm";
        short sData[] = new short[BufferElements2Rec];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (mIsRecording) {
            // gets the voice output from microphone to byte format

            mRecord.read(sData, 0, BufferElements2Rec);
            System.out.println("Short wirting to file" + sData.toString());
            try {
                // // writes the data to file from buffer
                // // stores the voice buffer
                byte bData[] = short2byte(sData);
                os.write(bData, 0, BufferElements2Rec * BytesPerElement);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
