package com.example.mazzers.voicerecorder.bookmarks.adapters;


import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.bookmarks.Bookmark;
import com.example.mazzers.voicerecorder.utils.Utils;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Bookmark>> _listDataChild;
    private Utils utils;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<Bookmark>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }


    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        Bookmark child =(Bookmark) getChild(groupPosition, childPosition);
        utils = new Utils();
        //final String childTime = (String) getChild(groupPosition,childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        TextView txtListChildMessage = (TextView) convertView
                .findViewById(R.id.lblListItemMessage);
        ImageView listChildIcon = (ImageView) convertView.findViewById(R.id.list_image);
        TextView txtListChildTime = (TextView) convertView.findViewById(R.id.lblbItemTime);
        txtListChildTime.setText(utils.timeToString(child.getTime()));
        switch (child.getType()){
            case 1:
                listChildIcon.setBackgroundResource(R.drawable.new_bookmark);
                break;
            case 2:
                listChildIcon.setBackgroundResource(R.drawable.new_imp);
                break;
            case 3:
                listChildIcon.setBackgroundResource(R.drawable.new_question);
                break;
            default:
                listChildIcon.setBackgroundResource(R.drawable.new_bookmark);
                break;
        }
        //txtListChild.setText("Bookmark at: "+String.valueOf(child.getTime())+" sec.");
        txtListChild.setText(child.getName());
        txtListChildMessage.setText(child.getMessage());

        //txtListChildTime.setText(child.getTime());
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        //Toast.makeText(_context, "Toast lalala long press", Toast.LENGTH_SHORT).show();
        return true;
    }
}