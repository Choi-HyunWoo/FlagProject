package com.corcow.hw.flagproject.view;

import android.content.Context;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.manager.UserManager;
import com.corcow.hw.flagproject.model.UserFileParent;
import com.corcow.hw.flagproject.model.json.UserFile;
import com.corcow.hw.flagproject.util.Utilities;

/**
 * Created by multimedia on 2016-05-17.
 */
public class UserFileParentView extends FrameLayout {

    ImageView fileIconView;
    TextView flagNameView, fileNameView;

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
        flagNameView = (TextView) findViewById(R.id.text_flag_name);
        fileNameView = (TextView) findViewById(R.id.text_file_name);
    }

    public void setParentItem (UserFileParent item) {
        setIconImage(item.fileName);
        flagNameView.setText(item.fileName);
        fileNameView.setText(item.flagName);
        pageOwner = item.pageOwner;
    }

    private void setIconImage(String fileName) {
        String extension = Utilities.getExtension(fileName);
        if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")
                || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("bmp")
                || extension.equalsIgnoreCase("gif")) {
            fileIconView.setImageResource(R.drawable.icon_file_image);
        } else if (extension.equalsIgnoreCase("avi") || extension.equalsIgnoreCase("mp4")) {
            fileIconView.setImageResource(R.drawable.icon_file_video);
        } else if (extension.equalsIgnoreCase("mp3")) {
            fileIconView.setImageResource(R.drawable.icon_file_mp3_small);
        } else if (extension.equalsIgnoreCase("wmv")) {
            fileIconView.setImageResource(R.drawable.icon_file_wmv_small);
        } else if (extension.equalsIgnoreCase("hwp")) {
            fileIconView.setImageResource(R.drawable.icon_file_hwp_small);
        } else if (extension.equalsIgnoreCase("ppt") || extension.equalsIgnoreCase("pptx")) {
            fileIconView.setImageResource(R.drawable.icon_file_ppt_small);
        } else if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")
                || extension.equalsIgnoreCase("xlsm") || extension.equalsIgnoreCase("csv")) {
            fileIconView.setImageResource(R.drawable.icon_file_xls_small);
        } else if (extension.equalsIgnoreCase("pdf")) {
            fileIconView.setImageResource(R.drawable.icon_file_pdf_small);
        } else if (extension.equalsIgnoreCase("zip")) {
            fileIconView.setImageResource(R.drawable.icon_file_zip);
        } else {
            fileIconView.setImageResource(R.drawable.icon_file_unknown_small);
        }
    }

}
