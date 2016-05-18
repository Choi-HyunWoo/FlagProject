package com.corcow.hw.flagproject.activity.main;

import com.corcow.hw.flagproject.util.Utilities;

/**
 * Created by multimedia on 2016-04-29.
 */
public class FileItem {

    public static final int IS_IMAGE_FILE = -1111;
    public static final int IS_VIDEO_FILE = -2222;

    String extension;           // 확장자명

    int iconImgResource;
    String fileName;
    String absolutePath;

    FileItem () {

    }

    FileItem (String fileName, String absolutePath) {
        extension = Utilities.getExtension(fileName);
        this.fileName = fileName;
        this.absolutePath = absolutePath;
    }

    // select setting
    public boolean isSelected = false;
    public void setSelectedState (boolean selectedState) {
        isSelected = selectedState;
    }

}
