package com.example.mazzers.voicerecorder.bookmarks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.bookmarks.Bookmark;
import com.example.mazzers.voicerecorder.utils.Utils;

import java.util.List;

/**
 * voiceRecorder application
 *
 * @author Vitaliy Vashchenko A11B0529P
 *         Adapter for bookmark files
 */
public class BookmarkListAdapter extends ArrayAdapter<Bookmark> {
    /**
     * Class constructor
     *
     * @param context adapter context
     * @param items   adapter data
     */
    public BookmarkListAdapter(Context context, List<Bookmark> items) {

        super(context, R.layout.listview_item, items);
    }

    /**
     * Generate view by position
     *
     * @param position    position in list
     * @param convertView view on position
     * @param parent      view parent
     * @return view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listview_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemIcon = (ImageView) convertView.findViewById(R.id.itemIcon);
            viewHolder.itemName = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.itemMessage = (TextView) convertView.findViewById(R.id.tvDescription);
            viewHolder.itemTime = (TextView) convertView.findViewById(R.id.itemTime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Bookmark item = getItem(position);
        viewHolder.itemName.setText(item.getName());
        viewHolder.itemMessage.setText(item.getMessage());
        viewHolder.itemTime.setText(Utils.timeToString(item.getTime()));
        //switch icon by bookmark type
        switch (item.getType()) {
            case 1:
                viewHolder.itemIcon.setBackgroundResource(R.mipmap.text_bookmark_focused);
                break;
            case 2:
                viewHolder.itemIcon.setBackgroundResource(R.mipmap.imp_focused);
                break;
            case 3:
                viewHolder.itemIcon.setBackgroundResource(R.mipmap.quest_focused);
                break;
            default:
                viewHolder.itemIcon.setBackgroundResource(R.mipmap.imp_focused);
                break;
        }

        return convertView;
    }


    /**
     * ViewHolder class. Contains item view elements
     */
    private static class ViewHolder {
        TextView itemName;
        TextView itemMessage;
        ImageView itemIcon;
        TextView itemTime;
    }
}