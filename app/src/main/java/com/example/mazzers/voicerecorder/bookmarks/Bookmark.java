package com.example.mazzers.voicerecorder.bookmarks;

/**
 * Vashchenko Vitaliy A11B0529P
 * PRJ5 - Voice bookmarks
 * <p/>
 * Bookmark implementation. Created from parsed files.
 */
public class Bookmark {

    private final String path;
    private final String fileName;
    private final String message;
    private final String bookmarkPath;
    private final int time;
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
     * @param fileName     - audioFile name
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
     * Get audioFile path
     *
     * @return audioFile path
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
     * Get audioFile name
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



    /**
     * Get bookmark file path
     *
     * @return
     */
    public String getBookmarkPath() {
        return bookmarkPath;
    }

}
