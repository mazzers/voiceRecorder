package com.example.mazzers.voicerecorder.fragments;

import android.app.Activity;
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
import android.widget.Toast;

import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.bookmarks.Bookmark;
import com.example.mazzers.voicerecorder.bookmarks.adapters.ListViewAdapter;
import com.example.mazzers.voicerecorder.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Vashchenko Vitaliy A11B0529P
 * PRJ5 - Voice bookmarks
 * <p/>
 * Player fragment. Handles user action and media file control
 */

public class PlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {
    private ImageButton btnPlay, btnFwd, btnBwd;

    private Utils utils;

    private static SeekBar seekBar;
    private static MediaPlayer mediaPlayer;
    private static String TAG_LOG = "playerFragment";
    private static String path;
    private static int time;
    public static ListView listView;
    private Bundle bundle;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private Handler handler = new Handler();
    private ArrayList<Bookmark> bookmarkArrayList;
    private int seekValue = 5000;
    private Activity mainActivity;
    public int[] stamps;

    /**
     * Create player view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    //todo player view
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.player_layout, container, false);
        btnPlay = (ImageButton) rootView.findViewById(R.id.btnPlay);
        btnFwd = (ImageButton) rootView.findViewById(R.id.btnForward);
        btnBwd = (ImageButton) rootView.findViewById(R.id.btnBackward);
        songCurrentDurationLabel = (TextView) rootView.findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) rootView.findViewById(R.id.songTotalDurationLabel);

        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        btnPlay.setOnClickListener(new btnPlayClick());
        btnFwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    int curr = mediaPlayer.getCurrentPosition();
                    if (curr + seekValue <= mediaPlayer.getDuration()) {
                        mediaPlayer.seekTo(curr + seekValue);
                    } else {
                        mediaPlayer.seekTo(mediaPlayer.getDuration());
                    }
                }
            }
        });
        btnBwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    int curr = mediaPlayer.getCurrentPosition();
                    if (curr - seekValue >= 0) {
                        mediaPlayer.seekTo(curr - seekValue);
                    } else {
                        mediaPlayer.seekTo(0);
                    }
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        utils = new Utils();
        bundle = getArguments();
        if (bundle == null) {
            Log.d(TAG_LOG, "bundle is null");
            btnPlay.setEnabled(false);
            Log.d(TAG_LOG, "From drawer");
            seekBar.setEnabled(false);
            btnBwd.setEnabled(false);
            btnFwd.setEnabled(false);
        } else {
            path = bundle.getString("filePath");
            bookmarkArrayList = bundle.getParcelableArrayList("bookmarks");
            stamps = new int[bookmarkArrayList.size()];
            makeStamps();
            listView = (ListView) rootView.findViewById(R.id.player_list);
            ListViewAdapter listViewAdapter = new ListViewAdapter(getActivity(), bookmarkArrayList);
            listView.setAdapter(listViewAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bookmark temp = (Bookmark) listView.getItemAtPosition(position);
                    if (mediaPlayer != null) {
                        mediaPlayer.seekTo(temp.getTime() * 1000);
                    }
                }
            });
            Log.d(TAG_LOG, "PlayerFragment: path= " + path);
            time = bundle.getInt("fileTime");
            Log.d(TAG_LOG, "PlayerFragment: time=" + time);
            btnPlay.setEnabled(true);
            seekBar.setEnabled(true);
            btnBwd.setEnabled(true);
            btnFwd.setEnabled(true);
            prepareMediaPlayer();

        }


        //setRetainInstance(true);
        return rootView;


    }


    Runnable run = new Runnable() {
        public void run() {
            //while (shouldRun) {
            //Log.d(TAG_LOG, "PlayerFragment: in run()");
            if (mediaPlayer != null) {
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

                seekBar.setProgress(progress);
            }

            // Running this thread after 100 milliseconds
            handler.postDelayed(this, 100);
            //}
        }
    };

//    Runnable highlight = new Runnable() {
//
//        public void run() {
//            int curr;
//            int pos;
//            //Log.d(TAG_LOG,"highrlight thread");
//                curr = mediaPlayer.getCurrentPosition();
//                pos = Arrays.asList(stamps).indexOf(curr);
//                if (pos!=-1){
//                    //selectPos(pos);
//                    Log.d(TAG_LOG,"time reached");
//                }
//
//            handler.postDelayed(this,500);
//        }
//    };

//    Runnable setStamps = new Runnable() {
//
//        public void run() {
//            Log.d(TAG_LOG,"setStamps");
//            for(int i=0;i<bookmarkArrayList.size();i++){
//                stamps[i] = bookmarkArrayList.get(i).getTime();
//                Log.d(TAG_LOG,String.valueOf(stamps[i]));
//            }
//        }
//    };

    /**
     * Seekbar progres changed
     *
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    /**
     * Touch tracking
     *
     * @param seekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(run);

    }

    /**
     * Handle action when touch stops
     *
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mediaPlayer != null) {
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


    }

    /**
     * On playback complete
     *
     * @param mp
     */
    @Override

