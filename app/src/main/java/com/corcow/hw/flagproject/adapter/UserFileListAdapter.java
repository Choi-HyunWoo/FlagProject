package com.corcow.hw.flagproject.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.corcow.hw.flagproject.model.UserFileChild;
import com.corcow.hw.flagproject.model.UserFileParent;
import com.corcow.hw.flagproject.model.json.UserFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by multimedia on 2016-05-17.
 */
public class UserFileListAdapter extends BaseExpandableListAdapter {

    List<UserFileParent> items = new ArrayList<UserFileParent>();

    public void add(UserFile item, String isPublic) {
        UserFileParent parent = new UserFileParent();
        parent._id = item._id;
        parent.fileName = item.fileName;
        parent.flagName = item.flagName;

        UserFileChild child = new UserFileChild();
        child.isPublic = isPublic;
        parent.child = child;

        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).child;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return (long)groupPosition << 32 | 0xFFFFFFFF;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return (long)groupPosition << 32 | childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {


        return null;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}
