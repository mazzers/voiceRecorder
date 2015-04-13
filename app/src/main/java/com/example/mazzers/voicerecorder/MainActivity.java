package com.example.mazzers.voicerecorder;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.mazzers.voicerecorder.bookmarks.Bookmark;
import com.example.mazzers.voicerecorder.bookmarks.ParseBookmarkFiles;
import com.example.mazzers.voicerecorder.bookmarks.ScanFiles;
import com.example.mazzers.voicerecorder.fragments.ExpandableBookmarks;
import com.example.mazzers.voicerecorder.fragments.PlayerFragment;
import com.example.mazzers.voicerecorder.fragments.RecorderFragment;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.io.File;

/**
 * Vashchenko Vitaliy A11B0529P
 * PRJ5 - Voice bookmarks
 * <p/>
 * Main activity with drawer and fragments
 */
public class MainActivity extends ActionBarActivity {
    private Drawer.Result result = null;
    private static PlayerFragment playerFragment;
    private ExpandableBookmarks expandableBookmarks;
    private RecorderFragment recorderFragment;
    private final String TAG_LOG = "MainActivity";
    private static Bookmark[] bookmarks;
    private static File[] files;
    private File recordsDirectory;

    public File getRecordsDirectory() {
        return recordsDirectory;
    }


    public static File[] getFiles() {
        return files;
    }

    public static void setFiles(File[] files) {
        MainActivity.files = files;
    }

    public static Bookmark[] getBookmarks() {
        return bookmarks;
    }

    public static void setBookmarks(Bookmark[] bookmarks) {

        MainActivity.bookmarks = bookmarks;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //TODO add widget/notification
        //increase icons
        //button push xml

        super.onCreate(savedInstanceState);
        Log.d(TAG_LOG, "onCreate");
        setContentView(R.layout.activity_main);
        recordsDirectory = new File(Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/");
        if (!recordsDirectory.exists()) {
            Log.d(TAG_LOG, "Main activity: directory not exist");
            recordsDirectory.mkdirs();

        }

        Thread parseBookmarkFiles = new Thread(new ParseBookmarkFiles());
        Thread scanFiles = new Thread(new ScanFiles(recordsDirectory));
        playerFragment = PlayerFragment.createNewInstance();
        recorderFragment = RecorderFragment.createNewInstance();
        expandableBookmarks = ExpandableBookmarks.createNewInstance();


        parseBookmarkFiles.start();
        scanFiles.start();
        result = new Drawer()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)

                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.title_section1).withIcon(FontAwesome.Icon.faw_microphone).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.title_section2).withIcon(FontAwesome.Icon.faw_play).withIdentifier(2),
                        //new PrimaryDrawerItem().withName(R.string.title_section3).withIcon(FontAwesome.Icon.faw_star).withIdentifier(3),
                        new SectionDrawerItem().withName(R.string.action_settings),
                        new SecondaryDrawerItem().withName(R.string.action_settings).withIcon(FontAwesome.Icon.faw_gear).withIdentifier(3)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                        if (iDrawerItem instanceof Nameable) {
                            //Toast.makeText(MainActivity.this, MainActivity.this.getString(((Nameable) iDrawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                            switch (iDrawerItem.getIdentifier()) {
                                case 1:
                                    displayRecorder();
                                    break;
                                case 2:
                                    displayPlayer();
                                    break;
                                case 3:
                                    Intent intent = new Intent(getApplication(), SettingsActivity.class);
                                    startActivity(intent);
                                    break;
//                                    displayBookmarks();
//                                    break;
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
        if (!playerFragment.isVisible()) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();

//        if (playerFragment.isAdded()) {
//            ft.show(playerFragment);
//        } else {
//            ft.add(R.id.container, playerFragment, PLAYER_TAG);
//            ft.show(playerFragment);
//        }
//
//        if (recorderFragment.isAdded()) {
//            ft.hide(recorderFragment);
//        }
//        if (expandableBookmarks.isAdded()) {
//            ft.hide(expandableBookmarks);
//        }
            playerFragment = (PlayerFragment) getFragmentManager().findFragmentByTag(PlayerFragment.PLAYER_TAG);
            if (playerFragment == null) {
                playerFragment = PlayerFragment.createNewInstance();
            }
            if (recorderFragment.isVisible()) {
                ft.hide(recorderFragment);
            }
            if (!playerFragment.isVisible()) {
                ft.show(playerFragment);
            }
            ft.replace(R.id.container, playerFragment, PlayerFragment.PLAYER_TAG);
//        ft.addToBackStack(null);
            ft.commit();
        }

    }

    public void displayPlayer(Bundle args) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        //if (playerFragment.isAdded()) {
        //ft.detach(playerFragment);
        playerFragment = PlayerFragment.createNewInstance(args);
        //ft.add(R.id.container, playerFragment, PLAYER_TAG);
        //} else {
        //   playerFragment = PlayerFragment.createNewInstance(PLAYER_TAG, args);
        //    ft.add(R.id.container, playerFragment, PLAYER_TAG);
        //   ft.show(playerFragment);
        // }


//        if (recorderFragment.isAdded()) {
//            ft.hide(recorderFragment);
//        }
//        if (expandableBookmarks.isAdded()) {
//            ft.hide(expandableBookmarks);
//        }

        ft.replace(R.id.container, playerFragment, PlayerFragment.PLAYER_TAG);
//        ft.addToBackStack(null);
        ft.commit();


    }


    void displayRecorder() {
        if (!recorderFragment.isVisible()) {
            Toast.makeText(this, "Recorder", Toast.LENGTH_SHORT).show();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
//        if(recorderFragment==null){
//            recorderFragment = RecorderFragment.createNewInstance();
//        }
//        if (recorderFragment.isAdded()) {
//            Log.d(TAG_LOG, "is added");
//            ft.show(recorderFragment);
//        } else {
//            Log.d(TAG_LOG, "not added");
//            ft.add(R.id.container, recorderFragment, RECORDER_TAG);
//            ft.show(recorderFragment);
//        }
//        if (playerFragment.isAdded()) {
//            ft.hide(playerFragment);
//        }
//        if (expandableBookmarks.isAdded()) {
//            ft.hide(expandableBookmarks);
//        }
            if (playerFragment.isVisible()) {
                ft.hide(playerFragment);
            }
            if (!recorderFragment.isVisible()) {
                ft.show(recorderFragment);
            }
            ft.replace(R.id.container, recorderFragment, RecorderFragment.RECORDER_TAG);
            ft.commit();
        }

    }


    void displayBookmarks() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        if (expandableBookmarks==null){
//            expandableBookmarks = ExpandableBookmarks.createNewInstance();
//        }
//        if (expandableBookmarks.isAdded()) {
//            ft.show(expandableBookmarks);
//            Log.d(TAG_LOG, "bookmarks are already added");
//        } else {
//            ft.add(R.id.container, expandableBookmarks, BOOKMARKS_TAG);
//            ft.show(expandableBookmarks);
//        }
//
//        if (playerFragment.isAdded()) {
//            ft.hide(playerFragment);
//        }
//        if (recorderFragment.isAdded()) {
//            ft.hide(recorderFragment);
//        }
        ft.replace(R.id.container, expandableBookmarks);
        ft.commit();
    }

    public static void setPlayerFragment(PlayerFragment Playerfragment) {

        playerFragment = Playerfragment;
    }

    public static PlayerFragment getPlayerFragment() {
        return playerFragment;
    }

}

