package com.example.mazzers.voicerecorder.fragments.base;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;


import java.io.IOException;

/**
 * voiceRecorder application
 * @author Vitaliy Vashchenko A11B0529P
 * Player base for application use
 */
public class Player implements MediaPlayer.OnCompletionListener {
    private String TAG_LOG = "viewLessPlayer";
    private MediaPlayer player;
    private String path;

    /**
     * Base constructor
     */
    public Player() {
        player = new MediaPlayer();
    }

    /**
     * Set player based on file
     *
     * @param path file path
     */
    public void setPlayer(String path) {
        Log.d(TAG_LOG, "prepareMediaPlayer");
        player.reset(); //reset player
        try {
            player.setDataSource(path);
            this.path = path; // set player file
        } catch (IOException e) {
            Log.d(TAG_LOG, e.toString());
        }
        player.setAudioStreamType(AudioManager.STREAM_MUSIC); // set stream type
        try {
            Log.d(TAG_LOG, "Call prepareAsync()");
            player.prepareAsync();  // prepare player

        } catch (Exception e) {
            Log.d(TAG_LOG, e.toString());
        }
    }

    /**
     * Set new file
     *
     * @param newPath new file path
     */
    public void setFile(String newPath) {
        if (!newPath.equals(path)) {
            setPlayer(newPath);
        }
    }

    /**
     * Get active file path
     * @return file path
     */
    public String getPath() {
        return path;
    }

    /**
     * Get MediaPlayer object
     * @return active MediaPlayer
     */
    public MediaPlayer getMediaPlayer() {
        return player;
    }

    /**
     * MediaPlayer onCompletion listener
     *
     * @param mp active MediaPlayer
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG_LOG, "onCompletion");
        mp.stop();
    }

}
