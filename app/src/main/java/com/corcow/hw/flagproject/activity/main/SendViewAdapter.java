package com.corcow.hw.flagproject.activity.main;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by multimedia on 2016-05-18.
 */
public class SendViewAdapter extends BaseAdapter{

    String fileName;
    String filePath;

    public void setSendItem(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SendView view = new SendView(parent.getContext());
        view.setViewItem(fileName);
        return view;
    }
}
