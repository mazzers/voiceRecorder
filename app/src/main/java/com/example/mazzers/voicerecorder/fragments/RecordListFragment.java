package com.example.mazzers.voicerecorder.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ListView;

import com.example.mazzers.voicerecorder.MainActivity;
import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.bookmarks.Bookmark;
import com.example.mazzers.voicerecorder.bookmarks.adapters.RecordListAdapter;
import com.example.mazzers.voicerecorder.utils.FilesLoader;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mazzers on 24. 3. 2015.
 */
public class RecordListFragment extends Fragment implements LoaderManager.LoaderCallbacks<File[]> {
    public static final String LIST_TAG = "RECORD_LIST";
    private final int LIST_FILE_LOADER_ID = 2;
    private RecordListAdapter recordListAdapter;
    private ListView listView;
    private final int FILE_DELETE = 1;
    private List<File> mFileList, prevFileList;
    private static final String TAG_LOG = "RecorderListFragment";
    private final File dir = new File(Environment.getExternalStorageDirectory() + "/voicerecorder/");


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        switch (item.getItemId()) {
            case FILE_DELETE:
                deleteFile(index);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        bundle.putString("dir", Environment.getExternalStorageDirectory() + "/voicerecorder/");
        getLoaderManager().initLoader(LIST_FILE_LOADER_ID, bundle, this).forceLoad();
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        //prepareData();

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
                //toggleList();
                toggleList();
        }
        return super.onOptionsItemSelected(item);
    }


    void toggleList() {
        MainActivity activity = (MainActivity) getActivity();
        activity.togglePlayer();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG_LOG, "onCreateView");
        View rootView = inflater.inflate(R.layout.record_list_layout, container, false);
        listView = (ListView) rootView.findViewById(R.id.recordsListView);
        if (prevFileList != null) {
            mFileList = prevFileList;
        } else {
            mFileList = new LinkedList<>();
        }
        recordListAdapter = new RecordListAdapter(rootView.getContext(), mFileList);
        listView.setAdapter(recordListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File child = mFileList.get(position);
                changeFile(child);

            }
        });
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, FILE_DELETE, 0, "Delete record");

            }
        });

        return rootView;
    }

    void changeFile(File file) {
        PlayerFragment playerFragment = MainActivity.getPlayerFragment();
        playerFragment.setNewFile(file);
        toggleList();
    }


    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().getLoader(LIST_FILE_LOADER_ID).forceLoad();
    }

    void updateList(File[] data) {
        mFileList.clear();
        mFileList.addAll(Arrays.asList(data));
        recordListAdapter.notifyDataSetChanged();
        listView.invalidateViews();
    }

    public static RecordListFragment createNewInstance() {
        return new RecordListFragment();
    }

    private void deleteFile(int position) {
        File recordToDelete = mFileList.get(position);
        recordToDelete.delete();
        mFileList.remove(position);
        String name = recordToDelete.getName().substring(0, recordToDelete.getName().length() - 4);
        HashMap<String, List<Bookmark>> mItems = MainActivity.getmItems();
        if (mItems != null) {


            if (mItems.containsKey(name)) {
                List<Bookmark> listToRemove = mItems.get(name);
                for (int j = 0; j < listToRemove.size(); j++) {
                    File bookmarkToDelete = new File(listToRemove.get(j).getBookmarkPath());
                    bookmarkToDelete.delete();
                }
                mItems.remove(name);
            }
            getLoaderManager().getLoader(LIST_FILE_LOADER_ID).forceLoad();

        } else {
            Log.i(TAG_LOG, "mItems = null");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        prevFileList = mFileList;
    }


    @Override
    public Loader<File[]> onCreateLoader(int id, Bundle args) {
        return new FilesLoader(getActivity(), args);
    }

    @Override
    public void onLoadFinished(Loader<File[]> loader, File[] data) {
        Log.i(TAG_LOG, "onLoadFinished");
        updateList(data);
    }

    @Override
    public void onLoaderReset(Loader<File[]> loader) {

    }
}
