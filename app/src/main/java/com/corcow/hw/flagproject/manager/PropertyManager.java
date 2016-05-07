package com.corcow.hw.flagproject.manager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.corcow.hw.flagproject.util.MyApplication;

/**
 * Created by multimedia on 2016-04-28.
 */
public class PropertyManager {
    private static PropertyManager instance;
    public static PropertyManager getInstance() {
        if (instance == null) {
            instance = new PropertyManager();
        }
        return instance;
    }

    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;           // 값을 저장할 때는 Editor

    private PropertyManager () {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        mEditor = mPrefs.edit();
    }


    /* Auto Login Checked */
    public static final String KEY_AUTO_LOGIN = "auto_login";
    public static final String KEY_AUTO_ID = "auto_id";
    public static final String KEY_AUTO_PASSWORD = "auto_password";


    // 자동 로그인 설정
    public void setAutoLogin(boolean isAutoLogin) {
        mEditor.putBoolean(KEY_AUTO_LOGIN, isAutoLogin);
        mEditor.commit();
    }
    public boolean getAutoLogin() {
        return mPrefs.getBoolean(KEY_AUTO_LOGIN, false);
    }
    public void setAutoLoginId(String id) {
        mEditor.putString(KEY_AUTO_ID, id);
        mEditor.commit();
    }
    public String getAutoLoginId() {
        return mPrefs.getString(KEY_AUTO_ID, "");
    }

    public void setAutoLoginPassword(String password) {
        mEditor.putString(KEY_AUTO_PASSWORD, password);
        mEditor.commit();
    }
    public String getAutoLoginPassword() {
        return mPrefs.getString(KEY_AUTO_PASSWORD, "");
    }




}
