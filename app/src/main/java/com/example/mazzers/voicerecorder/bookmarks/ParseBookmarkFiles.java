package com.example.mazzers.voicerecorder.bookmarks;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by mazzers on 24. 11. 2014.
 */
public class ParseBookmarkFiles implements Runnable {
    private File dir = new File(Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/");
    private static Bookmark[] bookmarks;
    private static ArrayList<Bookmark> bookmarkArrayList;
    private ReadFromXML obj;
    private String TAG_LOG = "Write&Parse";

    // public static boolean traverseFinish;
    @Override
    public void run() {
        Log.d(TAG_LOG, "ParseBookmarkFiles: in run");
        traverse(dir);

    }

    public void traverse(File dir) {
        if (dir.exists()) {
            // traverseFinish = false;
            Log.d(TAG_LOG, "ParseBookmarkFiles: folder exist");
            File[] files = dir.listFiles();
            bookmarks = new Bookmark[files.length];
            bookmarkArrayList = new ArrayList<Bookmark>();

            Log.d(TAG_LOG, "ParseBookmarkFiles: create array od bookmarks");
//            for (File file : dir.listFiles(new FileExtensionFilter())) {
//                obj = new ReadFromXML(file);
//                obj.fetchXML();
//                while(obj.parsingComplete);
//                bookmarkArrayList.add(new Bookmark(obj.getPath(),obj.getName(),obj.getTime()));
//                // Adding each song to SongList
//                //songsList.add(song);
//            }
            if (dir.listFiles(new FileExtensionFilter()).length > 0) {
                for (File file : dir.listFiles(new FileExtensionFilter())) {
                    //File file = files[i];
                    if (file.isDirectory()) {
                        traverse(file);
                    } else {
                        Log.d(TAG_LOG, file.getPath());
                        //todo bad file exception
                        obj = new ReadFromXML(file);
                        obj.fetchXML();
                        while (obj.parsingComplete) ;
                        Log.d(TAG_LOG, "ParseBookmarkFiles: fill path from XML");
                        //pathTemp = obj.getPath();
                        Log.d(TAG_LOG, "ParseBookmarkFiles: fill time from XML");
                        //bookmarks[i].setTime(obj.getTime());
                        //timeTemp = obj.getTime();
                        //bookmarkTemp = new Bookmark(pathTemp,timeTemp);
                        Log.d(TAG_LOG, "ParseBookmarkFiles: Fill bookmark i-iteration");
                        //bookmarks[i] = new Bookmark(obj.getPath(),obj.getName(),obj.getTime());
                        bookmarkArrayList.add(new Bookmark(obj.getPath(), obj.getName(), obj.getTime()));
//                    Log.d(TAG_LOG,"ParseBookmarkFiles: ---->filled path: "+bookmarks[i].getPath());
//                    Log.d(TAG_LOG,"ParseBookmarkFiles: ---->filled name: "+bookmarks[i].getName());
//                    Log.d(TAG_LOG,"ParseBookmarkFiles: ---->filled time at: "+String.valueOf(bookmarks[i].getTime()));


                        // do something here with the file
                    }
                }
            }
        } else {
            Log.e(TAG_LOG, "ParseBookmarkFiles: dir not exist");
        }
        //traverseFinish = true;
        bookmarks = bookmarkArrayList.toArray(new Bookmark[bookmarkArrayList.size()]);
    }

    // public static boolean getTraverse(){
    //    return traverseFinish;
    //}
    public static Bookmark[] getBookmarks() {
        return bookmarks;

    }

    public static ArrayList<Bookmark> getBookmarkArrayList() {
        return bookmarkArrayList;
    }

    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".xml") || name.endsWith(".XML"));
        }
    }
}
