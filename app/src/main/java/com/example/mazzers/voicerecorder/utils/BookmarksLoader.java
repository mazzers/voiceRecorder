package com.example.mazzers.voicerecorder.utils;

import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;

import com.example.mazzers.voicerecorder.bookmarks.Bookmark;
import com.example.mazzers.voicerecorder.bookmarks.BookmarkParser;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * voiceRecorder application
 * @author Vitaliy Vashchenko A11B0529P
 * Bookmarks loader
 */
public class BookmarksLoader extends Loader<HashMap<String, List<Bookmark>>> {

    private final File dir; // dir with bookmarks
    private final String TAG_LOG = "BOOKMARK_LOADER"; //loader tag
    private static ArrayList<Bookmark> bookmarkArrayList; // bookmarks list
    private static HashMap<String, List<Bookmark>> mItems; // bookmarks hashMap

    /**
     * Loader constructor
     *
     * @param context loader context
     * @param args    loader arguments
     */
    public BookmarksLoader(Context context, Bundle args) {
        super(context);
        dir = new File(args.getString("dir_bookmarks"));
    }

    /**
     * On loader creation
     */
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    /**
     * Loader main method
     */
    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        traverse(dir);
        deliverResult(mItems);

    }

    /**
     * Parse selected dir
     *
     * @param dir dir to parse
     */
    void traverse(File dir) {
        if (dir.exists()) {
            bookmarkArrayList = new ArrayList<>();
            int skip = 0;
            if (dir.listFiles(new FileExtensionFilter()).length > 0) {
                for (File file : dir.listFiles(new FileExtensionFilter())) {

                    if (file.isDirectory()) {
                        traverse(file);
                    } else {
                        BookmarkParser parser = new BookmarkParser();
                        Bookmark temp = parser.parse(file);
                        if (temp != null) {
                            bookmarkArrayList.add(temp);
                        } else {
                            Log.d(TAG_LOG, "skip file");
                            skip++;
                        }

                    }
                }
                Log.d(TAG_LOG, "Bookmarks size:" + bookmarkArrayList.size());
                Log.d(TAG_LOG, "Skipped: "+skip);
            }
        } else {
            Log.e(TAG_LOG, "ParseBookmarkFiles: dir not exist");
        }
        classifyBookmarks();

    }

    /**
     * Make HashMap from list of bookmarks
     */
    void classifyBookmarks() {
        List<String> listDataHeader;
        listDataHeader = new ArrayList<>();
        mItems = new HashMap<>();

        List<Bookmark> tempArray = new ArrayList<>();

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
                    //add groupName to array
                    listDataHeader.add(groupName);
                }
                tempArray.add(bookmarkArrayList.get(i));
            }
            mItems.put(listDataHeader.get(groupCount - 1), tempArray);
        } else {
            Log.d(TAG_LOG, "no bookmarks");
        }
    }

    /**
     * Deliver data to caller
     * @param data parsed data
     */
    @Override
    public void deliverResult(HashMap<String, List<Bookmark>> data) {
        Log.d(TAG_LOG, "deliverResult: " + data.size());
        super.deliverResult(data);
    }

    /**
     * File extension filter
     */
    private class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.toLowerCase().endsWith(".xml"));
        }
    }
}
