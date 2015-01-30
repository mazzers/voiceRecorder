package com.example.mazzers.voicerecorder.bookmarks;

/**
 * Created by mazzers on 24. 11. 2014.
 */
public class Bookmark {
    //todo add message possibility
    private String path,fileName,message;
    private int time;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Bookmark(String path,String fileName, int time,String message,int type) {
        this.path = path;
        this.time = time;
        this.fileName = fileName;
        this.message = message;
        this.type = type;


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

    public String getMessage(){
        return message;
    }
}
