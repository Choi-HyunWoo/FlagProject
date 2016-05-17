package com.corcow.hw.flagproject.activity.userpage;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by multimedia on 2016-05-17.
 */
public class UserFileListAdapter extends BaseAdapter {

    List<UserFileListItem> items = new ArrayList<UserFileListItem>();

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserFileListItemView view = null;
        if (convertView == null) {
            view = new UserFileListItemView(parent.getContext());
        }
        else {
            view = (UserFileListItemView) convertView;
        }
        view.setViewItem(items.get(position));

        return view;
    }
}
