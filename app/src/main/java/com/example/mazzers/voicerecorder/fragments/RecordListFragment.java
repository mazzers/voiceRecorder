package com.example.mazzers.voicerecorder.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ListView;

import com.example.mazzers.voicerecorder.MainActivity;
import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.bookmarks.adapters.RecordListAdapter;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mazzers on 24. 3. 2015.
 */
public class RecordListFragment extends Fragment {
    public static final String LIST_TAG = "RECORD_LIST";
    private RecordListAdapter recordListAdapter;
    private ListView listView;
    private List<File> mFileList;
    private static String TAG_LOG = "RecorderListFragment";
    private final File dir = new File(Environment.getExternalStorageDirectory() + "/voicerecorder/");
    private Handler handler = new Handler();
    private boolean doneScanning = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

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
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);
        ft.hide(this);
        PlayerFragment playerFragment = MainActivity.getPlayerFragment();
        if (!playerFragment.isVisible()) ft.show(playerFragment);
        ft.commit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.record_list_layout, container, false);
        listView = (ListView) rootView.findViewById(R.id.recordsListView);
        prepareData();
        recordListAdapter = new RecordListAdapter(rootView.getContext(), mFileList);
        listView.setAdapter(recordListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File child = mFileList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("filePath", child.getPath());
                toggleListWithNewFile(bundle);

            }
        });
        return rootView;
    }

    void prepareData() {
        //Thread scanFiles = new Thread(new ScanFiles(Environment.getExternalStorageDirectory() + "/voicerecorder/"));
        //scanFiles.start();
        mFileList = Arrays.asList(MainActivity.getFiles());
        if (mFileList.size() == 0) {
            Log.d(TAG_LOG, "no files");
        }
    }

    void toggleListWithNewFile(Bundle bundle) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);
        ft.remove(MainActivity.getPlayerFragment());
        PlayerFragment playerFragment = PlayerFragment.createNewInstance(bundle);
        MainActivity.setPlayerFragment(playerFragment);
        ft.hide(this);
        ft.add(R.id.container, playerFragment, PlayerFragment.PLAYER_TAG);
        if (!MainActivity.getPlayerFragment().isVisible()) {
            ft.show(MainActivity.getPlayerFragment());
        }
        //todo add back button
        ft.commit();
    }

    public static RecordListFragment createNewInstance() {
        return new RecordListFragment();
    }

}
