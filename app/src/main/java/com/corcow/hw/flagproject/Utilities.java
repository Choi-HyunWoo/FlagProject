package com.corcow.hw.flagproject;

/**
 * Created by HYUNWOO on 2016-05-06.
 */
public class Utilities {



    public static String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

}
