package com.example.mazzers.voicerecorder.fragments;

import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.utils.Utils;

import java.io.IOException;

/**
 * Created by mazzers on 26. 11. 2014.
 */
public class PlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {
    private Button btnStop;
    private Button btnPlay;
    private Utils utils;

    private static SeekBar seekBar;
    private static MediaPlayer mediaPlayer;
    private static String TAG_LOG = "myLogs";
    private static String path;
    private static int time;
    private static boolean bookmark = false;
    private Bundle bundle;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    Handler handler = new Handler();
    public PlayerFragment() {
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                View rootView = inflater.inflate(R.layout.player_layout,container,false);
        btnPlay = (Button) rootView.findViewById(R.id.btnPlay);
        btnStop = (Button) rootView.findViewById(R.id.btnStopPlayer);
        songCurrentDurationLabel = (TextView) rootView.findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) rootView.findViewById(R.id.songTotalDurationLabel);

        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        btnPlay.setOnClickListener(new btnPlayClick());

        btnStop.setOnClickListener(new btnStopClick());
        seekBar.setOnSeekBarChangeListener(this);
        utils = new Utils();
        Log.d(TAG_LOG,"PlayerFragment: try to get arguments ");
        bundle = getArguments();
        Log.d(TAG_LOG,"PlayerFragment: put extra in string");
        try {
            path = bundle.getString("filePath");
            Log.d(TAG_LOG,"PlayerFragment: path= "+path);
            time = bundle.getInt("fileTime");
            Log.d(TAG_LOG,"PlayerFragment: time="+time);
        }catch (Exception e){
            Log.d(TAG_LOG,e.toString());
        }
        if (path==null){
            btnPlay.setEnabled(false);
        }
        //seekBar.setMax(mediaPlayer.getDuration());

        //pathToFile = startRec.getFilePathAudio();

        return rootView;


    }
    Runnable run = new Runnable() {
        public void run() {
            Log.d(TAG_LOG,"PlayerFragment: in run()");
            long totalDuration = mediaPlayer.getDuration();
            Log.d(TAG_LOG,"PlayerFragment: duration= "+String.valueOf(totalDuration));

            long currentDuration = mediaPlayer.getCurrentPosition();
            Log.d(TAG_LOG,"PlayerFragment: current="+String.valueOf(currentDuration));

            // Displaying Total Duration time
            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            Log.d(TAG_LOG,"PlayerFragment: progress"+ progress);
            seekBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            handler.postDelayed(this, 100);
        }
    };



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
         handler.removeCallbacks(run);

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(run);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(),totalDuration);
        mediaPlayer.seekTo(currentPosition);
        updateProgressBar();


    }

    @Override
    public void onCompletion(MediaPlayer mp) {


    }


    class btnPlayClick implements View.OnClickListener {


        @Override
        public void onClick(View v) {

            try {

                if (path!=null){
                    btnPlay.setEnabled(true);
                }
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(path);
                } catch (IOException e) {
                    Log.d(TAG_LOG,e.toString());
                    e.printStackTrace();
                }
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.prepare();
                }catch (Exception e){
                    Log.d(TAG_LOG,"PlayerFragment: player prepare fail");
                    Log.d(TAG_LOG,e.toString());
                }
                mediaPlayer.seekTo(time*1000);
                mediaPlayer.start();
                seekBar.setProgress(0);
                seekBar.setMax(100);
                Log.d(TAG_LOG,"PlayerFragment: call updateProgressBar()");
                updateProgressBar();
                Log.d(TAG_LOG,"PlayerFragment: DURATION: "+mediaPlayer.getDuration());
                //seekBar.setMax(mediaPlayer.getDuration());


            }catch (Exception e){
                Log.d(TAG_LOG,"PlayerFragment: NO file to play");
                Log.d(TAG_LOG,e.toString());
            }


        }
    }


    class btnStopClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if (mediaPlayer!=null) {
                mediaPlayer.pause();
            }
           // if (mediaPlayer2!=null){mediaPlayer2.stop();}


        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_LOG,"PlayerFragment: onCrete PlayerFragment");
        super.onCreate(savedInstanceState);

    }



    public void updateProgressBar() {
        Log.d(TAG_LOG,"PlayerFragment: in updateProgressBar");
        handler.postDelayed(run, 100);
    }


}
