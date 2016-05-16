package com.corcow.hw.flagproject.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.activity.main.MainActivity;
import com.corcow.hw.flagproject.manager.NetworkManager;
import com.corcow.hw.flagproject.manager.PropertyManager;
import com.corcow.hw.flagproject.manager.UserManager;

public class SplashActivity extends AppCompatActivity {

    Handler mHandler = new Handler(Looper.getMainLooper());
    boolean isPreviewChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 임시
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goMain();
            }
        }, 2000);


/*
        // 자동 로그인 상태 확인
        boolean autoLogin = PropertyManager.getInstance().getAutoLogin();
        // 자동로그인 ON
        if (autoLogin) {
            String id = PropertyManager.getInstance().getAutoLoginId();
            String password = PropertyManager.getInstance().getAutoLoginPassword();

            NetworkManager.getInstance().login(this, id, password, new NetworkManager.OnResultResponseListener<Login>() {
                @Override
                public void onSuccess(Login result) {
                    if (result.status.equals("ok")) {
                        // 자동 로그인 성공 >> 유저 정보 저장
                        UserManager.getInstance().setLoginState(true);
                        UserManager.getInstance().setUser_id(result.user._id);
                        if (result.user.image_ids.size() != 0) {
                            if (result.user.image_ids.get(0).equals("")) {
                                UserManager.getInstance().setUserProfileImageURL("drawable://" + R.drawable.icon_profile_default);
                            } else {
                                UserManager.getInstance().setUserProfileImageURL(result.user.image_ids.get(0).uri);
                            }
                        }
                        UserManager.getInstance().setUserEmail(result.user.email);
                        UserManager.getInstance().setUserPassword(result.user.password);
                        UserManager.getInstance().setUserNickname(result.user.nick);

                        choiceNextActivity();
                    } else {
                        // 자동 로그인 실패 >> 비회원으로 접속
                        UserManager.getInstance().setLoginState(false);
                        choiceNextActivity();
                    }
                }

                @Override
                public void onFail(int code, String responseString) {
                    // 서버와의 연결 실패 >> 로그 띄우고 비회원으로 접속
                    UserManager.getInstance().setLoginState(false);
                    Log.d("Network error/splash", "" + code);
                    choiceNextActivity();
                }
            });
        }
        // 자동로그인 OFF
        else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    choiceNextActivity();
                }
            }, 2000);
        }
*/
    }

    private void choiceNextActivity() {
        if (!isPreviewChecked) {
            goPreview();
        } else {
            goMain();
        }
    }

    private void goPreview() {
/*
        Intent intent = new Intent(SplashActivity.this, PreviewActivity.class);
        intent.putExtra(PreviewActivity.START_MODE, PreviewActivity.MODE_FIRST);
        startActivity(intent);
        finish();
*/
    }

    private void goMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
