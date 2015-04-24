package com.example.mazzers.voicerecorder.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextMenu;
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

import com.example.mazzers.voicerecorder.MainActivity;
import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.bookmarks.Bookmark;
import com.example.mazzers.voicerecorder.bookmarks.adapters.ListViewAdapter;
import com.example.mazzers.voicerecorder.utils.BookmarksLoader;
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

public class PlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener, LoaderManager.LoaderCallbacks<HashMap<String, List<Bookmark>>> {
    public static final String PLAYER_TAG = "PLAYER_TAG";
    private ImageButton btnPlay, btnFwd, btnBwd;
    private HashMap<String, List<Bookmark>> mItems;
    private final int LOAD_BOOKMARKS_ID = 5;
    private ListViewAdapter listViewAdapter;
    private final int FILE_DELETE = 1;
    private static SeekBar seekBar;
    private static MediaPlayer mediaPlayer;
    private static final String TAG_LOG = "playerFragment";
    private static String path, prevPath;
    private static ListView listView;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private final Handler handler = new Handler();
    private ArrayList<Bookmark> bookmarkArrayList;
    private final int seekValue = 5000;
    private int[] stamps;
    private MediaPlayer prevPlayer;
    // private boolean running;

    /**
     * On fragment create
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mItems = new HashMap<>();
        Bundle loaderBundle = new Bundle();
        loaderBundle.putString("dir_bookmarks", Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/");

        getLoaderManager().initLoader(LOAD_BOOKMARKS_ID, loaderBundle, this).forceLoad();
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

        mediaPlayer = new MediaPlayer();
        seekBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);
        changeButtonsState();
        listView = (ListView) rootView.findViewById(R.id.player_list);
        Log.i(TAG_LOG, "else");
        bookmarkArrayList = new ArrayList<>();
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
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, FILE_DELETE, 0, "Delete bookmark");

            }
        });


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
                //toggleList();
                newToggle();
        }
        return super.onOptionsItemSelected(item);
    }

    void changeButtonsState() {
        if (path == null) {
            btnPlay.setEnabled(false);
            btnBwd.setEnabled(false);
            btnFwd.setEnabled(false);
            seekBar.setEnabled(false);
        } else {

            btnBwd.setEnabled(true);
            btnFwd.setEnabled(true);
            btnPlay.setEnabled(true);
            seekBar.setEnabled(true);

        }
    }

    void newToggle() {
        MainActivity activity = (MainActivity) getActivity();
        activity.toggleList();
    }

    private final Runnable run = new Runnable() {
        public void run() {
            if (mediaPlayer != null && mediaPlayer.getDuration() > 0) {
                long totalDuration = mediaPlayer.getDuration();

                long currentDuration = mediaPlayer.getCurrentPosition();

                // Displaying Total Duration time
                songTotalDurationLabel.setText("" + Utils.milliSecondsToTimer(totalDuration));
                // Displaying time completed playing
                songCurrentDurationLabel.setText("" + Utils.milliSecondsToTimer(currentDuration));

                // Updating progress bar
                int progress = (Utils.getProgressPercentage(currentDuration, totalDuration));

                seekBar.setProgress(progress);
            }

            handler.postDelayed(this, 100);
        }
    };

    private final Runnable highlight = new Runnable() {

        public void run() {
            // while (running) {
            if (stamps != null && stamps.length > 0) {
                int curr;
                long curr_long;
                int pos;
                curr_long = mediaPlayer.getCurrentPosition();
                curr = (int) (curr_long / 1000);
                pos = Arrays.binarySearch(stamps, curr);
                if (pos >= 0) {
                    Log.d(TAG_LOG, "Time reached");
                    selectPos(pos);
                }


            }
            handler.postDelayed(this, 500);
            //  }
            Log.i(TAG_LOG, "Thread:" + hashCode() + ". End of highlight");
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
                //handler.removeCallbacks(run);
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = Utils.progressToTimer(seekBar.getProgress(), totalDuration);
                mediaPlayer.seekTo(currentPosition);
                updateProgressBar();
                updateListViewSelection();
                mediaPlayer.start();

            } catch (Exception e) {
                handler.removeCallbacks(run);
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
        mediaPlayer.seekTo(0);
        btnPlay.setBackgroundResource(R.drawable.play_icon);
        deselectPos();
        handler.removeCallbacks(highlight);

    }

    @Override
    public Loader<HashMap<String, List<Bookmark>>> onCreateLoader(int id, Bundle args) {
        return new BookmarksLoader(getActivity(), args);
    }

    @Override
    public void onLoadFinished(Loader<HashMap<String, List<Bookmark>>> loader, HashMap<String, List<Bookmark>> data) {
        updateList(data);
        Log.i(TAG_LOG, "onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<HashMap<String, List<Bookmark>>> loader) {

    }

    void updateList(HashMap<String, List<Bookmark>> data) {
        Log.d(TAG_LOG, "updateList:" + data.size());
        mItems.clear();
        mItems.putAll(data);
        if (path != null) {
            File currFile = new File(path);
            String name = currFile.getName().substring(0, currFile.getName().length() - 4);
            if (mItems.containsKey(name)) {
                bookmarkArrayList.clear();
                bookmarkArrayList.addAll(mItems.get(name));
                Log.d(TAG_LOG, "set adapter");
                listViewAdapter.notifyDataSetChanged();
                if (bookmarkArrayList.size() > 0) {
                    stamps = new int[bookmarkArrayList.size()];
                    for (int i = 0; i < bookmarkArrayList.size(); i++) {
                        stamps[i] = bookmarkArrayList.get(i).getTime();
                    }
                }
            }
        }
    }

    /**
     * Button play listener. Start/pause playing
     */
    private class btnPlayClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlay.setBackgroundResource(R.drawable.play_icon);
            } else {
                mediaPlayer.start();
                updateListViewSelection();
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
            Log.d(TAG_LOG, "player prepare fail");
            Log.d(TAG_LOG, e.toString());
        }
        seekBar.setProgress(0);
        seekBar.setMax(100);
        Log.d(TAG_LOG, "PlayerFragment: call updateProgressBar()");
        //running = true;
        updateProgressBar();
        updateListViewSelection();


        Log.d(TAG_LOG, "PlayerFragment: DURATION: " + mediaPlayer.getDuration());

    }

    public void setNewFile(File newFile) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                btnPlay.setBackgroundResource(R.drawable.play_icon);
            }
            handler.removeCallbacks(run);
            handler.removeCallbacks(highlight);
            mediaPlayer.reset();
        }
        path = newFile.getPath();
        getLoaderManager().getLoader(LOAD_BOOKMARKS_ID).forceLoad();
        prepareMediaPlayer();
        changeButtonsState();
    }


    private void selectPos(int i) {
        Log.d(TAG_LOG, "Pos is:" + i);
        View activePosition = listView.getChildAt(i);
        if (activePosition != null) {
            RelativeLayout relativeLayout = (RelativeLayout) activePosition.findViewById(R.id.list_view_relative_layout);
            relativeLayout.setBackgroundResource(R.color.frame_highlighted_pink);
            if (listView.getCount() > 1) {
                for (int index = 0; index < listView.getCount(); index++) {
                    View tempView;
                    if (index != i) {
                        tempView = listView.getChildAt(index);
                        RelativeLayout tempLayout = (RelativeLayout) tempView.findViewById(R.id.list_view_relative_layout);
                        tempLayout.setBackgroundResource(R.color.frame_transparent);
                    }
                }
            }
        } else {
            Log.d(TAG_LOG, "View by pos in null");
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

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG_LOG, "onPause = Stop threads");
        handler.removeCallbacks(run);
        handler.removeCallbacks(highlight);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG_LOG, "onResume = Resume threads");
    }


    public static PlayerFragment createNewInstance() {
        return new PlayerFragment();
    }


    public static PlayerFragment createNewInstance(Bundle args) {
        PlayerFragment playerFragment = new PlayerFragment();
        playerFragment.setArguments(args);
        return playerFragment;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        switch (item.getItemId()) {
            case FILE_DELETE:
                deleteBookmark(index);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    void deleteBookmark(int position) {
        Bookmark temp = bookmarkArrayList.get(position);
        File bookmarkToRemove = new File(temp.getBookmarkPath());
        bookmarkToRemove.delete();
        bookmarkArrayList.remove(position);
        listViewAdapter.notifyDataSetChanged();
    }

}
