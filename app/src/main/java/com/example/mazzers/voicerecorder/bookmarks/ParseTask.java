package com.example.mazzers.voicerecorder.bookmarks;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.mazzers.voicerecorder.MainActivity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by mazzers on 11. 3. 2015.
 */
public class ParseTask extends AsyncTask<Void, Void, Void> {
    private File dir = new File(Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/");
    private static Bookmark[] bookmarks;
    private static ArrayList<Bookmark> bookmarkArrayList;
    private ReadFromXML obj;
    private String TAG_LOG = "parseTask";

    @Override
    protected Void doInBackground(Void... params) {
        Log.d(TAG_LOG, "do in background");
        traverse(dir);
        return null;
    }

    public void traverse(File dir) {
        if (dir.exists()) {
            ///Log.d(TAG_LOG, "ParseBookmarkFiles: folder exist");
            File[] files = dir.listFiles();
            bookmarks = new Bookmark[files.length];
            bookmarkArrayList = new ArrayList<Bookmark>();

            //Log.d(TAG_LOG, "ParseBookmarkFiles: create array od bookmarks");
            if (dir.listFiles(new FileExtensionFilter()).length > 0) {
                for (File file : dir.listFiles(new FileExtensionFilter())) {

                    if (file.isDirectory()) {
                        traverse(file);
                    } else {
                        //Log.d(TAG_LOG, file.getPath());

                        obj = new ReadFromXML(file);
                        obj.fetchXML();
                        while (obj.parsingComplete) ;
                        bookmarkArrayList.add(new Bookmark(obj.getPath(), obj.getBookmarkPath(), obj.getName(), obj.getTime(), obj.getMessage(), obj.getType()));

                    }
                }
            }
        } else {
            Log.e(TAG_LOG, "ParseBookmarkFiles: dir not exist");
        }
        bookmarks = bookmarkArrayList.toArray(new Bookmark[bookmarkArrayList.size()]);
        MainActivity.setBookmarks(bookmarks);
    }


    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".xml"));
        }
    }
}
