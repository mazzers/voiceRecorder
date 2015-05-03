package com.example.mazzers.voicerecorder.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.example.mazzers.voicerecorder.MainActivity;
import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.bookmarks.Bookmark;
import com.example.mazzers.voicerecorder.bookmarks.adapters.BookmarkListAdapter;
import com.example.mazzers.voicerecorder.fragments.base.Player;
import com.example.mazzers.voicerecorder.utils.BookmarksLoader;
import com.example.mazzers.voicerecorder.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * voiceRecorder application
 * @author Vitaliy Vashchenko A11B0529P
 * Player fragment with UI elements
 */

public class PlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, LoaderManager.LoaderCallbacks<HashMap<String, List<Bookmark>>>, MediaPlayer.OnCompletionListener {
    public static final String PLAYER_TAG = "PLAYER_TAG"; // fragment tag
    private static final String TAG_LOG = "playerFragment"; // Logger tag
    private final Handler handler = new Handler();

    private ImageButton btnPlay, btnFwd, btnBwd; // UI elements
    private static SeekBar seekBar;
    private static ListView listView;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private ProgressDialog progressDialog;

    private HashMap<String, List<Bookmark>> mItems; // bookmark list elements
    private final int LOAD_BOOKMARKS_ID = 5;
    private BookmarkListAdapter bookmarkListAdapter;
    private ArrayList<Bookmark> bookmarkArrayList;
    private final int FILE_DELETE = 1;
    private int[] stamps;

    private static MediaPlayer mediaPlayer; // player elements
    private static String path;
    private final int seekValue = 5000;
    private Player player;
    private boolean fileIsValid = true;

    /**
     * On fragment create
     *
     * @param savedInstanceState previous state
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
     * @param inflater layout inflater
     * @param container fragment container
     * @param savedInstanceState fragment state
     * @return fragment view
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
        MainActivity activity = (MainActivity) getActivity();
        player = activity.getPlayer();
        mediaPlayer = player.getMediaPlayer();

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
                    progressDialog.dismiss();
                    changeButtonsState(false);
                    Toast.makeText(getActivity(), "Load timeout", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG_LOG, "some error");
                    switch (extra) {
                        case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                            Toast.makeText(getActivity(), "Unsupported", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            changeButtonsState(false);
                            break;
                        case MediaPlayer.MEDIA_ERROR_MALFORMED:
                            Toast.makeText(getActivity(), "Malformed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            changeButtonsState(false);
                            break;
                    }
                }
                return false;
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i(TAG_LOG, "player Prepared");
                progressDialog.dismiss();
                continuePrepare();
            }
        });
        seekBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);
        if (savedInstanceState == null) {
            changeButtonsState(false);
        }
        listView = (ListView) rootView.findViewById(R.id.player_list);
        Log.i(TAG_LOG, "else");
        bookmarkArrayList = new ArrayList<>();
        bookmarkListAdapter = new BookmarkListAdapter(getActivity(), bookmarkArrayList);
        listView.setAdapter(bookmarkListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bookmark temp = (Bookmark) listView.getItemAtPosition(position);
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(temp.getTime() * 1000);
                    updateListViewSelection();
                    updateProgressBar();
                }
            }
        });
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, FILE_DELETE, 0, "Delete bookmark");

            }
        });
        if (player.getPath() != null) { //player has active file
            setNewFile(player.getPath()); // set fragment view based on active file
            if (fileIsValid && mediaPlayer.isPlaying()) {
                restorePlaying();
            } else {
                restorePaused();
            }
        }


        return rootView;
    }

    /**
     * Create options menu
     *
     * @param menu     menu layout
     * @param inflater menu inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.player_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    /**
     * Selection listener
     *
     * @param item selected item
     * @return action from selected item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.player_menu_toggle_list:
                newToggle();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Change buttons state
     * @param state new state
     */
    void changeButtonsState(Boolean state) {
        btnPlay.setEnabled(state);
        btnBwd.setEnabled(state);
        btnFwd.setEnabled(state);
        seekBar.setEnabled(state);
    }

