package com.corcow.hw.flagproject.view;

import android.content.Context;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.model.json.UserFile;

/**
 * Created by multimedia on 2016-05-17.
 */
public class UserFileParentView extends FrameLayout {

    ImageView fileIconView;
    TextView flagTextView, fileNameView;

    Button flagCopyButton, deleteButton, publicButton;


    public UserFileParentView(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.user_file_parent_view, this);


    }

    public void setViewItem (UserFile item) {
        flagTextView.setText(item.fileName);
        fileNameView.setText(item.flagName);
    }

}
