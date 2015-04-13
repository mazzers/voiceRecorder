package com.example.mazzers.voicerecorder.bookmarks;

import android.os.Environment;

import com.example.mazzers.voicerecorder.MainActivity;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by mazzers on 7. 4. 2015.
 */
public class ScanFiles implements Runnable {
    private File dir;
    private final File def_dir = new File(Environment.getExternalStorageDirectory() + "/voicerecorder/");

    public ScanFiles(File dir) {
        this.dir = dir;
    }

    public ScanFiles(String dir) {
        this.dir = new File(dir);
    }

    @Override
    public void run() {
        prepareData();
    }

    void prepareData() {


        File[] mFiles;
        mFiles = def_dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".mp4");
            }
        });

        MainActivity.setFiles(mFiles);

    }
}
