package com.example.mazzers.voicerecorder.utils;

import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;

/**
 * voiceRecorder application
 *
 * @author Vitaliy Vashchenko A11B0529P
 *         Records loader
 */
public class FilesLoader extends Loader<File[]> {
    private File dir; // folder to work with
    private static final String TAG_LOG = "FILE_LOADER"; //loader tag

    /**
     * New file loader
     *
     * @param context loader context
     * @param args    loader arguments
     */
    public FilesLoader(Context context, Bundle args) {
        super(context);
        dir = new File(args.getString("dir"));
    }

    /**
     * Loader main method
     */
    @Override
    protected void onForceLoad() {
        Log.d(TAG_LOG, hashCode() + " : onForceLoad: " + dir);
        super.onForceLoad();
        File[] mFiles = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".mp4") || name.toLowerCase().endsWith(".3gpp");
            }
        });
        deliverResult(mFiles);

    }

    /**
     * Deliver processed data to caller
     *
     * @param data return processed data
     */
    @Override
    public void deliverResult(File[] data) {
        super.deliverResult(data);
        Log.d(TAG_LOG, hashCode() + " deliverResult: " + data.length);
    }

    /**
     * On loader creation
     */
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
