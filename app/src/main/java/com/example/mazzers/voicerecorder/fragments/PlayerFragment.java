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

import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.bookmarks.ParseBookmarkFiles;
import com.example.mazzers.voicerecorder.utils.Utils;

import java.io.IOException;

/**
 * Created by mazzers on 26. 11. 2014.
 */
public class PlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {
    private Button btnStop, btnChoose, btnShow;
    private static Button btnPlay;
    private Utils utils;
   // String pathToFile;
    private static SeekBar seekBar;
    //private MediaPlayer mediaPlayer;
    private static MediaPlayer mediaPlayer;
    private static String TAG_LOG = "myLogs";
    private static String path;
    private static int time=0;
    private static boolean bookmark = false;
    private Bundle bundle;
    Handler handler;
    public PlayerFragment() {
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                View rootView = inflater.inflate(R.layout.player_layout,container,false);
        //btnChoose = (Button) rootView.findViewById(R.id.btnChoose);
        btnPlay = (Button) rootView.findViewById(R.id.btnPlay);
        btnStop = (Button) rootView.findViewById(R.id.btnStopPlayer);
        //btnShow = (Button) rootView.findViewById(R.id.btnShow);
        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        btnPlay.setOnClickListener(new btnPlayClick());
        //btnShow.setOnClickListener(new btnShowClick());
        //btnChoose.setOnClickListener(new btnChooseClick());
        btnStop.setOnClickListener(new btnStopClick());
        seekBar.setOnSeekBarChangeListener(this);
        Log.d(TAG_LOG,"PlayerFragment: try to get arguments ");
        bundle = getArguments();
        Log.d(TAG_LOG,"PlayerFragment: put extra in string");
        try {
            path = bundle.getString("filePath");
            time = bundle.getInt("fileTime");
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
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            //songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            //songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
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

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

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
                mediaPlayer.stop();
            }
           // if (mediaPlayer2!=null){mediaPlayer2.stop();}


        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_LOG,"PlayerFragment: onCrete PlayerFragment");
        super.onCreate(savedInstanceState);
        Thread parseBookmarkFiles = new Thread(new ParseBookmarkFiles());
        parseBookmarkFiles.start();
    }

    public void callBookmarkPlay(String pathTemp, int timeTemp){



        //btnPlay.callOnClick();

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(pathTemp);
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

        //Toast.makeText(getActivity(),String.valueOf(timeTemp),Toast.LENGTH_SHORT).show();
        mediaPlayer.seekTo(timeTemp*1000);

        mediaPlayer.start();
        seekBar.setProgress(0);
        seekBar.setMax(100);
        //updateProgressBar();
        //seekBar.setMax(mediaPlayer.getDuration());
        Log.d(TAG_LOG,"PlayerFragment: DURATION: "+mediaPlayer.getDuration());



    }

    public void updateProgressBar() {
        handler.postDelayed(run, 100);
    }

    public void setPath(String path){
        this.path = path;
    }

    public static void setTime(int i){
        time = i;
    }


    public void startPlayOn(){

    }

}
