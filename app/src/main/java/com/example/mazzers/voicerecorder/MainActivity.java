package com.example.mazzers.voicerecorder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mazzers.voicerecorder.bookmarks.ParseBookmarkFiles;
import com.example.mazzers.voicerecorder.fragments.ExpandableBookmarks;
import com.example.mazzers.voicerecorder.fragments.PlayerFragment;
import com.example.mazzers.voicerecorder.fragments.RecorderFragment;

import java.io.File;


public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private Bundle bundle;


    final String TAG_LOG = "myLogs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //todo debug orientation change
        //TODO add widget/notification
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG_LOG, "Main activity: Start");
        File recordsDirectory = new File(Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/");
        if (!recordsDirectory.exists()){
            Log.d(TAG_LOG,"Main activity: directory not exist");
            recordsDirectory.mkdirs();
            if (!recordsDirectory.mkdirs()){
            }
            Log.d(TAG_LOG,"Main activity: directories created");
        }
        Thread parseBookmarkFiles = new Thread(new ParseBookmarkFiles());
        //todo asynctasts and handlers
        parseBookmarkFiles.start();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        bundle = new Bundle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        Log.d(TAG_LOG, "Main activity: onCreate");

    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Log.d(TAG_LOG,"Main activity: onNavigationDrawerItemSelected");
        Fragment fragment;

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        switch (position){
            case 0:
                fragment = new RecorderFragment();

                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment).commit();
                mTitle = getString(R.string.title_section1);
                break;
            case 1:
                fragment = new PlayerFragment();
                bundle.putBoolean("fromDrawer",true);
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment).commit();
                mTitle = getString(R.string.title_section2);
                break;
            case 2:
                //fragment = new BookmarkFragment();
                fragment = new ExpandableBookmarks();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment).commit();
                mTitle = getString(R.string.title_section3);
                break;


        }


    }



    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
