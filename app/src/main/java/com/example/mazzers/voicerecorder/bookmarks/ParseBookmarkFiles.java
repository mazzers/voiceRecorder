package com.example.mazzers.voicerecorder.bookmarks;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Vashchenko Vitaliy A11B0529P
 * PRJ5 - Voice bookmarks
 * <p/>
 * Bookmarks parser
 */
public class ParseBookmarkFiles implements Runnable {
    private File dir = new File(Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/");
    private static Bookmark[] bookmarks;
    private static ArrayList<Bookmark> bookmarkArrayList;
    private ReadFromXML obj;
    private String TAG_LOG = "Write&Parse";

    @Override
    public void run() {
        Log.d(TAG_LOG, "ParseBookmarkFiles: in run");
        traverse(dir);

    }

    /**
     * Bookmark parsing
     *
     * @param dir - directory with bookmarks
     */
    public void traverse(File dir) {
        if (dir.exists()) {
            Log.d(TAG_LOG, "ParseBookmarkFiles: folder exist");
            File[] files = dir.listFiles();
            bookmarks = new Bookmark[files.length];
            bookmarkArrayList = new ArrayList<Bookmark>();

            Log.d(TAG_LOG, "ParseBookmarkFiles: create array od bookmarks");
            if (dir.listFiles(new FileExtensionFilter()).length > 0) {
                for (File file : dir.listFiles(new FileExtensionFilter())) {

                    if (file.isDirectory()) {
                        traverse(file);
                    } else {
                        Log.d(TAG_LOG, file.getPath());
                        //todo bad file exception
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
    }

    /**
     * Get parsed bookmarks
     *
     * @return bookmarks list
     */
    public static Bookmark[] getBookmarks() {
        if (bookmarks.length != 0) {
            return bookmarks;
        } else {
            return null;
        }


    }

    /**
     * Parsing filter. Parse only .xml files
     */
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".xml"));
        }
    }
}
