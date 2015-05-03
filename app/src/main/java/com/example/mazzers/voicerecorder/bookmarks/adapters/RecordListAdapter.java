package com.example.mazzers.voicerecorder.bookmarks.adapters;

import android.content.Context;
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
 * voiceRecorder application
 *
 * @author Vitaliy Vashchenko A11B0529P
 *         Adapter for records list
 */
public class RecordListAdapter extends ArrayAdapter<File> {
    /**
     * Adapter container
     *
     * @param context adapter context
     * @param items   adapter data
     */
    public RecordListAdapter(Context context, List<File> items) {
        super(context, R.layout.file_listview_item, items);
    }

    /**
     * Generate items view by position
     *
     * @param position    item position
     * @param convertView item view
     * @param parent      parent view
     * @return item view
     */
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

    /**
     * Holder for view elements
     */
    private static class ViewHolder {
        TextView itemName;
        TextView itemTime;
        TextView itemCreate;
        TextView itemSize;
    }
}

