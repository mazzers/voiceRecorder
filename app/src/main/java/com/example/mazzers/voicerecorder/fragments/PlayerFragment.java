package com.example.mazzers.voicerecorder.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mazzers.voicerecorder.MainActivity;
import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.bookmarks.Bookmark;
import com.example.mazzers.voicerecorder.bookmarks.adapters.ListViewAdapter;
import com.example.mazzers.voicerecorder.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Vashchenko Vitaliy A11B0529P
 * PRJ5 - Voice bookmarks
 * <p/>
 * Player fragment. Handles user action and media file control
 */

public class PlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {
    public static final String PLAYER_TAG = "PLAYER_TAG";
    private ImageButton btnPlay;
    private List<String> listDataHeader;
    private HashMap<String, List<Bookmark>> mItems;
    private Utils utils;
    private ListViewAdapter listViewAdapter;
    private static SeekBar seekBar;
    private static MediaPlayer mediaPlayer;
    private static String TAG_LOG = "playerFragment";
    private static String path;
    private static int time;
    private static ListView listView;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private Handler handler = new Handler();
    private ArrayList<Bookmark> bookmarkArrayList;
    private int seekValue = 5000;
    private int[] stamps;
    private long prevDuration;
    private MediaPlayer prevPlayer;

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
        setHasOptionsMenu(true);
        Activity mainActivity = getActivity();

