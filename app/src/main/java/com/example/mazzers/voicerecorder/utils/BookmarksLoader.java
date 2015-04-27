package com.example.mazzers.voicerecorder.utils;

import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;

import com.example.mazzers.voicerecorder.bookmarks.Bookmark;
import com.example.mazzers.voicerecorder.bookmarks.ReadFromXML;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mazzers on 15. 4. 2015.
 */
public class BookmarksLoader extends Loader<HashMap<String, List<Bookmark>>> {
    /**
     * Stores away the application context associated with context.
     * Since Loaders can be used across multiple activities it's dangerous to
     * store the context directly; always use {@link #getContext()} to retrieve
     * the Loader's Context, don't use the constructor argument directly.
     * The Context returned by {@link #getContext} is safe to use across
     * Activity instances.
     *
     * @param context used to retrieve the application context.
     */

    private final File dir;
    private final String TAG_LOG = "BOOKMARK_LOADER";
    private static Bookmark[] bookmarks;
    private static ArrayList<Bookmark> bookmarkArrayList;
    private static HashMap<String, List<Bookmark>> mItems;

    public BookmarksLoader(Context context, Bundle args) {
        super(context);
        dir = new File(args.getString("dir_bookmarks"));
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        traverse(dir);
        deliverResult(mItems);

    }

    void traverse(File dir) {
        if (dir.exists()) {
            ///Log.d(TAG_LOG, "ParseBookmarkFiles: folder exist");
            File[] files = dir.listFiles();
            //bookmarks = new Bookmark[files.length];
            bookmarkArrayList = new ArrayList<>();

            //Log.d(TAG_LOG, "ParseBookmarkFiles: create array od bookmarks");
            if (dir.listFiles(new FileExtensionFilter()).length > 0) {
                for (File file : dir.listFiles(new FileExtensionFilter())) {

                    if (file.isDirectory()) {
                        traverse(file);
                    } else {
                        //Log.d(TAG_LOG, file.getPath());

                        ReadFromXML obj = new ReadFromXML(file);
                        obj.fetchXML();
                        while (obj.parsingComplete) ;
                        bookmarkArrayList.add(new Bookmark(obj.getPath(), obj.getBookmarkPath(), obj.getName(), obj.getTime(), obj.getMessage(), obj.getType()));

                    }
                }
                Log.d(TAG_LOG, "Bookmarks size:" + bookmarkArrayList.size());
            }
        } else {
            Log.e(TAG_LOG, "ParseBookmarkFiles: dir not exist");
        }
        //bookmarks = bookmarkArrayList.toArray(new Bookmark[bookmarkArrayList.size()]);
        //MainActivity.setBookmarks(bookmarkArrayList);
        classifyBookmarks();

    }

    void classifyBookmarks() {
        List<String> listDataHeader;
        //HashMap<String, List<Bookmark>> mItems;
        listDataHeader = new ArrayList<>();
        mItems = new HashMap<>();

        List<Bookmark> tempArray = new ArrayList<>();
        //Bookmark[] bookmarksList = MainActivity.getBookmarks();

        if (bookmarkArrayList != null && bookmarkArrayList.size() > 0) {

            String groupName = bookmarkArrayList.get(0).getPath();
            int groupCount = 1;
            listDataHeader.add(groupName);

            for (int i = 0; i < bookmarkArrayList.size(); i++) {
                if (!bookmarkArrayList.get(i).getPath().equals(groupName)) {
                    //new group
                    //add array to previous group
                    mItems.put(listDataHeader.get(groupCount - 1), tempArray);
                    //increase group pointer
                    groupCount++;
                    //reinitialize array
                    tempArray = new ArrayList<>();
                    //set new group name
                    groupName = bookmarkArrayList.get(i).getPath();
                    //add groupname to array
                    listDataHeader.add(groupName);
                }
                tempArray.add(bookmarkArrayList.get(i));
            }
            mItems.put(listDataHeader.get(groupCount - 1), tempArray);
            //MainActivity.setmItems(mItems);
            //MainActivity.setListDataHeader(listDataHeader);
        } else {
            Log.d(TAG_LOG, "no bookmarks");
        }
    }

    @Override
    public void deliverResult(HashMap<String, List<Bookmark>> data) {
        Log.d(TAG_LOG, "deliverResult: " + data.size());
        super.deliverResult(data);
    }

    private class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.toLowerCase().endsWith(".xml"));
        }
    }
}
