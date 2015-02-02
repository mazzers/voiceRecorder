package com.example.mazzers.voicerecorder.bookmarks;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mazzers on 24. 11. 2014.
 */
public class Bookmark implements Parcelable {
    //todo add message possibility
    private String path,fileName,message,bookmarkPath;
    private int time;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Bookmark(String path,String bookmarkPath, String fileName, int time,String message,int type) {
        this.path = path;
        this.bookmarkPath = bookmarkPath;
        this.time = time;
        this.fileName = fileName;
        this.message = message;
        this.type = type;


    }

    public Bookmark(Parcel in){
        String[] data = new String[5];

        in.readStringArray(data);
        this.path = data[0];
        this.bookmarkPath = data[1];
        this.fileName = data[2];
        this.time = Integer.valueOf(data[3]);
        this.message = data[4];
        this.type = Integer.valueOf(data[5]);

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

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Bookmark> CREATOR =
            new Parcelable.Creator<Bookmark>() {

                @Override
                public Bookmark createFromParcel(Parcel source) {
                    return new Bookmark(source);
                }

                @Override
                public Bookmark[] newArray(int size) {
                    return new Bookmark[size];
                }

            };

    public String getBookmarkPath() {
        return bookmarkPath;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(bookmarkPath);
        dest.writeString(fileName);
        dest.writeString(message);
        dest.writeInt(time);
        dest.writeInt(type);
    }
}
