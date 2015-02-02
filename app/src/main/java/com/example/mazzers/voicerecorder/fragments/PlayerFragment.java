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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.bookmarks.Bookmark;
import com.example.mazzers.voicerecorder.bookmarks.adapters.ListViewAdapter;
import com.example.mazzers.voicerecorder.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mazzers on 26. 11. 2014.
 */
public class PlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {
   // private ImageButton btnStop;
    private ImageButton btnPlay;
    private Utils utils;

    private static SeekBar seekBar;
    private static MediaPlayer mediaPlayer;
    private static String TAG_LOG = "myLogs";
    private static String path;
    private static int time;
    private boolean fromDrawer;
    private ListView listView;
    private Bundle bundle;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    Handler handler = new Handler();
    private ArrayList<Bookmark> bookmarkArrayList;

    public PlayerFragment() {
    }


    @Nullable
    @Override
    //todo player view
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.player_layout, container, false);
        btnPlay = (ImageButton) rootView.findViewById(R.id.btnPlay);
        //btnStop = (ImageButton) rootView.findViewById(R.id.btnStopPlayer);
        songCurrentDurationLabel = (TextView) rootView.findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) rootView.findViewById(R.id.songTotalDurationLabel);

        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        btnPlay.setOnClickListener(new btnPlayClick());

        //btnStop.setOnClickListener(new btnStopClick());
        seekBar.setOnSeekBarChangeListener(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        utils = new Utils();
        Log.d(TAG_LOG, "PlayerFragment: try to get arguments ");
        bundle = getArguments();
        Log.d(TAG_LOG, "PlayerFragment: put extra in string");
        if (bundle.getBoolean("fromDrawer")) {
            btnPlay.setEnabled(false);
            //btnStop.setEnabled(false);
        } else {
            path = bundle.getString("filePath");
            bookmarkArrayList = bundle.getParcelableArrayList("bookmarks");
            listView = (ListView) rootView.findViewById(R.id.player_list);
            ListViewAdapter listViewAdapter = new ListViewAdapter(getActivity(),bookmarkArrayList);
            listView.setAdapter(listViewAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bookmark temp = (Bookmark) listView.getItemAtPosition(position);
                    if (mediaPlayer!=null){
                        mediaPlayer.seekTo(temp.getTime()*1000);
                    }
                }
            });
            Log.d(TAG_LOG, "PlayerFragment: path= " + path);
            time = bundle.getInt("fileTime");
            Log.d(TAG_LOG, "PlayerFragment: time=" + time);
            btnPlay.setEnabled(true);
            //btnStop.setEnabled(true);
            prepareMediaPlayer();
            //} catch (Exception e) {
            //    Log.d(TAG_LOG, e.toString());
        }
