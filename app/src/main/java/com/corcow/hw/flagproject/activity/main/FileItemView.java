package com.corcow.hw.flagproject.activity.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.util.Utilities;

import java.io.File;

/**
 * Created by multimedia on 2016-04-29.
 */
public class FileItemView extends FrameLayout {

    ImageView fileIconView;
    TextView fileNameView;

    public FileItemView(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.file_item_view, this);
        fileIconView = (ImageView)findViewById(R.id.image_file_icon);
        fileNameView = (TextView)findViewById(R.id.text_file_name);
    }

    public void setViewItem(FileItem item) {
        if (item.iconImgResource == FileItem.IS_IMAGE_FILE) {
//            Utilities.getThumnailPath(getContext(), item.absolutePath);
            File imgFile = new File(Utilities.getThumnailPath(getContext(),item.absolutePath));
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                fileIconView.setImageBitmap(myBitmap);
            }
//            fileIconView.setImageBitmap(bmThumbnail);
//            fileIconView.setI
        } else if (item.iconImgResource == FileItem.IS_VIDEO_FILE) {
            Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(item.absolutePath, MediaStore.Video.Thumbnails.MICRO_KIND);
            fileIconView.setImageBitmap(bmThumbnail);
        } else {
            fileIconView.setImageResource(item.iconImgResource);
        }

        fileNameView.setText(item.fileName);
    }

}
