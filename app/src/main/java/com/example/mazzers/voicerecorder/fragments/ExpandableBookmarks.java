package com.example.mazzers.voicerecorder.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.bookmarks.Bookmark;
import com.example.mazzers.voicerecorder.bookmarks.ExpandedList.ExpandableListAdapter;
import com.example.mazzers.voicerecorder.bookmarks.ParseBookmarkFiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mazzers on 12. 12. 2014.
 */
public class ExpandableBookmarks extends Fragment {
    ExpandableListAdapter listAdapter;
    ExpandableListView expandableListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    View rootView;
    private static Bookmark[] bookmarksList;
    private HashMap<String, List<Bookmark>> mItems;
    private String TAG_LOG = "myLogs";

    public ExpandableBookmarks() {
        //todo dynamic listChange
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.expandable_fragment_layout, container, false);
        expandableListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(rootView.getContext(), listDataHeader, mItems);

        // setting list adapter
        expandableListView.setAdapter(listAdapter);
        // Listview Group click listener
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(rootView.getContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(rootView.getContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                //Bookmark child = (Bookmark) rootView.getChild(groupPosition, childPosition);

                Toast.makeText(
                        rootView.getContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + (mItems.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition).getTime()), Toast.LENGTH_SHORT)
                        .show();
                Bookmark item = mItems.get(listDataHeader.get(groupPosition)).get(childPosition);
                FragmentManager fragmentManager = getFragmentManager();
                Fragment fragment = new PlayerFragment();
                Log.d(TAG_LOG, "BookmarkFragment: Start play on: " + item.getTime());

                Bundle bundle = new Bundle();
                bundle.putString("filePath", item.getPath());
                bundle.putInt("fileTime", item.getTime());
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                return false;
            }
        });

        return rootView;
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        //listDataChild = new HashMap<String, List<String>>();
        //listDataChildTime = new HashMap<String,Bookmark>();

        mItems = new HashMap<String, List<Bookmark>>();

        List<Bookmark> tempArray = new ArrayList<Bookmark>();
        bookmarksList = ParseBookmarkFiles.getBookmarks();
        String groupName = bookmarksList[0].getName();
        int groupCount = 1;
        listDataHeader.add(groupName);

        for (int i = 0; i < bookmarksList.length; i++) {
            if (!bookmarksList[i].getName().equals(groupName)) {
                //new group
                //add array to previous group
                //listDataChild.put(listDataHeader.get(groupCount-1), tempArray);
                mItems.put(listDataHeader.get(groupCount - 1), tempArray);
                //listDataChildTime.put(listDataHeader.get(groupCount-1),tempArrayTime);
                //increase group pointer
                groupCount++;
                //reinitialize array
                tempArray = new ArrayList<Bookmark>();
                //tempArrayTime = new ArrayList<String>();
                //set new group name
                groupName = bookmarksList[i].getName();
                //add groupname to array
                listDataHeader.add(groupName);
            } else {


            }
            tempArray.add(bookmarksList[i]);
            //tempArrayTime.add(String.valueOf(bookmarksList[i].getTime()));

        }

    }


}
