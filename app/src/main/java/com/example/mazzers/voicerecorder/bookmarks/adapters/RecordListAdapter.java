package com.example.mazzers.voicerecorder.bookmarks.adapters;

import android.content.Context;

import com.example.mazzers.voicerecorder.bookmarks.Bookmark;

import java.util.List;

/**
 * Created by mazzers on 23. 3. 2015.
 */
public class RecordListAdapter extends ListViewAdapter {
    public RecordListAdapter(Context context, List<Bookmark> items) {
        super(context, items);
    }
    //todo context menu delete record
}
