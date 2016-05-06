package com.corcow.hw.flagproject.activity;

import com.corcow.hw.flagproject.R;

/**
 * Created by multimedia on 2016-04-29.
 */
public class FileItem {

    public static final int TYPE_DIRECTORY = 0;
    public static final int TYPE_FILE = -1;

    boolean isDirectory;

    int type;
    int iconImgResource;
    String fileName;
    String absolutePath;


    /*
    public void setIconImageResource(FileItem item) {
        switch (item.type) {
            case TYPE_JPG :
                iconImgResource = R.drawable.file;
                break;
            case TYPE_PNG :
                iconImgResource = R.drawable.file;
                break;

        }
    }
    */
}
