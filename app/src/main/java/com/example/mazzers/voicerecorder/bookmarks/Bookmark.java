package com.example.mazzers.voicerecorder.bookmarks;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Vashchenko Vitaliy A11B0529P
 * PRJ5 - Voice bookmarks
 * <p/>
 * Bookmark implementation. Created from parsed files.
 */
public class Bookmark implements Parcelable {

    private String path, fileName, message, bookmarkPath;
    private int time;
    private int type;

    /**
     * Get bookmark type
     *
     * @return type
     */
    public int getType() {
        return type;
    }

    /**
     * Set bookmark type
     *
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Bookmark constructor
     *
     * @param path         - path to audio file
     * @param bookmarkPath - path to bookmark file
     * @param fileName     - audiofile name
     * @param time         - bookmark time
     * @param message      - bookmark message
     * @param type         - bookmark type
     */
    public Bookmark(String path, String bookmarkPath, String fileName, int time, String message, int type) {
        this.path = path;
        this.bookmarkPath = bookmarkPath;
        this.time = time;
        this.fileName = fileName;
        this.message = message;
        this.type = type;


    }

    /**
     * Parcelable method for sending bookmark object between fragments
     *
     * @param in
     */
    public Bookmark(Parcel in) {
        String[] data = new String[5];

        in.readStringArray(data);
        this.path = data[0];
        this.bookmarkPath = data[1];
        this.fileName = data[2];
        this.time = Integer.valueOf(data[3]);
        this.message = data[4];
        this.type = Integer.valueOf(data[5]);

    }

    /**
     * Get audiofile path
     *
     * @return audiofile path
     */
    public String getPath() {
        return path;
    }

    /**
     * Get bookmark time
     *
     * @return time
     */
    public int getTime() {
        return time;
    }

    /**
     * Get audiofile name
     *
     * @return name
     */
    public String getName() {
        return fileName;
    }

    /**
     * Get bookmark message
     *
     * @return message
     */
    public String getMessage() {
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

    /**
     * Get bookmark file path
     *
     * @return
     */
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
