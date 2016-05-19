package com.corcow.hw.flagproject.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.model.UserFileListItem;

/**
 * Created by multimedia on 2016-05-17.
 */
public class UserFileListItemView extends FrameLayout {

    TextView indexView, flagTextView, fileNameView;

    Button flagCopyButton, deleteButton;


    public UserFileListItemView(Context context) {
        super(context);
        init();
    }

    public UserFileListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        inflate(getContext(), R.layout.user_file_item_view, this);


    }

    public void setViewItem (UserFileListItem item) {

    }

}
