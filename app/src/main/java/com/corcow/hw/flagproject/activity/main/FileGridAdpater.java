package com.corcow.hw.flagproject.activity.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.util.Utilities;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;

import java.io.File;
import java.util.List;

/**
 * Created by multimedia on 2016-04-29.
 */
public class FileGridAdpater extends BaseDynamicGridAdapter {

//    ArrayList<FileItem> items = new ArrayList<FileItem>();
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
            ((FileItem) super.getItem(preSelectedPos)).setSelectedState(false);
            ((FileItem) super.getItem(selectedPos)).setSelectedState(selectedState);
        } else {
            ((FileItem) super.getItem(selectedPos)).setSelectedState(selectedState);
        }
        notifyDataSetChanged();
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
        holder.build((FileItem) getItem(position));

        if (((FileItem)super.getItem(position)).isSelected){
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));
        } else {
            convertView.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
        }

        return convertView;
    }

    private class ItemViewHolder {
        private ImageView fileIconView;
        private TextView fileNameView;

        private ItemViewHolder (View view) {
            fileNameView = (TextView) view.findViewById(R.id.text_file_name);
            fileIconView = (ImageView) view.findViewById(R.id.image_file_icon);
        }
        void build(FileItem item) {
            if (item.iconImgResource == FileItem.IS_IMAGE_FILE) {
                File imgFile = new File(Utilities.getThumnailPath(getContext(), item.absolutePath));
                if(imgFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    fileIconView.setImageBitmap(myBitmap);
                }
            } else if (item.iconImgResource == FileItem.IS_VIDEO_FILE) {
                Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(item.absolutePath, MediaStore.Video.Thumbnails.MICRO_KIND);
                fileIconView.setImageBitmap(bmThumbnail);
            } else {
                fileIconView.setImageResource(item.iconImgResource);
            }

            fileNameView.setText(item.fileName);
        }

    }
}
