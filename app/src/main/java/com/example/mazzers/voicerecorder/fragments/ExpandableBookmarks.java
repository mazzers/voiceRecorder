package com.example.mazzers.voicerecorder.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.bookmarks.Bookmark;
import com.example.mazzers.voicerecorder.bookmarks.ParseBookmarkFiles;
import com.example.mazzers.voicerecorder.bookmarks.adapters.ExpandableListAdapter;

import java.io.File;
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
    View rootView;
    private static Bookmark[] bookmarksList;
    private HashMap<String, List<Bookmark>> mItems;
    private String TAG_LOG = "myLogs";
    private final int MENU_CHILD_DELETE = 1;
    private final int MENU_CHILD_INFO = 2;
    private final int MENU_GROUP_DELETE = 3;
    private final int MENU_GROUP_INFO = 4;
    private Bookmark selectedBookmark;
    private List<Bookmark> selectedList;

    public ExpandableBookmarks() {
        //todo dynamic listChange
    }


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
//                Toast.makeText(rootView.getContext(),
//                        listDataHeader.get(groupPosition) + " Expanded",
//                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(rootView.getContext(),
//                        listDataHeader.get(groupPosition) + " Collapsed",
//                        Toast.LENGTH_SHORT).show();

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
                ArrayList<Bookmark> tempBookmarks;
                tempBookmarks = new ArrayList<Bookmark>(mItems.get(listDataHeader.get(groupPosition)));
                bundle.putParcelableArrayList("bookmarks", tempBookmarks);
                fragment.setArguments(bundle);
                //getFragmentManager().beginTransaction().addToBackStack(null);
                //getFragmentManager().beginTransaction().hide(getTargetFragment());
                fragmentManager.beginTransaction().replace(R.id.container, fragment, "player_bk").commit();
                return false;
            }
        });

//        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                int itemType = expandableListView.getPackedPositionType(id);
//                int item;
//                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
//                    item = ExpandableListView.getPackedPositionChild(id);
//                    Toast.makeText(getActivity(), "Child long press", Toast.LENGTH_SHORT).show();
//                    //groupPosition = ExpandableListView.getPackedPositionGroup(id);
//                    return true; //true if we consumed the click, false if not
//
//                } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
//                    item = ExpandableListView.getPackedPositionGroup(id);
//                    Toast.makeText(getActivity(), "Group long press", Toast.LENGTH_SHORT).show();
//                    //do your per-group callback here
//                    return true; //true if we consumed the click, false if not
//
//                } else {
//                    // null item; we don't consume the click
//                    return false;
//                }
//            }
//        });

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
                        selectedBookmark = (Bookmark) listAdapter.getChild(groupPos,childPos);
                        selectedList =mItems.get(listAdapter.getGroup(groupPos));
                        break;
                    case ExpandableListView.PACKED_POSITION_TYPE_GROUP:
                        menu.add(0, MENU_GROUP_DELETE, 0, "Delete record");
                        menu.add(0, MENU_GROUP_INFO,0, "Record info");
                        selectedList =mItems.get(listAdapter.getGroup(groupPos));
                        break;
                }
//                if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
//                    Toast.makeText(getActivity(), "Child long press", Toast.LENGTH_SHORT).show();
//                } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
//                    Toast.makeText(getActivity(), "Group long press", Toast.LENGTH_SHORT).show();
//                }

//                int menuItemIndex = item.ItemId;
//
//                ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.MenuInfo;
//
//                PackedPositionType type = ExpandableListView.GetPackedPositionType(info.PackedPosition);
//                if ((int)type == MemoboardsListAdapter.PACKED_POSITION_TYPE_CHILD) {
//                    //want to get selected child object or id
//                    return true;
//                } else if ((int)type == MemoboardsListAdapter.PACKED_POSITION_TYPE_GROUP) {
//                    //want to get selected group object or id
//                    return true;
//                }
            }
        });


        return rootView;
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        mItems = new HashMap<String, List<Bookmark>>();

        List<Bookmark> tempArray = new ArrayList<Bookmark>();
        bookmarksList = ParseBookmarkFiles.getBookmarks();
        if (bookmarksList != null) {
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
            mItems.put(listDataHeader.get(groupCount - 1), tempArray);

        } else {
            Log.d(TAG_LOG, "no bookmarks");
            Toast.makeText(getActivity(), "No bookmarks", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteChild() {
        File bookmarkToDelete = new File(selectedBookmark.getBookmarkPath());
        if (selectedList.isEmpty()){
            Log.d(TAG_LOG, "no list");
        }
        if (!selectedList.contains(selectedBookmark)){
            Log.d(TAG_LOG, "no selected bookmark in list");
        }else {
            Log.d(TAG_LOG, "selected bookmark in list");
            selectedList.remove(selectedBookmark);

            bookmarkToDelete.delete();
        }
        Thread parseBookmarkFiles = new Thread(new ParseBookmarkFiles());
        parseBookmarkFiles.start();
        //bookmarksList=ParseBookmarkFiles.getBookmarks();
        listAdapter.notifyDataSetChanged();

        //Toast.makeText(getActivity(), "Delete child", Toast.LENGTH_SHORT).show();

    }

    private void deleteGroup() {
        File recordToDelete = new File(selectedList.get(0).getPath());
        for (int i=0;i<selectedList.size();i++){
            File bookmarkToDelete = new File(selectedList.get(i).getBookmarkPath());
            //Log.e(TAG_LOG,selectedList.get(i).getBookmarkPath());
            //selectedList.remove(i);
            bookmarkToDelete.delete();
        }
        selectedList.clear();
        mItems.remove(selectedList);
        listDataHeader.remove(selectedList);
        recordToDelete.delete();
        Thread parseBookmarkFiles = new Thread(new ParseBookmarkFiles());
        parseBookmarkFiles.start();
        //bookmarksList=ParseBookmarkFiles.getBookmarks();
        listAdapter.notifyDataSetChanged();
        //Toast.makeText(getActivity(), "Delete record", Toast.LENGTH_SHORT).show();
    }



}
