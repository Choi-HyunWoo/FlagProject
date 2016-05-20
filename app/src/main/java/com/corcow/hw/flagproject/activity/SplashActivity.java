package com.corcow.hw.flagproject.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.corcow.hw.flagproject.R;

public class SplashActivity extends AppCompatActivity {

    // Permission request const
    public static final int MY_PERMISSIONS_REQUEST_READWRITE_STOREAGE = 1;
    Handler mHandler = new Handler(Looper.getMainLooper());
    boolean isPreviewChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        checkPermissionInRuntime();

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

    public void checkPermissionInRuntime() {
        // PERMISSION CHECK
        /** 1. 권한 확인 변수 설정 (내가 필요로 하는 permission이 이 액티비티에서 허가되었는지를 판단) **/
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        /** 2. 권한 요청 (PERMISSION_GRANTED = permission 인정;허가) **/
        // 이 App에 대해 다음 permission들이 하나라도 허가되지 않았다면,
        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {

            //최초 인지, 재요청인지 확인
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ) {
                // 임의로 취소 시킨 경우 권한 재요청
                // 액티비티에서 permission들 요청
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READWRITE_STOREAGE);
            } else {
                //최초로 권한을 요청하는 경우
                // 액티비티에서 permission들 요청
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READWRITE_STOREAGE);
            }
        } else {
            // 권한 허용됨
            // 임시
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goMain();
                }
            }, 2000);
        }
    }

    /** 3. Permission 요청에 대한 응답을 handling 하는 callback 함수 **/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READWRITE_STOREAGE :
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            goMain();
                        }
                    }, 2000);
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    SplashActivity.this.finish();
                }
                return;
        }
        // other 'case' lines to check for other permissions this app might request
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
