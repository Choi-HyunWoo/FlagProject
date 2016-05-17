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
    private UserManager() {
    }


    public void logoutClear() {
        PropertyManager.getInstance().setAutoLogin(false);      // 자동 로그인 끄기
        this.setLoginState(false);
        this.set_id("");
        this.setUserID("");
        this.setUserPW("");
        this.setUserEmail("");
    }


    // Login state
    boolean userLoginState;

    // User info variables
    String _id;
    String userID;
    String userPW;
    String userEmail;

    public void setLoginState(boolean userLoginState) {
        this.userLoginState = userLoginState;
    }
    public boolean getLoginState() {
        return userLoginState;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
    public String get_id() {
        return _id;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getUserID() {
        return userID;
    }

    public void setUserPW(String userPW) {
        this.userPW = userPW;
    }
    public String getUserPW() {
        return userPW;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    public String getUserEmail() {
        return userEmail;
    }

}
