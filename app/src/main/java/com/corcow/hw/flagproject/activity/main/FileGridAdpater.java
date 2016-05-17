package com.corcow.hw.flagproject.activity.main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;

/**
 * Created by multimedia on 2016-04-29.
 */
public class FileGridAdpater extends BaseDynamicGridAdapter {

//    ArrayList<FileItem> items = new ArrayList<FileItem>();

    @Override
    public void add(Object item) {
        super.add(item);
        notifyDataSetChanged();
    }

    public void delete(int position) {
        super.delete(position);
        notifyDataSetChanged();
    }

    public void clear() {
        super.clear();
        notifyDataSetChanged();
    }

    protected FileGridAdpater(Context context, int columnCount) {
        super(context, columnCount);
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FileItemView view = null;
        if (convertView != null) {
            view = (FileItemView) convertView;
        } else {
            view = new FileItemView(parent.getContext());
        }
        view.setViewItem((FileItem)getItem(position));
        return view;
    }
}
