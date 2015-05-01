package com.example.mazzers.voicerecorder.fragments.base;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;


import java.io.IOException;

/**
 * Created by mazzers on 30. 4. 2015.
 */
public class Player {
    private String TAG_LOG = "viewLessPlayer";
    private MediaPlayer player;
    private boolean prepared;
    private String path;

    public Player() {
        player = new MediaPlayer();
    }

    public void setPlayer(String path) {
        Log.d(TAG_LOG, "prepareMediaPlayer");
        player.reset();
        try {
            player.setDataSource(path);
            this.path = path;
        } catch (IOException e) {
            Log.d(TAG_LOG, e.toString());
        }
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            Log.d(TAG_LOG, "Call prepareAsync()");
            player.prepareAsync();

        } catch (Exception e) {
            Log.d(TAG_LOG, e.toString());
        }
    }

    public void setFile(String newPath) {
        if (newPath != path) {
            setPlayer(newPath);
        }
    }

    public void pausePlayer() {
        if (player != null) {
            player.pause();
        }
    }

    public void startMPlayer() {
        if (player != null) {
            player.start();
        }
    }

    public String getPath() {
        return path;
    }


    public void reset() {
        player.reset();
    }

    public MediaPlayer getMediaPlayer() {
        return player;
    }

//    @Override
//    public boolean onError(MediaPlayer mp, int what, int extra) {
//        return false;
//    }
}
