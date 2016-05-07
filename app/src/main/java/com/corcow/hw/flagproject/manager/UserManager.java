package com.corcow.hw.flagproject.manager;

/**
 * Created by HYUNWOO on 2016-05-08.
 */
public class UserManager {

    private static UserManager instance;
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }
    private UserManager() {}




}
