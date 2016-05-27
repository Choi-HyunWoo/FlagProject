package com.corcow.hw.flagproject.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.corcow.hw.flagproject.model.UserFileChild;
import com.corcow.hw.flagproject.model.UserFileParent;
import com.corcow.hw.flagproject.model.json.UserFile;
import com.corcow.hw.flagproject.view.UserFileChildView;
import com.corcow.hw.flagproject.view.UserFileParentView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by multimedia on 2016-05-17.
 */
public class UserFileListAdapter extends BaseExpandableListAdapter implements UserFileChildView.OnDownloadBtnClickListener {

    List<UserFileParent> items = new ArrayList<UserFileParent>();

    public void add(UserFile item, String isPublic, String pageOwner) {
        UserFileParent parent = new UserFileParent();
        parent._id = item._id;
        parent.fileName = item.fileName;
        parent.flagName = item.flagName;
        parent.pageOwner = pageOwner;

        UserFileChild child = new UserFileChild();
        child.isPublic = isPublic;
        parent.child = child;

        items.add(parent);

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
        UserFileParentView view;
        if (convertView != null) {
            view = (UserFileParentView) convertView;
        } else {
            view = new UserFileParentView(parent.getContext());
        }
        view.setParentItem(items.get(groupPosition));
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        UserFileChildView view;
        if (convertView != null) {
            view = (UserFileChildView) convertView;
        }
        else {
            view = new UserFileChildView(parent.getContext());
        }
        view.setChildItem(items.get(groupPosition).child, items.get(groupPosition));
        view.setOnDownloadBtnClickListener(this);
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }



    @Override
    public void onDownloadBtnClick(String pageOwnerID, String flagName) {
        mListener.onAdapterDownloadBtnClick(pageOwnerID, flagName);
    }

    public interface OnAdapterDownloadBtnClickListener {
        public void onAdapterDownloadBtnClick(String pageOwnerID, String flagName);
    }
    OnAdapterDownloadBtnClickListener mListener;
    public void setOnAdapterBtnClickListener(OnAdapterDownloadBtnClickListener listener) {
        mListener = listener;
    }

}
