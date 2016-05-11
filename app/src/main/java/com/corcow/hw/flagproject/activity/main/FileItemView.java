package com.corcow.hw.flagproject.activity.main;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.corcow.hw.flagproject.R;

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

    public void setFileItem(FileItem item) {
        fileIconView.setImageResource(item.iconImgResource);
        fileNameView.setText(item.fileName);
    }

}
