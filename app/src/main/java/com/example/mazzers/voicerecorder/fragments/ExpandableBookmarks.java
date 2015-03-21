package com.example.mazzers.voicerecorder.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.mazzers.voicerecorder.MainActivity;
import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.bookmarks.Bookmark;
import com.example.mazzers.voicerecorder.bookmarks.ParseBookmarkFiles;
import com.example.mazzers.voicerecorder.bookmarks.adapters.ExpandableListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Vashchenko Vitaliy A11B0529P
 * PRJ5 - Voice bookmarks
 * <p/>
 * Bookmark list fragment. Display expandListView and handles user actions
 */
public class ExpandableBookmarks extends Fragment {
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expandableListView;
    private List<String> listDataHeader;
    private View rootView;
    private HashMap<String, List<Bookmark>> mItems;
    private String TAG_LOG = "myLogs";
    private final int MENU_CHILD_DELETE = 1;
    private final int MENU_CHILD_INFO = 2;
    private final int MENU_GROUP_DELETE = 3;
    private final int MENU_GROUP_INFO = 4;
    private Bookmark selectedBookmark;
    private List<Bookmark> selectedList;
    //private ParseTask parseTask;


    /**
     * Context menu selected action
     *
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //Bookmark book = mItems.get(item.getGroupId());
        switch (item.getItemId()) {
            case MENU_CHILD_DELETE:
                deleteChild();
                break;
            case MENU_GROUP_DELETE:
                deleteGroup();
                break;

        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        prepareListData();
    }

    /**
     * Fragment view create
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.expandable_fragment_layout, container, false);
        expandableListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
        //bookmarksList = MainActivity.getBookmarks();
        //setRetainInstance(true);
        // preparing list data

        prepareListData();

        listAdapter = new ExpandableListAdapter(rootView.getContext(), listDataHeader, mItems);

        // setting list adapter
        expandableListView.setAdapter(listAdapter);
        // Listview Group click listener
//        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v,
//                                        int groupPosition, long id) {
//                // Toast.makeText(getApplicationContext(),
//                // "Group Clicked " + listDataHeader.get(groupPosition),
//                // Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });

//        // Listview Group expanded listener
//        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//
//            @Override
//            public void onGroupExpand(int groupPosition) {
////                Toast.makeText(rootView.getContext(),
////                        listDataHeader.get(groupPosition) + " Expanded",
////                        Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // Listview Group collasped listener
//        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//
//            @Override
//            public void onGroupCollapse(int groupPosition) {
////                Toast.makeText(rootView.getContext(),
////                        listDataHeader.get(groupPosition) + " Collapsed",
////                        Toast.LENGTH_SHORT).show();
//
//            }
//        });

        // Listview on child click listener
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                Toast.makeText(
                        rootView.getContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + (mItems.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition).getTime()), Toast.LENGTH_SHORT)
                        .show();
                Bookmark item = mItems.get(listDataHeader.get(groupPosition)).get(childPosition);

                Log.d(TAG_LOG, "BookmarkFragment: Start play on: " + item.getTime());

                Bundle bundle = new Bundle();
                bundle.putString("filePath", item.getPath());
                bundle.putInt("fileTime", item.getTime());
                bundle.putBoolean("fromDrawer",false);
                ArrayList<Bookmark> tempBookmarks;
                tempBookmarks = new ArrayList<>(mItems.get(listDataHeader.get(groupPosition)));
                bundle.putParcelableArrayList("bookmarks", tempBookmarks);
                //fragment.setArguments(bundle);

                //fragmentManager.beginTransaction().replace(R.id.container, fragment, "player_bk").commit();
                ((MainActivity)getActivity()).displayPlayer(bundle);
                return false;
            }
        });

        expandableListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
                int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
                int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
                int type = expandableListView.getPackedPositionType(info.packedPosition);
                switch (type) {
                    case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
                        menu.add(0, MENU_CHILD_DELETE, 0, "Delete bookmark");
                        menu.add(0, MENU_CHILD_INFO, 0, "Bookmark info");
                        selectedBookmark = (Bookmark) listAdapter.getChild(groupPos, childPos);
                        selectedList = mItems.get(listAdapter.getGroup(groupPos));
                        break;
                    case ExpandableListView.PACKED_POSITION_TYPE_GROUP:
                        menu.add(0, MENU_GROUP_DELETE, 0, "Delete record");
                        menu.add(0, MENU_GROUP_INFO, 0, "Record info");
                        selectedList = mItems.get(listAdapter.getGroup(groupPos));
                        break;
                }
            }
        });


        return rootView;
    }

    /**
     * Prepare data for view
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        mItems = new HashMap<>();

        List<Bookmark> tempArray = new ArrayList<>();
        Bookmark[] bookmarksList = MainActivity.getBookmarks();
        if (bookmarksList != null && bookmarksList.length > 0) {
            String groupName = bookmarksList[0].getName();
            int groupCount = 1;
            listDataHeader.add(groupName);

            for (int i = 0; i < bookmarksList.length; i++) {
                if (!bookmarksList[i].getName().equals(groupName)) {
                    //new group
                    //add array to previous group
                    mItems.put(listDataHeader.get(groupCount - 1), tempArray);
                    //increase group pointer
                    groupCount++;
                    //reinitialize array
                    tempArray = new ArrayList<>();
                    //set new group name
                    groupName = bookmarksList[i].getName();
                    //add groupname to array
                    listDataHeader.add(groupName);
                }

                tempArray.add(bookmarksList[i]);

            }
            mItems.put(listDataHeader.get(groupCount - 1), tempArray);

        } else {
            Log.d(TAG_LOG, "no bookmarks");
            Toast.makeText(getActivity(), "No bookmarks", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Delete selected child
     */
    private void deleteChild() {
        File bookmarkToDelete = new File(selectedBookmark.getBookmarkPath());
        if (selectedList.isEmpty()) {
            Log.d(TAG_LOG, "no list");
        }
        if (!selectedList.contains(selectedBookmark)) {
            Log.d(TAG_LOG, "no selected bookmark in list");
        } else {
            Log.d(TAG_LOG, "selected bookmark in list");
            selectedList.remove(selectedBookmark);

            bookmarkToDelete.delete();
        }
        Thread parseBookmarkFiles = new Thread(new ParseBookmarkFiles());
        parseBookmarkFiles.start();
        //listAdapter.notifyDataSetChanged();
        //parseTask = new ParseTask();
        //parseTask.execute();
        listAdapter.notifyDataSetChanged();

        //Toast.makeText(getActivity(), "Delete child", Toast.LENGTH_SHORT).show();

    }

    /**
     * delete selected record
     */
    private void deleteGroup() {
        File recordToDelete = new File(selectedList.get(0).getPath());
        listDataHeader.remove(selectedList.get(0).getName());
        for (int i = 0; i < selectedList.size(); i++) {
            File bookmarkToDelete = new File(selectedList.get(i).getBookmarkPath());
            bookmarkToDelete.delete();
        }
        selectedList.clear();

        recordToDelete.delete();
        Thread parseBookmarkFiles = new Thread(new ParseBookmarkFiles());
        parseBookmarkFiles.start();
        //parseTask = new ParseTask();
        //parseTask.execute();
        listAdapter.notifyDataSetChanged();
        //Toast.makeText(getActivity(), "Delete record", Toast.LENGTH_SHORT).show();
    }

   public static ExpandableBookmarks createNewInstance(){

//       Bundle args = new Bundle();
//       args.putString("fragment_tag",tag);
//       expandableBookmarks.setArguments(args);
       return new ExpandableBookmarks();

   }
}
