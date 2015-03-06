package com.example.mazzers.voicerecorder;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.mazzers.voicerecorder.bookmarks.ParseBookmarkFiles;
import com.example.mazzers.voicerecorder.fragments.ExpandableBookmarks;
import com.example.mazzers.voicerecorder.fragments.PlayerFragment;
import com.example.mazzers.voicerecorder.fragments.RecorderFragment;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
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
    private Bundle bundle;
    private final String RECORDER_TAG = "recorder_dw";
    private final String PLAYER_TAG = "player_dw";
    private final String BOOKMARKS_TAG = "bookmarks_dw";
    private PlayerFragment playerFragment;
    private ExpandableBookmarks expandableBookmarks;
    private RecorderFragment recorderFragment;
    final String TAG_LOG = "MainActivity";


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        playerFragment = (PlayerFragment)getFragmentManager().findFragmentByTag(PLAYER_TAG);
        recorderFragment = (RecorderFragment) getFragmentManager().findFragmentByTag(RECORDER_TAG);
        expandableBookmarks = (ExpandableBookmarks) getFragmentManager().findFragmentByTag(BOOKMARKS_TAG);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //todo debug orientation change
        //TODO add widget/notification
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        File recordsDirectory = new File(Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/");
        if (!recordsDirectory.exists()) {
            Log.d(TAG_LOG, "Main activity: directory not exist");
            recordsDirectory.mkdirs();
            if (!recordsDirectory.mkdirs()) {
            }
            Log.d(TAG_LOG, "Main activity: directories created");
        }
        bundle = new Bundle();
        Thread parseBookmarkFiles = new Thread(new ParseBookmarkFiles());

        if (savedInstanceState == null) {
            playerFragment = PlayerFragment.createNewInstance();
            expandableBookmarks = ExpandableBookmarks.createNewInstance();
            recorderFragment = RecorderFragment.createNewInstance();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, recorderFragment, RECORDER_TAG).commit();
        }else {
            Log.d(TAG_LOG,"saved is not null");
            playerFragment = (PlayerFragment) getFragmentManager().findFragmentByTag(PLAYER_TAG);
            expandableBookmarks = (ExpandableBookmarks) getFragmentManager().findFragmentByTag(BOOKMARKS_TAG);
            recorderFragment = (RecorderFragment) getFragmentManager().findFragmentByTag(RECORDER_TAG);
            if(recorderFragment==null){
                Log.d(TAG_LOG,"old not found");
            }

        }
        //todo asynctasts and handlers
        parseBookmarkFiles.start();
        new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.title_section1).withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.title_section2).withIcon(FontAwesome.Icon.faw_camera).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.title_section3).withIcon(FontAwesome.Icon.faw_bomb).withIdentifier(3),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.action_settings).withIcon(FontAwesome.Icon.faw_gear).withIdentifier(4)
                ).withOnDrawerListener(new Drawer.OnDrawerListener() {
            @Override
            public void onDrawerOpened(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);

            }

            @Override
            public void onDrawerClosed(View view) {

            }
        }).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                Toast.makeText(MainActivity.this, MainActivity.this.getString(((Nameable) iDrawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                Fragment fragment;
                FragmentManager fragmentManager = getFragmentManager();

                switch (iDrawerItem.getIdentifier()) {
                    case 1:
//                        fragment = fragmentManager.findFragmentByTag(RECORDER_TAG);
//                        if (fragment == null) {
//                            Log.d(TAG_LOG, "Fragment not found, create new");
//                            fragment = new RecorderFragment();
//                            fragmentManager.beginTransaction()
//                                    .replace(R.id.container, fragment, RECORDER_TAG).commit();
//                        }
                        displayRecorder();

                        //mTitle = getString(R.string.title_section1);
                        break;
                    case 2:
//                        fragment = fragmentManager.findFragmentByTag(PLAYER_TAG);
//                        if (fragment == null) {
//                            fragment = new PlayerFragment();
//                        }
//                        bundle.putBoolean("fromDrawer", true);
//                        fragment.setArguments(bundle);
//                        fragmentManager.beginTransaction().replace(R.id.container, fragment, PLAYER_TAG).commit();
                        displayPlayer();


                        break;
                    case 3:
//                        fragment = fragmentManager.findFragmentByTag(BOOKMARKS_TAG);
//                        if (fragment == null) {
//                            fragment = new ExpandableBookmarks();
//                        }
//                        fragmentManager.beginTransaction().replace(R.id.container, fragment, BOOKMARKS_TAG).commit();
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
        })
                .build();



        Log.d(TAG_LOG, "Main activity: onCreate");

    }

    public void displayPlayer(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(playerFragment.isAdded()){
           ft.show(playerFragment);
        }else{
          ft.add(R.id.container, playerFragment, PLAYER_TAG);
        }

        if(recorderFragment.isAdded()){
            ft.hide(recorderFragment);
        }
        if(expandableBookmarks.isAdded()){
            ft.hide(expandableBookmarks);
        }

       ft.commit();

        //todo add all fragments


    }

    public void displayPlayer(Bundle args){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(playerFragment.isAdded()){
            playerFragment = PlayerFragment.createNewInstance(PLAYER_TAG,args);
            ft.replace(R.id.container, playerFragment, PLAYER_TAG);
        }else {
            playerFragment = PlayerFragment.createNewInstance(PLAYER_TAG,args);
            ft.add(R.id.container,playerFragment,PLAYER_TAG);
        }


        if(recorderFragment.isAdded()){
            ft.hide(recorderFragment);
        }
        if(expandableBookmarks.isAdded()){
            ft.hide(expandableBookmarks);
        }

        ft.commit();

        //todo add all fragments


    }

    protected void displayRecorder(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(recorderFragment.isAdded()){

            ft.show(recorderFragment);
        }else {
            ft.add(R.id.container,recorderFragment,"recorder_fragment");
        }
        if(playerFragment.isAdded()){
            ft.hide(playerFragment);
        }
        if (expandableBookmarks.isAdded()){
            ft.hide(expandableBookmarks);
        }
        ft.commit();


    }


    protected void displayBookmarks(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(expandableBookmarks.isAdded()){
            ft.show(expandableBookmarks);
        }else {
            ft.add(R.id.container,expandableBookmarks,"bookmark_fragment");
        }

        if (playerFragment.isAdded()){
            ft.hide(playerFragment);
        }
        if(recorderFragment.isAdded()){
            ft.hide(recorderFragment);
        }
        ft.commit();
    }


}