        utils = new Utils();

    }

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //todo broken files skip
        //todo hightlight
        //todo add new while listening
        //todo context menu remove
        View rootView = inflater.inflate(R.layout.player_layout, container, false);
        btnPlay = (ImageButton) rootView.findViewById(R.id.btnPlay);
        ImageButton btnFwd = (ImageButton) rootView.findViewById(R.id.btnForward);
        ImageButton btnBwd = (ImageButton) rootView.findViewById(R.id.btnBackward);
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


        mediaPlayer = new MediaPlayer();
        seekBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);
        Bundle bundle = getArguments();
        if (bundle == null) {
            btnPlay.setEnabled(false);
            seekBar.setEnabled(false);
            btnBwd.setEnabled(false);
            btnFwd.setEnabled(false);
        } else {
            listView = (ListView) rootView.findViewById(R.id.player_list);
            path = bundle.getString("filePath");
            prepareListData();
            prepareMediaPlayer();
            File currFile = new File(path);
            String name = currFile.getName().substring(0, currFile.getName().length() - 4);
            int i = listDataHeader.indexOf(name);
            if (i >= 0) {
                bookmarkArrayList = new ArrayList<>(mItems.get(listDataHeader.get(i)));
                listViewAdapter = new ListViewAdapter(getActivity(), bookmarkArrayList);
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
                stamps = new int[bookmarkArrayList.size()];
                makeStamps();
            }


        }

        return rootView;


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.player_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.player_menu_toggle_list:
                toggleList();
        }
        return super.onOptionsItemSelected(item);
    }

    void toggleList() {
        if (MainActivity.getPlayerFragment() != this) {
            MainActivity.setPlayerFragment(this);
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);
        ft.hide(this);
        RecordListFragment recordListFragment = (RecordListFragment) getFragmentManager().findFragmentByTag(RecordListFragment.LIST_TAG);
        if (recordListFragment == null) {
            //Toast.makeText(getActivity(), "recordListFragment is null", Toast.LENGTH_SHORT).show();
            recordListFragment = RecordListFragment.createNewInstance();
            ft.add(R.id.container, recordListFragment, RecordListFragment.LIST_TAG);
        }
        ft.show(recordListFragment);
        ft.addToBackStack(null).commit();
    }

    private Runnable run = new Runnable() {
        public void run() {
            //Log.d(TAG_LOG, "PlayerFragment: in run()");
            if (mediaPlayer != null && mediaPlayer.getDuration() > 0) {
                long totalDuration = mediaPlayer.getDuration();

                long currentDuration = mediaPlayer.getCurrentPosition();

                // Displaying Total Duration time
                songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
                // Displaying time completed playing
                songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));

                // Updating progress bar
                int progress = (utils.getProgressPercentage(currentDuration, totalDuration));

                seekBar.setProgress(progress);
            }

            // Running this thread after 100 milliseconds
            handler.postDelayed(this, 100);
            //}
        }
    };

    private Runnable highlight = new Runnable() {

        public void run() {
            int curr;
            long curr_long;
            int pos;
            curr_long = mediaPlayer.getCurrentPosition();
            curr = (int) (curr_long / 1000);
            pos = Arrays.binarySearch(stamps, curr);
            if (pos >= 0) {
                Log.d(TAG_LOG, "time reached " + String.valueOf(stamps[pos]));
                selectPos(pos);
            }

            handler.postDelayed(this, 500);
        }
    };

    private Runnable setStamps = new Runnable() {

        public void run() {
            Log.d(TAG_LOG, "setStamps");
            for (int i = 0; i < bookmarkArrayList.size(); i++) {
                stamps[i] = bookmarkArrayList.get(i).getTime();
                Log.d(TAG_LOG, String.valueOf(stamps[i]));
            }
        }
    };

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
                handler.removeCallbacks(highlight);
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
                mediaPlayer.seekTo(currentPosition);
                updateProgressBar();
                updateListViewSelection();
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

        //Toast.makeText(mainActivity, "End of file", Toast.LENGTH_SHORT).show();

        //handler.removeCallbacks(run);
        //handler.removeCallbacks(highlight);
        mediaPlayer.seekTo(0);
        btnPlay.setBackgroundResource(R.drawable.play_icon);
        deselectPos();

    }

    /**
     * Button play listener. Start/pause playing
     */
    private class btnPlayClick implements View.OnClickListener {


        @Override
        public void onClick(View v) {

            //try {

            if (path != null) {
                btnPlay.setEnabled(true);
            }
            if (mediaPlayer.isPlaying()) {

                mediaPlayer.pause();
                // Changing button image to play button
                btnPlay.setBackgroundResource(R.drawable.play_icon);

            } else {
                // Resume song

                mediaPlayer.start();
                // Changing button image to pause button
                btnPlay.setBackgroundResource(R.drawable.pause_icon);

            }


        }
    }


    /**
     * Update seekbar
     */
    void updateProgressBar() {
        //Log.d(TAG_LOG, "PlayerFragment: in updateProgressBar");
        handler.postDelayed(run, 100);
    }

    void updateListViewSelection() {
        handler.postDelayed(highlight, 500);
    }

    /**
     * Prepare music player
     */
    void prepareMediaPlayer() {
        handler.post(setStamps);
        try {
            mediaPlayer.setDataSource(path);
            Log.d(TAG_LOG, "Set data source=" + path);
        } catch (IOException e) {
            Log.d(TAG_LOG, e.toString());
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
        seekBar.setProgress(0);
        seekBar.setMax(100);
        Log.d(TAG_LOG, "PlayerFragment: call updateProgressBar()");
        updateProgressBar();
        updateListViewSelection();


        Log.d(TAG_LOG, "PlayerFragment: DURATION: " + mediaPlayer.getDuration());

    }

    private void prepareListData() {
        //todo search in bookmarks
        listDataHeader = new ArrayList<>();
        mItems = new HashMap<>();

        List<Bookmark> tempArray = new ArrayList<>();
        Bookmark[] bookmarksList = MainActivity.getBookmarks();

        if (bookmarksList != null && bookmarksList.length > 0) {

            String groupName = bookmarksList[0].getName();
            int groupCount = 1;
            listDataHeader.add(groupName);

            for (int i = 0; i < bookmarksList.length; i++) {
                if (!bookmarksList[i].getName().equals(groupName)) {
                    //new group
                    //add array to previous group
                    mItems.put(listDataHeader.get(groupCount - 1), tempArray);
                    //increase group pointer
                    groupCount++;
                    //reinitialize array
                    tempArray = new ArrayList<>();
                    //set new group name
                    groupName = bookmarksList[i].getName();
                    //add groupname to array
                    listDataHeader.add(groupName);
                }

                tempArray.add(bookmarksList[i]);

            }
            mItems.put(listDataHeader.get(groupCount - 1), tempArray);

        } else {
            Log.d(TAG_LOG, "no bookmarks");
            Toast.makeText(getActivity(), "No bookmarks", Toast.LENGTH_LONG).show();
        }
    }

    private void selectPos(int i) {
        //todo hightlight
        View activePosition = listView.getChildAt(i);
        RelativeLayout relativeLayout = (RelativeLayout) activePosition.findViewById(R.id.list_view_relative_layout);
        relativeLayout.setBackgroundResource(R.color.frame_highlighted);
        for (int index = 0; index < listView.getCount(); index++) {
            View tempView;
            if (index != i) {
                tempView = listView.getChildAt(index);
                RelativeLayout tempLayout = (RelativeLayout) tempView.findViewById(R.id.list_view_relative_layout);
                tempLayout.setBackgroundResource(R.color.frame_transparent);
            }
        }
    }

    void deselectPos() {
        for (int index = 0; index < listView.getCount(); index++) {
            View tempView;
            tempView = listView.getChildAt(index);
            RelativeLayout tempLayout = (RelativeLayout) tempView.findViewById(R.id.list_view_relative_layout);
            tempLayout.setBackgroundResource(R.color.frame_transparent);
        }
    }

    void makeStamps() {
        for (int i = 0; i < bookmarkArrayList.size(); i++) {
            stamps[i] = bookmarkArrayList.get(i).getTime();
        }

    }

    @Override
    public void onDetach() {
        //todo save playing position
        super.onDetach();
    }

    public static PlayerFragment createNewInstance() {
//        Bundle args = new Bundle();
//        args.putString("fragment_tag",tag);
//        playerFragment.setArguments(args);
        return new PlayerFragment();

    }

    public static PlayerFragment createNewInstance(Bundle args) {
        PlayerFragment playerFragment = new PlayerFragment();

        //args.putParcelableArrayList("bookmarks",bookmarks);
        playerFragment.setArguments(args);
        return playerFragment;
    }


}
