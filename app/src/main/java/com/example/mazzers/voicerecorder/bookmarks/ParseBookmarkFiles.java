package com.example.mazzers.voicerecorder.bookmarks;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by mazzers on 24. 11. 2014.
 */
public class ParseBookmarkFiles implements Runnable {
    private File dir = new File(Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/");
    private static Bookmark[] bookmarks;
    private ReadFromXML obj;
    private String TAG_LOG = "Write&Parse";
   // public static boolean traverseFinish;
    @Override
    public void run() {
        Log.d(TAG_LOG,"ParseBookmarkFiles: in run");
        traverse(dir);

    }
    public void traverse (File dir) {
        if (dir.exists()) {
           // traverseFinish = false;
            Log.d(TAG_LOG,"ParseBookmarkFiles: folder exist");
            File[] files = dir.listFiles();
            bookmarks = new Bookmark[files.length];
            Log.d(TAG_LOG,"ParseBookmarkFiles: create array od bookmarks");
            for (int i = 0; i < files.length; ++i) {
                File file = files[i];
                if (file.isDirectory()) {
                    traverse(file);
                } else {
                    Log.d(TAG_LOG,file.getPath());
                    //todo bad file exception
                    obj = new ReadFromXML(file);
                    obj.fetchXML();
                    while(obj.parsingComplete);
                    Log.d(TAG_LOG,"ParseBookmarkFiles: fill path from XML");
                    //pathTemp = obj.getPath();
                    Log.d(TAG_LOG, "ParseBookmarkFiles: fill time from XML");
                    //bookmarks[i].setTime(obj.getTime());
                    //timeTemp = obj.getTime();
                    //bookmarkTemp = new Bookmark(pathTemp,timeTemp);
                    Log.d(TAG_LOG,"ParseBookmarkFiles: Fill bookmark i-iteration");
                    bookmarks[i] = new Bookmark(obj.getPath(),obj.getName(),obj.getTime());
                    Log.d(TAG_LOG,"ParseBookmarkFiles: ---->filled path: "+bookmarks[i].getPath());
                    Log.d(TAG_LOG,"ParseBookmarkFiles: ---->filled name: "+bookmarks[i].getName());
                    Log.d(TAG_LOG,"ParseBookmarkFiles: ---->filled time at: "+String.valueOf(bookmarks[i].getTime()));



                    // do something here with the file
                }
            }
        }else {
            Log.d(TAG_LOG,"ParseBookmarkFiles: dir not exist");
        }
        //traverseFinish = true;
    }

   // public static boolean getTraverse(){
    //    return traverseFinish;
    //}
    public static Bookmark[] getBookmarks(){
        return bookmarks;

    }
}