    public void onCompletion(MediaPlayer mp) {

        Toast.makeText(mainActivity, "End of file", Toast.LENGTH_SHORT).show();

        //handler.removeCallbacks(run);
        mediaPlayer.seekTo(0);
        btnPlay.setBackgroundResource(R.drawable.new_play);

    }

    /**
     * Button play listener. Start/pause playing
     */
    class btnPlayClick implements View.OnClickListener {


        @Override
        public void onClick(View v) {

            //try {

            if (path != null) {
                btnPlay.setEnabled(true);
            }
            if (mediaPlayer.isPlaying()) {
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                    // Changing button image to play button
                    btnPlay.setBackgroundResource(R.drawable.new_play);
                }
            } else {
                // Resume song
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                    // Changing button image to pause button
                    btnPlay.setBackgroundResource(R.drawable.new_pause);
                }
            }


        }
    }


    /**
     * On fragment create
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.d(TAG_LOG, "PlayerFragment: onCrete PlayerFragment");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mainActivity = getActivity();
    }

    /**
     * Update seekbar
     */
    public void updateProgressBar() {
        //Log.d(TAG_LOG, "PlayerFragment: in updateProgressBar");
        handler.postDelayed(run, 100);
    }

//    public void updateListViewSelection(){
//        handler.postDelayed(highlight,500);
//    }

    /**
     * Prepare music player
     */
    public void prepareMediaPlayer() {
        //handler.post(setStamps);
        try {
            mediaPlayer.setDataSource(path);
            Log.d(TAG_LOG, "Set data source=" + path);
        } catch (IOException e) {
            Log.d(TAG_LOG, e.toString());
            e.printStackTrace();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        Log.d(TAG_LOG, "Set data type");
        try {
            mediaPlayer.prepare();
            Log.d(TAG_LOG, "Call prepare");
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
        //updateListViewSelection();

        //btnStop.setEnabled(true);

        Log.d(TAG_LOG, "PlayerFragment: DURATION: " + mediaPlayer.getDuration());

    }

    public static void selectPos(int i) {
        listView.setSelection(i);
    }

    public void makeStamps() {
        for (int i = 0; i < bookmarkArrayList.size(); i++) {
            stamps[i] = bookmarkArrayList.get(i).getTime();
        }

    }


    public static PlayerFragment createNewInstance() {
        PlayerFragment playerFragment = new PlayerFragment();
//        Bundle args = new Bundle();
//        args.putString("fragment_tag",tag);
//        playerFragment.setArguments(args);
        return playerFragment;

    }

    public static PlayerFragment createNewInstance(String tag, Bundle args) {
        PlayerFragment playerFragment = new PlayerFragment();

        //args.putParcelableArrayList("bookmarks",bookmarks);
        playerFragment.setArguments(args);
        return playerFragment;
    }


}
