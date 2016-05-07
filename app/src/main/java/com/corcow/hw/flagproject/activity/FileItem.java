package com.corcow.hw.flagproject.activity;

import com.corcow.hw.flagproject.util.Utilities;

/**
 * Created by multimedia on 2016-04-29.
 */
public class FileItem {

    String extension;           // 확장자명

    int iconImgResource;
    String fileName;
    String absolutePath;

    FileItem (String fileName, String absolutePath) {
        extension = Utilities.getExtension(fileName);
        this.fileName = fileName;
        this.absolutePath = absolutePath;
    }

}
