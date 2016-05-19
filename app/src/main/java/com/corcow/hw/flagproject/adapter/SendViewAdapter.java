package com.corcow.hw.flagproject.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.corcow.hw.flagproject.view.SendView;
import com.corcow.hw.flagproject.model.FileItem;

/**
 * Created by multimedia on 2016-05-18.
 */
public class SendViewAdapter extends BaseAdapter{

    FileItem item = new FileItem();

    public void setSendItem(String fileName, String absolutePath) {
        item.fileName = fileName;
        item.absolutePath = absolutePath;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SendView view = null;
        if (convertView != null) {
            view = (SendView)convertView;
        } else {
            view = new SendView(parent.getContext());
        }
        view.setMinimumHeight(parent.getMeasuredHeight());
        view.setViewItem(item.fileName);
//        view.setMesuredHeight(parent.getMeasuredHeight());
        return view;
    }
}
