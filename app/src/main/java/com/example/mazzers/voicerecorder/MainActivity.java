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
    //private NavigationDrawerFragment mNavigationDrawerFragment;
    //private CharSequence mTitle;

    private Drawer.Result result = null;
    //private Bundle bundle;
    private final String RECORDER_TAG = "recorder_dw";
    private final String PLAYER_TAG = "player_dw";
    private final String BOOKMARKS_TAG = "bookmarks_dw";
    private PlayerFragment playerFragment;
    private ExpandableBookmarks expandableBookmarks;
    private RecorderFragment recorderFragment;
    final String TAG_LOG = "MainActivity";
    public static Bookmark[] bookmarks;
    //private ParseTask parseTask;

    public static Bookmark[] getBookmarks() {
        return bookmarks;
    }

    public static void setBookmarks(Bookmark[] bookmarks) {

        MainActivity.bookmarks = bookmarks;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //todo debug orientation change
        //TODO add widget/notification
        super.onCreate(savedInstanceState);
        Log.d(TAG_LOG, "onCreate");
        setContentView(R.layout.activity_main);
        File recordsDirectory = new File(Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/");
        if (!recordsDirectory.exists()) {
            Log.d(TAG_LOG, "Main activity: directory not exist");
            recordsDirectory.mkdirs();
            if (!recordsDirectory.mkdirs()) {
            }
            Log.d(TAG_LOG, "Main activity: directories created");
        }
        //parseTask = new ParseTask();
        //parseTask.execute();
//        FragmentManager fm = getFragmentManager();
//        recorderFragment = (RecorderFragment) fm.findFragmentByTag(RECORDER_TAG);
//        playerFragment = (PlayerFragment) fm.findFragmentByTag(PLAYER_TAG);
//        expandableBookmarks = (ExpandableBookmarks) fm.findFragmentByTag(BOOKMARKS_TAG);


        //bundle = new Bundle();

        Thread parseBookmarkFiles = new Thread(new ParseBookmarkFiles());


        if (savedInstanceState != null) {
            Log.d(TAG_LOG, "Orientation change");
//            recorderFragment = (RecorderFragment) getFragmentManager().findFragmentByTag(RECORDER_TAG);
//            playerFragment = (PlayerFragment) getFragmentManager().findFragmentByTag(PLAYER_TAG);
//            expandableBookmarks = (ExpandableBookmarks) getFragmentManager().findFragmentByTag(BOOKMARKS_TAG);
            if (recorderFragment == null) {
                recorderFragment = RecorderFragment.createNewInstance();
            }
            if (playerFragment == null) {
                playerFragment = PlayerFragment.createNewInstance();
            }
            if (expandableBookmarks == null) {
                expandableBookmarks = ExpandableBookmarks.createNewInstance();
            }

        } else {
            Log.d(TAG_LOG, "savedInstance is null");
            playerFragment = PlayerFragment.createNewInstance();
            expandableBookmarks = ExpandableBookmarks.createNewInstance();
            recorderFragment = RecorderFragment.createNewInstance();


            //FragmentTransaction ft = getFragmentManager().beginTransaction();
            //ft.add(R.id.container, recorderFragment, RECORDER_TAG);

            //ft.add(R.id.container,playerFragment,PLAYER_TAG);
            //ft.add(R.id.container,expandableBookmarks,BOOKMARKS_TAG);
//            ft.hide(playerFragment);
//            ft.hide(expandableBookmarks);
            //ft.commit();

        }

        //todo asynctasts and handlers
        parseBookmarkFiles.start();
        result = new Drawer()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(false)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.title_section1).withIcon(R.drawable.new_micro).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.title_section2).withIcon(R.drawable.new_play).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.title_section3).withIcon(R.drawable.new_star).withIdentifier(3),
                        new SectionDrawerItem().withName(R.string.action_settings),
                        new SecondaryDrawerItem().withName(R.string.action_settings).withIcon(FontAwesome.Icon.faw_gear).withIdentifier(4)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                        if (iDrawerItem instanceof Nameable) {
                            Toast.makeText(MainActivity.this, MainActivity.this.getString(((Nameable) iDrawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                            switch (iDrawerItem.getIdentifier()) {
                                case 1:
                                    displayRecorder();
                                    break;
                                case 2:
                                    displayPlayer();
                                    break;
                                case 3:
                                    displayBookmarks();
                                    break;
                                case 4:
                                    Intent intent = new Intent(getApplication(), SettingsActivity.class);
                                    startActivity(intent);
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

        int currr = savedInstanceState.getInt("selected", 1);
        result.setSelection(currr, false);
    }

    public void displayPlayer() {
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
        ft.replace(R.id.container, playerFragment, PLAYER_TAG);

        ft.commit();

        //todo add all fragments


    }

    public void displayPlayer(Bundle args) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        //if (playerFragment.isAdded()) {
        //ft.detach(playerFragment);
        playerFragment = PlayerFragment.createNewInstance(PLAYER_TAG, args);
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

        ft.replace(R.id.container, playerFragment, PLAYER_TAG);
        ft.commit();

        //todo add all fragments


    }


    protected void displayRecorder() {
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
        ft.replace(R.id.container, recorderFragment, RECORDER_TAG);
        ft.commit();


    }


    protected void displayBookmarks() {
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
        ft.replace(R.id.container, expandableBookmarks, BOOKMARKS_TAG);
        ft.commit();
    }


}
