package com.example.mazzers.voicerecorder.bookmarks;

/**
 * Created by mazzers on 24. 11. 2014.
 */
public class Bookmark {
    //todo add message possibility
    private String path,fileName;
    private int time;

    public Bookmark(String path,String fileName, int time) {
        this.path = path;
        this.time = time;
        this.fileName = fileName;

    }

    public String getPath() {
        return path;
    }

    public int getTime() {
        return time;
    }

    public String getName(){ return fileName;}

    public void setPath(String path) {
        this.path = path;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
