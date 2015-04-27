package com.example.mazzers.voicerecorder.utils;

import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by mazzers on 15. 4. 2015.
 */
public class FilesLoader extends Loader<File[]> {
    private File dir;
    private static final String TAG_LOG = "FILE_LOADER";

    /**
     * Stores away the application context associated with context.
     * Since Loaders can be used across multiple activities it's dangerous to
     * store the context directly; always use {@link #getContext()} to retrieve
     * the Loader's Context, don't use the constructor argument directly.
     * The Context returned by {@link #getContext} is safe to use across
     * Activity instances.
     *
     * @param context used to retrieve the application context.
     */
    public FilesLoader(Context context, Bundle args) {
        super(context);
        dir = new File(args.getString("dir"));
    }

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

    @Override
    public void deliverResult(File[] data) {
        super.deliverResult(data);
        Log.d(TAG_LOG, hashCode() + " deliverResult: " + data.length);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
