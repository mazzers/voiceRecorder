package com.example.mazzers.voicerecorder.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mazzers.voicerecorder.MainActivity;
import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.bookmarks.adapters.RecordListAdapter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mazzers on 24. 3. 2015.
 */
public class RecordListFragment extends Fragment {
    private RecordListAdapter recordListAdapter;
    private ListView listView;
    private List<File> mFileList;
    private static String TAG_LOG = "RecorderListFragment";
    private final File dir = new File(Environment.getExternalStorageDirectory() + "/voicerecorder/");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

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
        File[] mFiles;
        mFiles = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".mp4");
            }
        });
        mFileList = Arrays.asList(mFiles);
        if (mFiles.length > 0) {
            Log.d(TAG_LOG, "some files: " + mFiles.length);

        }


    }

    void toggleListWithNewFile(Bundle bundle) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);
        PlayerFragment playerFragment = PlayerFragment.createNewInstance(bundle);
        MainActivity.setPlayerFragment(playerFragment);
        ft.hide(this);
        ft.replace(R.id.container, playerFragment, PlayerFragment.PLAYER_TAG);
        //todo add back button
        ft.commit();
    }

    public static RecordListFragment createNewInstance() {
        return new RecordListFragment();
    }

}
