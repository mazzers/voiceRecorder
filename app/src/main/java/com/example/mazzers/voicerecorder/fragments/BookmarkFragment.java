package com.example.mazzers.voicerecorder.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.bookmarks.Bookmark;
import com.example.mazzers.voicerecorder.bookmarks.ListViewAdapter;
import com.example.mazzers.voicerecorder.bookmarks.ParseBookmarkFiles;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mazzers on 26. 11. 2014.
 */
//public class BookmarkFragment extends ListFragment {
public class BookmarkFragment extends ListFragment {
    private View rootView;
    private static Bookmark[] bookmarksList;
    private String[] files;
    private int[] time;
    private List<Bookmark> mItems;
    private String TAG_LOG = "myLogs";
    //ArrayList<String> groupItem = new ArrayList<String>();
    //ArrayList<Object> childItem = new ArrayList<Object>();
    //ExpandableListAdapter listAdapter;
    // expListView;
    //static List<String> listDataHeader;
    //static HashMap<String, List<String>> listDataChild;

    public BookmarkFragment() {
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        rootView = inflater.inflate(R.layout.bookmark_layout,container,false);
//        expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
//
//        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
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
//        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(rootView.getContext(),
//                        listDataHeader.get(groupPosition) + " Expanded",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // Listview Group collasped listener
//        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//
//            @Override
//            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(rootView.getContext(),
//                        listDataHeader.get(groupPosition) + " Collapsed",
//                        Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        // Listview on child click listener
//        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v,
//                                        int groupPosition, int childPosition, long id) {
//                // TODO Auto-generated method stub
//                Toast.makeText(
//                        rootView.getContext(),
//                        listDataHeader.get(groupPosition)
//                                + " : "
//                                + listDataChild.get(
//                                listDataHeader.get(groupPosition)).get(
//                                childPosition), Toast.LENGTH_SHORT)
//                        .show();
//                return false;
//            }
//        });
//        return rootView;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_LOG, "BookmarkFragment: onCreate");
        super.onCreate(savedInstanceState);

        mItems = new ArrayList<Bookmark>();
        // get the listview


        // preparing list data
        //prepareListData();


        fillStrings();
        //listAdapter = new ExpandableListAdapter(rootView.getContext(), listDataHeader, listDataChild);

        // setting list adapter
        //expListView.setAdapter(listAdapter);


    }

   public void fillStrings() {
        //listDataHeader = new ArrayList<String>();
        //listDataChild = new HashMap<String, List<String>>();
       // List<String> child = new ArrayList<String>();
       // int groupCount = 0;
        //Log.d(TAG_LOG, "BookmarkFragment: fillStrings");
        bookmarksList = ParseBookmarkFiles.getBookmarks();
        //Log.d(TAG_LOG,"ParseBookmarkFiles: getbookmarks");
        //Log.d(TAG_LOG,bookmarksList[0].getPath());
        //String groupName =bookmarksList[0].getPath();
       // addGroup(bookmarksList[0]);
        //Log.d(TAG_LOG,"add group");

        //files = new String[bookmarksList.length];
        // time = new int[bookmarksList.length];
        for (int i = 0; i < bookmarksList.length; i++) {
            //Log.d(TAG_LOG,"Букмарков: "+String.valueOf(bookmarksList.length));
            //Log.d(TAG_LOG,"int iteration: "+ String.valueOf(i));
            //files[i] = bookmarksList[i].getPath();
            //time[i] = bookmarksList[i].getTime();
          //  if(!bookmarksList[i].getPath().equals(groupName)){

              //  listDataChild.put(listDataHeader.get(groupCount), child);
                //Log.d(TAG_LOG,"put listdacachild");
              //  groupCount++;
                //childItem.add(child);
              //  child = new ArrayList<String>();
                //it's a new group
              //  addGroup(bookmarksList[i]);
             //   groupName=bookmarksList[i].getName();
           // }
            //Log.d(TAG_LOG,"add new value to child");
          //  child.add(String.valueOf(bookmarksList[i].getTime()));

            //mItems.add(new Bookmark(bookmarksList[i].getPath(),bookmarksList[i].getName(), bookmarksList[i].getTime()));
            mItems.add(bookmarksList[i]);

        }
        setListAdapter(new ListViewAdapter(getActivity(), mItems));


    }

    public static void addGroup(Bookmark bookmark){
        //Log.d(TAG_LOG,"add group method");
        //listDataHeader.add(bookmark.getName());

    }
    public void addChild(){

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
        Log.d(TAG_LOG,"BookmarkFragment: Start play on: "+ item.getTime());

        Bundle bundle = new Bundle();
        bundle.putString("filePath",item.getPath());
        bundle.putInt("fileTime",item.getTime());
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        //PlayerFragment.callBookmarkPlay(item.getPath(),item.getTime());


        // do something

    }
}

