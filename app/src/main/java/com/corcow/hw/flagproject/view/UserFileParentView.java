package com.corcow.hw.flagproject.view;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.manager.UserManager;
import com.corcow.hw.flagproject.model.UserFileParent;
import com.corcow.hw.flagproject.util.Utilities;

/**
 * Created by multimedia on 2016-05-17.
 */
public class UserFileParentView extends FrameLayout {

    ImageView fileIconView;
    TextView indexView, flagNameView, fileNameView;

    String pageOwner;
    String loggedInID;

    public UserFileParentView(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.user_file_parent_view, this);
        loggedInID = UserManager.getInstance().getUserID();

        fileIconView = (ImageView) findViewById(R.id.image_file_icon);

        indexView = (TextView) findViewById(R.id.text_index);
        flagNameView = (TextView) findViewById(R.id.text_flag_name);
        fileNameView = (TextView) findViewById(R.id.text_file_name);
    }

    public void setParentItem (UserFileParent item, int position) {
        this.indexView.setText(""+(position+1));
        this.flagNameView.setText(item.flagName);
        this.fileNameView.setText(item.fileName);
        this.pageOwner = item.pageOwner;
        setIconImage(item.fileName, item.flagName);
    }

    private void setIconImage(String fileName, String flagName) {
        String extension = Utilities.getExtension(fileName);
        if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")
                || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("bmp")
                || extension.equalsIgnoreCase("gif")) {
            Glide.with(getContext())
                    .load("http://fflag.me/" + pageOwner + "/" + flagName)
                    .thumbnail(0.1f)
                    .dontAnimate()
                    .into(fileIconView);
        } else if (extension.equalsIgnoreCase("avi") || extension.equalsIgnoreCase("mp4")
                || extension.equalsIgnoreCase("wmv")) {
            Glide.with(getContext()).load(R.drawable.icon_file_video).into(fileIconView);
        } else if (extension.equalsIgnoreCase("mp3")) {
            Glide.with(getContext()).load(R.drawable.icon_file_mp3_small).into(fileIconView);
        } else if (extension.equalsIgnoreCase("hwp")) {
            Glide.with(getContext()).load(R.drawable.icon_file_hwp_small).into(fileIconView);
        } else if (extension.equalsIgnoreCase("ppt") || extension.equalsIgnoreCase("pptx")) {
            Glide.with(getContext()).load(R.drawable.icon_file_ppt_small).into(fileIconView);
        } else if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")
                || extension.equalsIgnoreCase("xlsm") || extension.equalsIgnoreCase("csv")) {
            Glide.with(getContext()).load(R.drawable.icon_file_xls_small).into(fileIconView);
        } else if (extension.equalsIgnoreCase("pdf")) {
            Glide.with(getContext()).load(R.drawable.icon_file_pdf_small).into(fileIconView);
        } else if (extension.equalsIgnoreCase("zip")) {
            Glide.with(getContext()).load(R.drawable.icon_file_zip).into(fileIconView);
        } else {
            Glide.with(getContext()).load(R.drawable.icon_file_unknown_small).into(fileIconView);
        }
    }

}