    /**
     * Toggle record list
     */
    void newToggle() {
        Log.d(TAG_LOG, "newToggle");
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPlay.setBackgroundResource(R.drawable.play_icon);
        }
        handler.removeCallbacks(run);
        handler.removeCallbacks(highlight);
        MainActivity activity = (MainActivity) getActivity();
        activity.toggleList();
    }

    /**
     * Thread for seekBar updating
     */
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
    /**
     * Thread got list items highlighting
     */
    private final Runnable highlight = new Runnable() {

        public void run() {
            if (stamps != null && stamps.length > 0) {
                int curr;
                long curr_long;
                int pos;
                curr_long = mediaPlayer.getCurrentPosition();
                curr = (int) (curr_long / 1000);
                pos = Arrays.binarySearch(stamps, curr);
                if (pos >= 0) {
                    selectPos(pos); // select active position
                }


            }
            handler.postDelayed(this, 500);
        }
    };


    /**
     * SeekBar progress changed
     *
     * @param seekBar selected seekBar
     * @param progress seekBar progress
     * @param fromUser progress invoked by user
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    /**
     * Touch tracking
     *
     * @param seekBar selected seekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(run);
        handler.removeCallbacks(highlight);

    }

    /**
     * Handle action when touch stops
     *
     * @param seekBar selected seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mediaPlayer != null) {
            try {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = Utils.progressToTimer(seekBar.getProgress(), totalDuration);
                mediaPlayer.seekTo(currentPosition);
                updateProgressBar();
                updateListViewSelection();
                mediaPlayer.start();

            } catch (Exception e) {
                handler.removeCallbacks(run);
                handler.removeCallbacks(highlight);
            }
        }
    }

    /**
     * On playback complete
     *
     * @param mp selected MediaPlayer
     */
    @Override

    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG_LOG, "onCompletion");
        if (fileIsValid) {
            mp.seekTo(0);  //set player to start on completion
            seekBar.setProgress(0);
            songCurrentDurationLabel.setText("00:00:00");
            btnPlay.setBackgroundResource(R.drawable.play_icon);
            deselectPos();
            handler.removeCallbacks(highlight);
            handler.removeCallbacks(run);
        }

    }

    /**
     * Create loader
     * @param id loader id
     * @param args loader arguments
     * @return new loader
     */
    @Override
    public Loader<HashMap<String, List<Bookmark>>> onCreateLoader(int id, Bundle args) {
        return new BookmarksLoader(getActivity(), args);
    }

    /**
     * Process loader dara
     * @param loader active loader
     * @param data loader data
     */
    @Override
    public void onLoadFinished(Loader<HashMap<String, List<Bookmark>>> loader, HashMap<String, List<Bookmark>> data) {
        updateList(data);
        MainActivity.setmItems(data);
        Log.i(TAG_LOG, "onLoadFinished");
    }

    /**
     * Reset loader
     * @param loader active loader
     */
    @Override
    public void onLoaderReset(Loader<HashMap<String, List<Bookmark>>> loader) {

    }

    /**
     * Update list from new data
     * @param data new data
     */
    void updateList(HashMap<String, List<Bookmark>> data) {
        Log.d(TAG_LOG, "updateList:" + data.size());
        mItems.clear();
        mItems.putAll(data);
        if (path != null) {
            if (mItems.containsKey(path)) {
                bookmarkArrayList.clear();
                bookmarkArrayList.addAll(mItems.get(path));
                Log.d(TAG_LOG, "set adapter, size:" + bookmarkArrayList.size());

                bookmarkListAdapter.notifyDataSetChanged();
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
                handler.removeCallbacks(highlight);
                handler.removeCallbacks(run);
                btnPlay.setBackgroundResource(R.drawable.play_icon);
            } else {
                mediaPlayer.start();
                updateListViewSelection();
                updateProgressBar();
                btnPlay.setBackgroundResource(R.drawable.pause_icon);

            }


        }
    }

    /**
     * Stop threads on fragment destroy
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(run);
        handler.removeCallbacks(highlight);
    }

    /**
     * Update seekBar
     */
    void updateProgressBar() {
        //Log.d(TAG_LOG, "PlayerFragment: in updateProgressBar");
        handler.postDelayed(run, 100);
    }

    /**
     * Start list selection thread
     */
    void updateListViewSelection() {
        handler.postDelayed(highlight, 500);
    }

    void continuePrepare() {
        changeButtonsState(true);
        seekBar.setProgress(0);
        seekBar.setMax(100);
        Log.d(TAG_LOG, "PlayerFragment: call updateProgressBar()");
        updateProgressBar();
        updateListViewSelection();
    }

    /**
     * Prepare music player
     */
    void prepareMediaPlayer() {
        Log.d(TAG_LOG, "prepareMediaPlayer");
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(path);
        } catch (IOException e) {
            Log.d(TAG_LOG, e.toString());
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            Log.d(TAG_LOG, "Call prepareAsync()");
            mediaPlayer.prepareAsync();
            progressDialog = new ProgressDialog(getActivity());
            songCurrentDurationLabel.setText("");
            songTotalDurationLabel.setText("");
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        } catch (Exception e) {
            Log.d(TAG_LOG, e.toString());
        }
    }

    /**
     * Set new file
     * @param path path to new file
     */
    public void setNewFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (!path.equals(this.path)) {
                // set new file
                handler.removeCallbacks(run);
                handler.removeCallbacks(highlight);
                btnPlay.setBackgroundResource(R.drawable.play_icon);
                this.path = path;
                bookmarkArrayList.clear();
                bookmarkListAdapter.notifyDataSetChanged();
                getLoaderManager().getLoader(LOAD_BOOKMARKS_ID).forceLoad();
                prepareMediaPlayer();
                changeButtonsState(false);
            }
        } else {
            // notify about missing file
            Toast.makeText(getActivity(), "File doesn't exist", Toast.LENGTH_SHORT).show();
            mediaPlayer.reset();
            bookmarkArrayList.clear();
            bookmarkListAdapter.notifyDataSetChanged();
            fileIsValid = false;
            changeButtonsState(false);
            handler.removeCallbacks(run);
            handler.removeCallbacks(highlight);
            songTotalDurationLabel.setText("");
            songCurrentDurationLabel.setText("");
        }
    }

    /**
     * Restore playing state
     */
    void restorePlaying() {
        changeButtonsState(true);
        updateProgressBar();
        updateListViewSelection();
        btnPlay.setBackgroundResource(R.drawable.pause_icon);
    }

    /**
     * Restore pause state
     */
    void restorePaused() {
        changeButtonsState(true);
        updateProgressBar();
        updateListViewSelection();
        btnPlay.setBackgroundResource(R.drawable.play_icon);
    }

    /**
     * Select list item
     * @param i item position
     */
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
                        if (tempView != null) {
                            RelativeLayout tempLayout = (RelativeLayout) tempView.findViewById(R.id.list_view_relative_layout);
                            tempLayout.setBackgroundResource(R.color.frame_transparent);
                        }
                    }
                }
            }
        } else {
            Log.d(TAG_LOG, "View by pos in null");
        }
    }

    /**
     * Deselect positions
     */
    void deselectPos() {
        for (int index = 0; index < listView.getCount(); index++) {
            View tempView;
            tempView = listView.getChildAt(index);
            RelativeLayout tempLayout = (RelativeLayout) tempView.findViewById(R.id.list_view_relative_layout);
            tempLayout.setBackgroundResource(R.color.frame_transparent);
        }
    }

    /**
     * Stop threads on fragment destroy
     */
    @Override
    public void onDetach() {
        super.onDetach();
        handler.removeCallbacks(run);
        handler.removeCallbacks(highlight);
    }

    /**
     * Process item selection
     * @param item selected item
     * @return action
     */
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

    /**
     * Delete selected item
     * @param position selected bookmark
     */
    void deleteBookmark(int position) {
        Bookmark temp = bookmarkArrayList.get(position);
        File bookmarkToRemove = new File(temp.getBookmarkPath());
        bookmarkToRemove.delete();
        bookmarkArrayList.remove(position);
        bookmarkListAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "Bookmark deleted", Toast.LENGTH_SHORT).show();
        if (bookmarkArrayList.size() > 0) {
            stamps = new int[bookmarkArrayList.size()];
            for (int i = 0; i < bookmarkArrayList.size(); i++) {
                stamps[i] = bookmarkArrayList.get(i).getTime();
            }
        } else {
            handler.removeCallbacks(highlight);
            Toast.makeText(getActivity(), "No bookmarks left", Toast.LENGTH_SHORT).show();
        }

    }

}
