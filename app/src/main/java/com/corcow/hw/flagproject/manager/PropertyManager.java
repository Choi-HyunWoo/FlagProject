package com.corcow.hw.flagproject.manager;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
    }


    /* Auto Login Checked */
    public static final String KEY_AUTO_LOGIN = "auto_login";
    public static final String KEY_AUTO_ID = "auto_id";
    public static final String KEY_AUTO_PASSWORD = "auto_password";


    // 자동 로그인 설정
    public void setAutoLoginMode(Context context, boolean isAutoLogin) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mPrefs.edit();
        mEditor.putBoolean(KEY_AUTO_LOGIN, isAutoLogin);
        mEditor.commit();
    }
    public boolean getAutoLoginMode(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return mPrefs.getBoolean(KEY_AUTO_LOGIN, false);
    }
    public void setAutoLoginId(Context context, String id) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mPrefs.edit();
        mEditor.putString(KEY_AUTO_ID, id);
        mEditor.commit();
    }
    public String getAutoLoginId(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return mPrefs.getString(KEY_AUTO_ID, "");
    }

    public void setAutoLoginPassword(Context context, String password) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mPrefs.edit();
        mEditor.putString(KEY_AUTO_PASSWORD, password);
        mEditor.commit();
    }
    public String getAutoLoginPassword(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return mPrefs.getString(KEY_AUTO_PASSWORD, "");
    }




}
