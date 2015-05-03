package com.example.mazzers.voicerecorder.bookmarks;

/**
 * voiceRecorder application
 * @author Vitaliy Vashchenko A11B0529P
 * Bookmark representation in application
 */
public class Bookmark {

    private String path; // audio file path
    private String fileName; // audio file name
    private String message; // bookmark text
    private String bookmarkPath; // bookmark file path
    private int time; // bookmark time
    private int type; // bookmark type

    /**
     * Set bookmark path
     * @param bookmarkPath new bookmark path
     */
    public void setBookmarkPath(String bookmarkPath) {
        this.bookmarkPath = bookmarkPath;
    }

    /**
     * Set bookmark message
     *
     * @param message new message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Set bookmark path
     *
     * @param path new path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Set bookmark name
     *
     * @param fileName new name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Set bookmark time
     *
     * @param time new time
     */
    public void setTime(int time) {
        this.time = time;
    }


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
     * @param type new bookmark type
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
     * Empty bookmark constructor
     */
    public Bookmark() {

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
     * @return bookmark path
     */
    public String getBookmarkPath() {
        return bookmarkPath;
    }


}
