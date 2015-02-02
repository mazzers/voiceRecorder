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
 * Created by mazzers on 26. 11. 2014.
 */
public class ListViewAdapter extends ArrayAdapter<Bookmark> {
    Utils utils;

    public ListViewAdapter(Context context, List<Bookmark> items) {

        super(context, R.layout.listview_item, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listview_item, parent, false);

            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.itemIcon = (ImageView) convertView.findViewById(R.id.itemIcon);
            viewHolder.itemName = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.itemMessage = (TextView) convertView.findViewById(R.id.tvDescription);
            viewHolder.itemTime = (TextView) convertView.findViewById(R.id.itemTime);
            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Bookmark item = getItem(position);
        utils = new Utils();
        //viewHolder.ivIcon.setImageDrawable(item.icon);
        viewHolder.itemName.setText(item.getName());
        viewHolder.itemMessage.setText(item.getMessage());
        viewHolder.itemTime.setText(utils.timeToString(item.getTime()));
        switch (item.getType()){
            case 1:
                viewHolder.itemIcon.setBackgroundResource(R.drawable.new_bookmark);
                break;
            case 2:
                viewHolder.itemIcon.setBackgroundResource(R.drawable.new_imp);
                break;
            case 3:
                viewHolder.itemIcon.setBackgroundResource(R.drawable.new_question);
                break;
            default:
                viewHolder.itemIcon.setBackgroundResource(R.drawable.new_bookmark);
                break;
        }

        return convertView;
    }





    private static class ViewHolder {
        //ImageView ivIcon;
        //TextView tvTitle;
        //TextView tvDescription;
        TextView itemName;
        TextView itemMessage;
        ImageView itemIcon;
        TextView itemTime;
    }
}