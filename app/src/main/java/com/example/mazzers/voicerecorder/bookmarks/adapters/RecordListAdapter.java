package com.example.mazzers.voicerecorder.bookmarks.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.utils.Utils;

import java.io.File;
import java.util.List;

/**
 * Created by mazzers on 23. 3. 2015.
 */
public class RecordListAdapter extends ArrayAdapter<File> {
    //private MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    private MediaPlayer mediaPlayer;

    public RecordListAdapter(Context context, List<File> items) {
        super(context, R.layout.file_listview_item, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.file_listview_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemName = (TextView) convertView.findViewById(R.id.recordTitle);
            viewHolder.itemTime = (TextView) convertView.findViewById(R.id.recordTime);
            viewHolder.itemCreate = (TextView) convertView.findViewById(R.id.recordDate);
            viewHolder.itemSize = (TextView) convertView.findViewById(R.id.recordSize);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        File item = getItem(position);

        if (item == null) {
            String TAG_LOG = "RecordListAdapter";
            Log.d(TAG_LOG, "file by pos is null");
        }

        viewHolder.itemName.setText(item.getName());
        viewHolder.itemSize.setText(Utils.readableFileSize(item.length()));
        viewHolder.itemCreate.setText(Utils.dateToString(item.lastModified()));

        return convertView;
    }

    private static class ViewHolder {
        TextView itemName;
        TextView itemTime;
        TextView itemCreate;
        TextView itemSize;
    }
}

