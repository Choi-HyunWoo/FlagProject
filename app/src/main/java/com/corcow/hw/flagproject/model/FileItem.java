package com.corcow.hw.flagproject.model;

import com.corcow.hw.flagproject.util.Utilities;

/**
 * Created by multimedia on 2016-04-29.
 */
public class FileItem {

    public static final int IS_IMAGE_FILE = -1111;
    public static final int IS_VIDEO_FILE = -2222;

    public String extension;           // 확장자명

    public int iconImgResource;
    public String fileName;
    public String absolutePath;

    public FileItem () {

    }

    public FileItem (String fileName, String absolutePath) {
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
