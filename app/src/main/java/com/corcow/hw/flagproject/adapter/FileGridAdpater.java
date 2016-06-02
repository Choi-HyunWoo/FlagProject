package com.corcow.hw.flagproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.model.FileGridItem;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;


/**
 * Created by multimedia on 2016-04-29.
 */
public class FileGridAdpater extends BaseDynamicGridAdapter {

//    ArrayList<FileGridItem> items = new ArrayList<FileGridItem>();
//
//    int mSelectedPosition = -1;

    public FileGridAdpater(Context context, int columnCount) {
        super(context, columnCount);
    }

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

    public void setSelectedState(boolean selectedState, int preSelectedPos, int selectedPos) {
        if (preSelectedPos != -1) {
            ((FileGridItem) super.getItem(preSelectedPos)).setSelectedState(false);
            ((FileGridItem) super.getItem(selectedPos)).setSelectedState(selectedState);
        } else {
            ((FileGridItem) super.getItem(selectedPos)).setSelectedState(selectedState);
        }
        notifyDataSetChanged();
    }

    public boolean getSelectedState(int selectedPos) {
        return ((FileGridItem) super.getItem(selectedPos)).getSelectedState();
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ItemViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.file_item_view, null);
            holder = new ItemViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemViewHolder)convertView.getTag();
        }
        holder.build((FileGridItem) getItem(position));

        if (((FileGridItem)super.getItem(position)).isSelected){
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent_transparent));
        } else {
            convertView.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
        }

        return convertView;
    }

    public class ItemViewHolder {
        ImageView fileIconView;
        TextView fileNameView;

        public ItemViewHolder (View view) {
            fileNameView = (TextView) view.findViewById(R.id.text_file_name);
            fileIconView = (ImageView) view.findViewById(R.id.image_file_icon);
        }
        public void build(FileGridItem item) {
            if (item.iconImgResource == FileGridItem.IS_IMAGE_FILE) {
                Glide.with(getContext())
                        .load(item.absolutePath)
                        .thumbnail(0.1f)
                        .into(fileIconView);
            } else if (item.iconImgResource == FileGridItem.IS_VIDEO_FILE) {
                Glide.with(getContext())
                        .load(item.absolutePath)
                        .thumbnail(0.1f)
                        .into(fileIconView);
            } else {
                Glide.with(getContext()).load(item.iconImgResource).into(fileIconView);
            }
            fileNameView.setText(item.fileName);
        }

    }
}
