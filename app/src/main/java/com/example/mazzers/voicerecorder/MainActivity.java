package com.example.mazzers.voicerecorder;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.os.Environment;
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
public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<HashMap<String, List<Bookmark>>> {
    private Drawer.Result result = null;
    private static PlayerFragment playerFragment;
    private static RecorderFragment recorderFragment;
    private static SettingsFragment settingsFragment;
    private static RecordListFragment recordListFragment;
    private final String TAG_LOG = "MainActivity";
    private static ArrayList<Bookmark> bookmarks;
    private static File[] files;
    private File recordsDirectory;
    private static List<String> listDataHeader;
    private static HashMap<String, List<Bookmark>> mItems;
    private final int LOADER_BOOKMARKS_ID = 3;
    private final String bookmarksFolder = Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/";

    public static HashMap<String, List<Bookmark>> getmItems() {
        return mItems;
    }

    public static List<String> getListDataHeader() {
        return listDataHeader;
    }

    public static void setmItems(HashMap<String, List<Bookmark>> mItems) {
        MainActivity.mItems = mItems;
    }

    public static void setListDataHeader(List<String> listDataHeader) {
        MainActivity.listDataHeader = listDataHeader;
    }

    public File getRecordsDirectory() {
        return recordsDirectory;
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
        setContentView(R.layout.activity_main);
        recordsDirectory = new File(bookmarksFolder);
        if (!recordsDirectory.exists()) {
            Log.d(TAG_LOG, "Main activity: directory not exist");
            recordsDirectory.mkdirs();
        }
        Bundle loaderBundle = new Bundle();
        loaderBundle.putString("dir_bookmarks", bookmarksFolder);
        getLoaderManager().initLoader(LOADER_BOOKMARKS_ID, loaderBundle, this).forceLoad();

        playerFragment = (PlayerFragment) getFragmentManager().findFragmentByTag(PlayerFragment.PLAYER_TAG);
        if (playerFragment == null) {
            playerFragment = PlayerFragment.createNewInstance();
        }
        recorderFragment = (RecorderFragment) getFragmentManager().findFragmentByTag(RecorderFragment.RECORDER_TAG);
        if (recorderFragment == null) {
            recorderFragment = RecorderFragment.createNewInstance();
        }
        settingsFragment = (SettingsFragment) getFragmentManager().findFragmentByTag(SettingsFragment.SETTINGS_TAG);
        if (settingsFragment == null) {
            settingsFragment = new SettingsFragment();
        }


        result = new Drawer()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)

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
                            //Toast.makeText(MainActivity.this, MainActivity.this.getString(((Nameable) iDrawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                            Fragment fragment;
                            switch (iDrawerItem.getIdentifier()) {
                                case 1:
                                    displayRecorder();

                                    break;
                                case 2:
                                    displayPlayer();
                                    break;
                                case 3:
                                    displaySettings();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }).build();

        //Log.d(TAG_LOG, "Main activity: onCreate");
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


    void displayPlayer() {
        Toast.makeText(this, "Player", Toast.LENGTH_SHORT).show();
        PlayerFragment tempPlayerFragment = (PlayerFragment) getFragmentManager().findFragmentByTag(PlayerFragment.PLAYER_TAG);
        if (tempPlayerFragment == null) {
            playerFragment = PlayerFragment.createNewInstance();
        } else {
            playerFragment = tempPlayerFragment;
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (!playerFragment.isVisible()) {
            if (recorderFragment != null && recorderFragment.isVisible()) {
                ft.hide(recorderFragment);
            }
            if (settingsFragment != null && settingsFragment.isVisible()) {
                ft.hide(settingsFragment);
            }
            if (recordListFragment != null && recordListFragment.isVisible()) {
                ft.hide(recordListFragment);
            }
            if (!playerFragment.isAdded()) {
                ft.add(R.id.container, playerFragment, PlayerFragment.PLAYER_TAG);
            }
            ft.show(playerFragment);
            ft.addToBackStack(null);
            ft.commit();
        }


    }

    void displaySettings() {
        //Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
        SettingsFragment tempSettingsFragment = (SettingsFragment) getFragmentManager().findFragmentByTag(SettingsFragment.SETTINGS_TAG);
        if (tempSettingsFragment == null) {
            settingsFragment = new SettingsFragment();
        } else {
            settingsFragment = tempSettingsFragment;
        }
        if (!settingsFragment.isVisible()) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();

            if (playerFragment != null && playerFragment.isVisible()) {
                ft.hide(playerFragment);
            }
            if (recorderFragment != null && recorderFragment.isVisible()) {
                ft.hide(recorderFragment);
            }
            if (recordListFragment != null && recordListFragment.isVisible()) {
                ft.hide(recordListFragment);
            }
            if (!settingsFragment.isAdded()) {
                ft.add(R.id.container, settingsFragment, SettingsFragment.SETTINGS_TAG);
            }
            ft.show(settingsFragment);
            ft.addToBackStack(null);
            ft.commit();
        }

    }

    public void toggleList() {
        RecordListFragment tempListFragment = (RecordListFragment) getFragmentManager().findFragmentByTag(RecordListFragment.LIST_TAG);
        if (tempListFragment == null) {
            recordListFragment = RecordListFragment.createNewInstance();
        } else {
            recordListFragment = tempListFragment;
        }
        if (!recordListFragment.isVisible()) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();

            if (playerFragment != null && playerFragment.isVisible()) {
                ft.hide(playerFragment);
            }
            if (settingsFragment != null && settingsFragment.isVisible()) {
                ft.hide(settingsFragment);
            }
            if (recorderFragment != null && recorderFragment.isVisible()) {
                ft.hide(recorderFragment);
            }
            if (!recordListFragment.isAdded()) {
                ft.add(R.id.container, recordListFragment, RecordListFragment.LIST_TAG);
            }
            ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);
            ft.show(recordListFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    public void togglePlayer() {
        PlayerFragment tempPlayerFragment = (PlayerFragment) getFragmentManager().findFragmentByTag(PlayerFragment.PLAYER_TAG);
        if (tempPlayerFragment == null) {
            playerFragment = PlayerFragment.createNewInstance();
        } else {
            playerFragment = tempPlayerFragment;
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (!playerFragment.isVisible()) {
            if (recorderFragment != null && recorderFragment.isVisible()) {
                ft.hide(recorderFragment);
            }
            if (settingsFragment != null && settingsFragment.isVisible()) {
                ft.hide(settingsFragment);
            }
            if (recordListFragment != null && recordListFragment.isVisible()) {
                ft.hide(recordListFragment);
            }
            if (!playerFragment.isAdded()) {
                ft.add(R.id.container, playerFragment, PlayerFragment.PLAYER_TAG);
            }
            ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);
            ft.show(playerFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }


    void displayRecorder() {
        //Toast.makeText(this, "Recorder", Toast.LENGTH_SHORT).show();
        RecorderFragment tempRecorderFragment = (RecorderFragment) getFragmentManager().findFragmentByTag(RecorderFragment.RECORDER_TAG);
        if (tempRecorderFragment == null) {
            recorderFragment = RecorderFragment.createNewInstance();
        } else {
            recorderFragment = tempRecorderFragment;
        }
        if (!recorderFragment.isVisible()) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();

            if (playerFragment != null && playerFragment.isVisible()) {
                ft.hide(playerFragment);
            }
            if (settingsFragment != null && settingsFragment.isVisible()) {
                ft.hide(settingsFragment);
            }
            if (recordListFragment != null && recordListFragment.isVisible()) {
                ft.hide(recordListFragment);
            }
            if (!recorderFragment.isAdded()) {
                ft.add(R.id.container, recorderFragment, RecorderFragment.RECORDER_TAG);
            }
            ft.show(recorderFragment);
            ft.addToBackStack(null);
            ft.commit();
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
}

