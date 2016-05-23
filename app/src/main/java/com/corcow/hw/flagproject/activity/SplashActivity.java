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
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.manager.NetworkManager;
import com.corcow.hw.flagproject.manager.PropertyManager;
import com.corcow.hw.flagproject.manager.UserManager;
import com.corcow.hw.flagproject.model.json.LoginResult;

public class SplashActivity extends AppCompatActivity {

    // Permission request const
    public static final int MY_PERMISSIONS_REQUEST_READWRITE_STOREAGE = 1;
    Handler mHandler = new Handler(Looper.getMainLooper());

    boolean isAutoLoginMode;

    boolean isPermissionChecked;
    boolean isAutoLoginSuccessed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkPermissionInRuntime();

        if (isPermissionChecked) {
            // 자동 로그인 상태 확인
            isAutoLoginMode = PropertyManager.getInstance().getAutoLoginMode(SplashActivity.this);

            // 자동로그인 ON
            if (isAutoLoginMode) {

                String userID = PropertyManager.getInstance().getAutoLoginId(SplashActivity.this);
                String userPW = PropertyManager.getInstance().getAutoLoginPassword(SplashActivity.this);

                NetworkManager.getInstance().signIn(SplashActivity.this, userID, userPW, new NetworkManager.OnResultListener<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult result) {
                        UserManager.getInstance().setLoginState(true);
                        UserManager.getInstance().set_id(result.user._id);
                        UserManager.getInstance().setUserID(result.user.userID);
                        UserManager.getInstance().setUserPW(result.user.userPW);
                        UserManager.getInstance().setUserEmail(result.user.email);

                        goMain();
                    }

                    @Override
                    public void onFail(int code) {
                        if (code == 500) {
                            Toast.makeText(SplashActivity.this, "연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SplashActivity.this, "로그인 실패. 아이디와 비밀번호를 확인하세요"+code, Toast.LENGTH_SHORT).show();
                        }
                        goMain();
                    }
                });
            }
            // 자동로그인 OFF
            else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goMain();
                    }
                }, 2000);
            }
        } else {
            Toast.makeText(SplashActivity.this, "권한을 허용해 주세요", Toast.LENGTH_SHORT).show();
            finish();
        }
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
            isPermissionChecked = true;
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
                    isPermissionChecked = true;
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(SplashActivity.this, "권한을 허용해 주세요", Toast.LENGTH_SHORT).show();
                    SplashActivity.this.finish();
                }
                return;
        }
        // other 'case' lines to check for other permissions this app might request
    }


    private void goMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
