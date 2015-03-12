package com.example.mazzers.voicerecorder.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.mazzers.voicerecorder.MainActivity;
import com.example.mazzers.voicerecorder.bookmarks.Bookmark;
import com.example.mazzers.voicerecorder.bookmarks.adapters.ListViewAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Vashchenko Vitaliy A11B0529P
 * PRJ5 - Voice bookmarks
 * Simple bookmark list fragment (currently not used in project)
 */
public class BookmarkFragment extends ListFragment {
    private static Bookmark[] bookmarksList;
    private List<Bookmark> mItems;
    private String TAG_LOG = "myLogs";

    public BookmarkFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_LOG, "BookmarkFragment: onCreate");
        super.onCreate(savedInstanceState);

        mItems = new ArrayList<Bookmark>();

        fillStrings();

    }

    public void fillStrings() {

        if (bookmarksList == null) {
            Log.d(TAG_LOG, "BookmarkFragment: bookmarkList is empty");
            bookmarksList = MainActivity.getBookmarks();
        }
        for (int i = 0; i < bookmarksList.length; i++) {

            mItems.add(bookmarksList[i]);

        }


        setListAdapter(new ListViewAdapter(getActivity(), mItems));


    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // remove the dividers from the ListView of the ListFragment
        getListView().setDivider(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        Bookmark item = mItems.get(position);
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = new PlayerFragment();
        Log.d(TAG_LOG, "BookmarkFragment: Start play on: " + item.getTime());

        Bundle bundle = new Bundle();
        bundle.putString("filePath", item.getPath());
        bundle.putInt("fileTime", item.getTime());
        bundle.putBoolean("fromDrawer", false);
        //fragment.setArguments(bundle);
        //fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();


    }
}

