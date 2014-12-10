package com.example.mazzers.voicerecorder.fragments;

import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.bookmarks.ParseBookmarkFiles;
import com.example.mazzers.voicerecorder.recorder.startRec;

import java.io.IOException;

/**
 * Created by mazzers on 26. 11. 2014.
 */
public class PlayerFragment extends Fragment {
    private Button btnStop, btnChoose, btnShow;
    private static Button btnPlay;
   // String pathToFile;
    private MediaPlayer mediaPlayer;
    private static MediaPlayer mediaPlayer2;
    private static String TAG_LOG = "myLogs";
    //private static String path;
    private static int time=0;
    private static boolean bookmark = false;
    public PlayerFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                View rootView = inflater.inflate(R.layout.player_layout,container,false);
        btnChoose = (Button) rootView.findViewById(R.id.btnChoose);
        btnPlay = (Button) rootView.findViewById(R.id.btnPlay);
        btnStop = (Button) rootView.findViewById(R.id.btnStopPlayer);
        btnShow = (Button) rootView.findViewById(R.id.btnShow);
        btnPlay.setOnClickListener(new btnPlayClick());
        btnShow.setOnClickListener(new btnShowClick());
        btnChoose.setOnClickListener(new btnChooseClick());
        btnStop.setOnClickListener(new btnStopClick());
        //pathToFile = startRec.getFilePathAudio();
        return rootView;
    }

    class btnPlayClick implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(startRec.getFilePathAudio());
            } catch (IOException e) {
                Log.d(TAG_LOG,e.toString());
                e.printStackTrace();
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.prepare();
            }catch (Exception e){
                Log.d(TAG_LOG,"player prepare fail");
                Log.d(TAG_LOG,e.toString());
            }
            mediaPlayer.start();


        }
    }

    class btnShowClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Log.d(TAG_LOG, "btnShowClick on click");

           // Thread parseBookmarkFiles = new Thread(new ParseBookmarkFiles());
           // parseBookmarkFiles.start();


        }
    }
    class btnChooseClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {


        }
    }
    class btnStopClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if (mediaPlayer!=null) {
                mediaPlayer.stop();
            }
            if (mediaPlayer2!=null){mediaPlayer2.stop();}


        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_LOG,"oncrete PLayerfragment");
        super.onCreate(savedInstanceState);
        Thread parseBookmarkFiles = new Thread(new ParseBookmarkFiles());
        parseBookmarkFiles.start();
    }

    public static void callBookmarkPlay(String pathTemp, int timeTemp){
        mediaPlayer2 = new MediaPlayer();
        try {
            mediaPlayer2.setDataSource(pathTemp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer2.prepare();
        }catch (Exception e){
            Log.d(TAG_LOG,"player prepare fail");
            Log.d(TAG_LOG,e.toString());
        }

        //Toast.makeText(getActivity(),String.valueOf(timeTemp),Toast.LENGTH_SHORT).show();
        mediaPlayer2.seekTo(timeTemp*1000);
        mediaPlayer2.start();



    }

    public void startPlayOn(){

    }
}
