package com.corcow.hw.flagproject.view;

import android.content.Context;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.util.Utilities;

/**
 * Created by multimedia on 2016-05-18.
 */
public class SendView extends FrameLayout {

    ImageView fileImageView;
    LinearLayout inputContainer;
    TextView fileNameView;
    EditText flagNameView;

    public SendView(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.send_view, this);
        fileImageView = (ImageView)findViewById(R.id.file_image);
        inputContainer = (LinearLayout)findViewById(R.id.input_container);
        fileNameView = (TextView)findViewById(R.id.file_name);
        flagNameView = (EditText)findViewById(R.id.flag_name);
    }

    public void setMesuredHeight(int parentMesuredHeight) {
        setMinimumHeight(parentMesuredHeight);
        fileImageView.setMinimumHeight(parentMesuredHeight);
        inputContainer.setMinimumHeight(parentMesuredHeight);
    }

    public void setViewItem(String fileName) {
        fileNameView.setText(fileName);
        String extension = Utilities.getExtension(fileName);
        if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")
                || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("bmp")
                || extension.equalsIgnoreCase("gif")) {
            fileImageView.setImageResource(R.drawable.icon_file_unknown_small);
        } else if (extension.equalsIgnoreCase("avi") || extension.equalsIgnoreCase("mp4")) {
            fileImageView.setImageResource(R.drawable.icon_file_unknown_small);
        } else if (extension.equalsIgnoreCase("mp3")) {
            fileImageView.setImageResource(R.drawable.icon_file_mp3_small);
        } else if (extension.equalsIgnoreCase("wmv")) {
            fileImageView.setImageResource(R.drawable.icon_file_wmv_small);
        } else if (extension.equalsIgnoreCase("hwp")) {
            fileImageView.setImageResource(R.drawable.icon_file_hwp_small);
        } else if (extension.equalsIgnoreCase("ppt") || (extension.equalsIgnoreCase("pptx"))) {
            fileImageView.setImageResource(R.drawable.icon_file_ppt_small);
        } else if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx") || extension.equalsIgnoreCase("xlsm")) {
            fileImageView.setImageResource(R.drawable.icon_file_xls_small);
        } else if (extension.equalsIgnoreCase("pdf")) {
            fileImageView.setImageResource(R.drawable.icon_file_pdf_small);
        } else {
            fileImageView.setImageResource(R.drawable.icon_file_unknown_small);
        }
    }
}
