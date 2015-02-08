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

/**
 * Vashchenko Vitaliy A11B0529P
 * PRJ5 - Voice bookmarks
 *
 * Adapter for expandable list layout
 * Contain bookmarks
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, List<Bookmark>> _listDataChild;
    private Utils utils;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<Bookmark>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    /**
     * Get child by position
     *
     * @param groupPosition
     * @param childPosititon
     * @return child object
     */
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    /**
     * Get child ID by position
     *
     * @param groupPosition
     * @param childPosition
     * @return child ID
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * Get child view by position
     * @param groupPosition
     * @param childPosition
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return child view
     */
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        Bookmark child = (Bookmark) getChild(groupPosition, childPosition);
        utils = new Utils();

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
        switch (child.getType()) {
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
        txtListChild.setText(child.getName());
        txtListChildMessage.setText(child.getMessage());

        return convertView;
    }

    /**
     * Get children count
     * @param groupPosition
     * @return children count
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    /**
     * Get group by position
     * @param groupPosition
     * @return group
     */
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    /**
     * Get group count
     * @return group count
     */
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    /**
     * Get group id by position
     * @param groupPosition
     * @return group ID
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * Get group view by position
     * @param groupPosition
     * @param isExpanded
     * @param convertView
     * @param parent
     * @return group view
     */
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

    /**
     * Check if has stable iD
     * @return
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Check if child selectable
     * @param groupPosition
     * @param childPosition
     * @return true
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}