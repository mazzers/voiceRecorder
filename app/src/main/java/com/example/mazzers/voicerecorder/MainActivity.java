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
 * voiceRecorder application
 * @author Vitaliy Vashchenko A11B0529P
 * Main activity with application fragments
 */
public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<HashMap<String, List<Bookmark>>>, FragmentManager.OnBackStackChangedListener {
    private Drawer.Result result = null; // drawer creation result
    private final String TAG_LOG = "MainActivity"; // logger tag
    private static ArrayList<Bookmark> bookmarks; // parsed bookmarks
    private static File[] files; // parsed records
    private File recordsDirectory; // directory with record files
    private static HashMap<String, List<Bookmark>> mItems; // classified bookmarks
    private final int LOADER_BOOKMARKS_ID = 3; // loader id
    private final String bookmarksFolder = Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/"; // directory with bookmark files
    private boolean pressedOnce = false; // back key press count
    private Recorder recorder; // recorder base
    private Player player; // player base

    /**
     * Get recorder base
     *
     * @return recorder base
     */
    public Recorder getRecorder() {
        return recorder;
    }

    /**
     * Get classified bookmarks
     *
     * @return hashMap with bookmarks
     */
    public static HashMap<String, List<Bookmark>> getmItems() {
        return mItems;
    }

    /**
     * Set hashMap
     * @param mItems new hashMap
     */
    public static void setmItems(HashMap<String, List<Bookmark>> mItems) {
        MainActivity.mItems = mItems;
    }

    /**
     * Get player base
     * @return player base
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get array of records
     * @return array of records
     */
    public static File[] getFiles() {
        return files;
    }

    /**
     * Set array of records
     * @param files new array of records
     */
    public static void setFiles(File[] files) {
        MainActivity.files = files;
    }

    /**
     * Set bookmarks list
     * @param bookmarks new list of bookmarks
     */
    public static void setBookmarks(ArrayList<Bookmark> bookmarks) {
        MainActivity.bookmarks = bookmarks;
    }

    /**
     * Get bookmarks list
     * @return bookmarks list
     */
    public static ArrayList<Bookmark> getBookmarks() {

        return bookmarks;
    }

    /**
     * Activity creation
     * @param savedInstanceState previous activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //Log.d(TAG_LOG, "onCreate");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this); // set listener
        setContentView(R.layout.activity_main); // set view file
        recordsDirectory = new File(bookmarksFolder); //set records folder

        if (!recordsDirectory.exists()) {
            Log.d(TAG_LOG, "Main activity: directory not exist");
            recordsDirectory.mkdirs();  // create folder if doesn't exist
        }
        Bundle loaderBundle = new Bundle(); // bundle for loader
        loaderBundle.putString("dir_bookmarks", bookmarksFolder); // set folder for loader
        getLoaderManager().initLoader(LOADER_BOOKMARKS_ID, loaderBundle, this).forceLoad(); // create loader and start it afterwards
        recorder = new Recorder(); // initialize bases
        player = new Player();


        result = new Drawer() // create drawer view
                .withActivity(this)
                .withTranslucentStatusBar(false)
//                .withActionBarDrawerToggle(true)

                .addDrawerItems(  // add drawer items
                        new PrimaryDrawerItem().withName(R.string.title_section1).withIcon(FontAwesome.Icon.faw_microphone).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.title_section2).withIcon(FontAwesome.Icon.faw_play).withIdentifier(2),
                        new SectionDrawerItem().withName(R.string.action_settings),
                        new SecondaryDrawerItem().withName(R.string.action_settings).withIcon(FontAwesome.Icon.faw_gear).withIdentifier(3)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() { // listen for drawer clicks
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                        if (iDrawerItem instanceof Nameable) {
                            switch (iDrawerItem.getIdentifier()) {
                                case 1:
                                    toggleRecorder(); // show recorder fragment
                                    break;
                                case 2:
                                    togglePlayer(); // show player fragment
                                    break;
                                case 3:
                                    toggleSettings(); // show settings fragment
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

    /**
     * Save state before activity destroy
     * @param outState actual activity state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("selected", result.getCurrentSelection()); // save actual fragments
        super.onSaveInstanceState(outState);
    }

    /**
     * Restore state of destroyed activity
     * @param savedInstanceState previous activity state
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        int curr = savedInstanceState.getInt("selected", 1); // select previous fragment
        result.setSelection(curr, false);
    }

    /**
     * Show recorder fragment
     */
    public void toggleRecorder() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        RecorderFragment temp = new RecorderFragment(); // create new fragment
        ft.replace(R.id.container, temp, RecorderFragment.RECORDER_TAG); // replace fragment in container
        ft.commit(); // execute transaction
    }

    /**
     * Show player fragment
     */
    public void togglePlayer() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        PlayerFragment temp = new PlayerFragment();
        ft.replace(R.id.container, temp, PlayerFragment.PLAYER_TAG);
        ft.commit();
    }

    /**
     * Show record list fragment
     */
    public void toggleList() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        RecordListFragment temp = new RecordListFragment();
        ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
        ft.replace(R.id.container, temp, RecordListFragment.LIST_TAG);
        ft.commit();
    }

    /**
     * Show settings fragment
     */
    public void toggleSettings() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        SettingsFragment temp = new SettingsFragment();
        ft.replace(R.id.container, temp, SettingsFragment.SETTINGS_TAG);
        ft.commit();
    }

    /**
     * Process back key press
     */
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
            // close application on x2 back button press
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

    /**
     * Create loader
     * @param id loader id
     * @param args loader arguments
     * @return loader
     */
    @Override
    public Loader<HashMap<String, List<Bookmark>>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG_LOG, "onCreateLoader");
        return new BookmarksLoader(this, args);
    }

    /**
     * Get data from loader
     * @param loader finished loader
     * @param data data from loader
     */
    @Override
    public void onLoadFinished(Loader<HashMap<String, List<Bookmark>>> loader, HashMap<String, List<Bookmark>> data) {
        Log.d(TAG_LOG, "onLoadFinished");
        mItems = data;
    }

    /**
     * Reset loader
     * @param loader finished loader
     */
    @Override
    public void onLoaderReset(Loader<HashMap<String, List<Bookmark>>> loader) {

    }

    /**
     * Listen for backStack changes
     */
    @Override
    public void onBackStackChanged() {
        Log.i(TAG_LOG, "BackStack changed");
    }

    /**
     * Stop recoding on application stop
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG_LOG, "onDestroy Activity");
        if (recorder.isRecording()) {
            recorder.stopRecording();
        }
    }
}

