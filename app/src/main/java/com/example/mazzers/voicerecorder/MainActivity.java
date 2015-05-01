package com.example.mazzers.voicerecorder;

import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.mazzers.voicerecorder.bookmarks.Bookmark;
import com.example.mazzers.voicerecorder.fragments.PlayerFragment;
import com.example.mazzers.voicerecorder.fragments.RecordListFragment;
import com.example.mazzers.voicerecorder.fragments.RecorderFragment;
import com.example.mazzers.voicerecorder.fragments.SettingsFragment;
import com.example.mazzers.voicerecorder.fragments.base.Player;
import com.example.mazzers.voicerecorder.fragments.base.Recorder;
import com.example.mazzers.voicerecorder.utils.BookmarksLoader;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Vashchenko Vitaliy A11B0529P
 * PRJ5 - Voice bookmarks
 * <p/>
 * Main activity with drawer and fragments
 */
public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<HashMap<String, List<Bookmark>>>, FragmentManager.OnBackStackChangedListener {
    private Drawer.Result result = null;
    private static PlayerFragment playerFragment;
    private static RecorderFragment recorderFragment;
    private static SettingsFragment settingsFragment;
    private static RecordListFragment recordListFragment;
    private final String TAG_LOG = "MainActivity";
    private static ArrayList<Bookmark> bookmarks;
    private static File[] files;
    private File recordsDirectory;
    private static HashMap<String, List<Bookmark>> mItems;
    private final int LOADER_BOOKMARKS_ID = 3;
    private final String bookmarksFolder = Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/";
    private boolean pressedOnce = false;
    private Recorder recorder;
    private Player player;

    public Recorder getRecorder() {
        return recorder;
    }

    public static HashMap<String, List<Bookmark>> getmItems() {
        return mItems;
    }

    public static void setmItems(HashMap<String, List<Bookmark>> mItems) {
        MainActivity.mItems = mItems;
    }


    public Player getPlayer() {
        return player;
    }

    public static File[] getFiles() {
        return files;
    }

    public static void setFiles(File[] files) {
        MainActivity.files = files;
    }

    public static void setBookmarks(ArrayList<Bookmark> bookmarks) {
        MainActivity.bookmarks = bookmarks;
    }

    public static ArrayList<Bookmark> getBookmarks() {

        return bookmarks;
    }

    public RecorderFragment getRecorderFragment() {
        return recorderFragment;
    }

    public void setRecorderFragment(RecorderFragment recorderFragment) {
        this.recorderFragment = recorderFragment;
    }

    public static void setRecordListFragment(RecordListFragment recordListFragment) {
        MainActivity.recordListFragment = recordListFragment;
    }

    public static RecordListFragment getRecordListFragment() {
        return recordListFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //Log.d(TAG_LOG, "onCreate");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        setContentView(R.layout.activity_main);
        recordsDirectory = new File(bookmarksFolder);

        if (!recordsDirectory.exists()) {
            Log.d(TAG_LOG, "Main activity: directory not exist");
            recordsDirectory.mkdirs();
        }
        Bundle loaderBundle = new Bundle();
        loaderBundle.putString("dir_bookmarks", bookmarksFolder);
        getLoaderManager().initLoader(LOADER_BOOKMARKS_ID, loaderBundle, this).forceLoad();
        recorder = new Recorder();
        player = new Player();


        result = new Drawer()
                .withActivity(this)
                .withTranslucentStatusBar(false)
//                .withActionBarDrawerToggle(true)

                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.title_section1).withIcon(FontAwesome.Icon.faw_microphone).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.title_section2).withIcon(FontAwesome.Icon.faw_play).withIdentifier(2),
                        new SectionDrawerItem().withName(R.string.action_settings),
                        new SecondaryDrawerItem().withName(R.string.action_settings).withIcon(FontAwesome.Icon.faw_gear).withIdentifier(3)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                        if (iDrawerItem instanceof Nameable) {
                            switch (iDrawerItem.getIdentifier()) {
                                case 1:
                                    toggleRecorder();
                                    break;
                                case 2:
                                    togglePlayer();
                                    break;
                                case 3:
                                    toggleSettings();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }).build();

        if (savedInstanceState == null) {
            result.setSelection(0);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("selected", result.getCurrentSelection());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        int curr = savedInstanceState.getInt("selected", 1);
        result.setSelection(curr, false);
    }


    public void toggleRecorder() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        RecorderFragment temp = new RecorderFragment();
        ft.replace(R.id.container, temp, RecorderFragment.RECORDER_TAG);
        ft.commit();
    }

    public void togglePlayer() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        PlayerFragment temp = new PlayerFragment();
        ft.replace(R.id.container, temp, PlayerFragment.PLAYER_TAG);
        ft.commit();
    }

    public void toggleList() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        RecordListFragment temp = new RecordListFragment();
        ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
        ft.replace(R.id.container, temp, RecordListFragment.LIST_TAG);
        ft.commit();
    }

    public void toggleSettings() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        SettingsFragment temp = new SettingsFragment();
        ft.replace(R.id.container, temp, SettingsFragment.SETTINGS_TAG);
        ft.commit();
    }


    @Override
    public void onBackPressed() {
        int i = getFragmentManager().getBackStackEntryCount();
        if (i > 1) {
            getFragmentManager().popBackStack();
        } else {
            if (pressedOnce) {
                super.onBackPressed();
                return;
            }

            this.pressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    pressedOnce = false;
                }
            }, 2000);
        }
    }

    public static void setPlayerFragment(PlayerFragment newPlayerFragment) {

        playerFragment = newPlayerFragment;
    }

    public static PlayerFragment getPlayerFragment() {
        return playerFragment;
    }

    @Override
    public Loader<HashMap<String, List<Bookmark>>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG_LOG, "onCreateLoader");
        return new BookmarksLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<HashMap<String, List<Bookmark>>> loader, HashMap<String, List<Bookmark>> data) {
        //Toast.makeText(this, "onLoadFinished", Toast.LENGTH_SHORT).show();
        Log.d(TAG_LOG, "onLoadFinished");
        mItems = data;
    }


    @Override
    public void onLoaderReset(Loader<HashMap<String, List<Bookmark>>> loader) {

    }

    @Override
    public void onBackStackChanged() {
        Log.i(TAG_LOG, "BackStack changed");
    }
}