//        if (path == null) {
//            btnPlay.setEnabled(false);
//        }
        //seekBar.setMax(mediaPlayer.getDuration());

        //pathToFile = startRec.getFilePathAudio();

        return rootView;


    }

    //todo seekbar optimisation
    Runnable run = new Runnable() {
        public void run() {
            //while (shouldRun) {
            //Log.d(TAG_LOG, "PlayerFragment: in run()");
            long totalDuration = mediaPlayer.getDuration();
            //Log.d(TAG_LOG, "PlayerFragment: duration= " + String.valueOf(totalDuration));

            long currentDuration = mediaPlayer.getCurrentPosition();
            //Log.d(TAG_LOG, "PlayerFragment: current=" + String.valueOf(currentDuration));

            // Displaying Total Duration time
            songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            //Log.d(TAG_LOG, "PlayerFragment: progress =" + progress);
            seekBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            handler.postDelayed(this, 100);
            //}
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
        try {


            handler.removeCallbacks(run);
            int totalDuration = mediaPlayer.getDuration();
            int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
            mediaPlayer.seekTo(currentPosition);
            updateProgressBar();
            mediaPlayer.start();

        } catch (Exception e) {
            //Log.e(TAG_LOG, e.toString());
        }

    }

    @Override
    //todo seekbar thread control
    public void onCompletion(MediaPlayer mp) {
        //TODO add coplete seek bar thread stop
        //seekBar.setProgress(0);
        //stopped=true;
        handler.removeCallbacks(run);


    }


    class btnPlayClick implements View.OnClickListener {


        @Override
        public void onClick(View v) {

            //try {

            if (path != null) {
                btnPlay.setEnabled(true);
            }
//            if (mediaPlayer.isPlaying()) {
//                if (mediaPlayer != null) {
//                    mediaPlayer.pause();
//                    // Changing button image to play button
//                    //btnPlay.setImageResource(R.drawable.btn_play);
//                }
            //mediaPlayer = new MediaPlayer();
//            try {
//                mediaPlayer.setDataSource(path);
//            } catch (IOException e) {
//                Log.d(TAG_LOG, e.toString());
//                e.printStackTrace();
//            }
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            try {
//                mediaPlayer.prepare();
//            } catch (Exception e) {
//                Log.d(TAG_LOG, "PlayerFragment: player prepare fail");
//                Log.d(TAG_LOG, e.toString());
//            }
//            mediaPlayer.seekTo(time * 1000);
//            mediaPlayer.start();
            if(mediaPlayer.isPlaying()){
                if(mediaPlayer!=null){
                    mediaPlayer.pause();
                    // Changing button image to play button
                    btnPlay.setBackgroundResource(R.drawable.new_play);
                }
            }else{
                // Resume song
                if(mediaPlayer!=null){
                    mediaPlayer.start();
                    // Changing button image to pause button
                    btnPlay.setBackgroundResource(R.drawable.new_pause);
                }
            }
//            seekBar.setProgress(0);
//            seekBar.setMax(100);
//            Log.d(TAG_LOG, "PlayerFragment: call updateProgressBar()");
//            //stopped=false;
//            updateProgressBar();
//
//            //btnStop.setEnabled(true);
//
//            Log.d(TAG_LOG, "PlayerFragment: DURATION: " + mediaPlayer.getDuration());
            //seekBar.setMax(mediaPlayer.getDuration());


            //} catch (Exception e) {
            //   Log.d(TAG_LOG, "PlayerFragment: NO file to play");
            //    Log.d(TAG_LOG, e.toString());
            //}


        }
    }
    //}


//    class btnStopClick implements View.OnClickListener {
//
//        @Override
//        public void onClick(View v) {
//            Log.d(TAG_LOG, "PlayerFragment: stop Called");
//            if (mediaPlayer != null) {
//                //mediaPlayer.pause();
//                seekBar.setProgress(100);
//                mediaPlayer.stop();
//                handler.removeCallbacks(run);
//
//                //stopped=true;
//                //btnStop.setEnabled(false);
//                //seekBar.setEnabled(false);
//            }
//            // if (mediaPlayer2!=null){mediaPlayer2.stop();}
//
//
//        }
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_LOG, "PlayerFragment: onCrete PlayerFragment");
        super.onCreate(savedInstanceState);

    }


    public void updateProgressBar() {
        Log.d(TAG_LOG, "PlayerFragment: in updateProgressBar");
        handler.postDelayed(run, 100);
    }
    public void prepareMediaPlayer(){
        try {
            mediaPlayer.setDataSource(path);
        } catch (IOException e) {
            Log.d(TAG_LOG, e.toString());
            e.printStackTrace();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.prepare();
        } catch (Exception e) {
            Log.d(TAG_LOG, "PlayerFragment: player prepare fail");
            Log.d(TAG_LOG, e.toString());
        }
        mediaPlayer.seekTo(time * 1000);
        //mediaPlayer.start();
        seekBar.setProgress(0);
        seekBar.setMax(100);
        Log.d(TAG_LOG, "PlayerFragment: call updateProgressBar()");
        //stopped=false;
        updateProgressBar();

        //btnStop.setEnabled(true);

        Log.d(TAG_LOG, "PlayerFragment: DURATION: " + mediaPlayer.getDuration());

    }

}
