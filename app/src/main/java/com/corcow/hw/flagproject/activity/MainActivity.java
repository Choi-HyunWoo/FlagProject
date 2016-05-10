package com.corcow.hw.flagproject.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.util.Utilities;

import org.askerov.dynamicgrid.DynamicGridView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends AppCompatActivity {

    // Permission request const
    public static final int MY_PERMISSIONS_REQUEST_READWRITE_STOREAGE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // PERMISSION CHECK
        /** 1. 권한 확인 변수 설정 (내가 필요로 하는 permission이 이 액티비티에서 허가되었는지를 판단) **/
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        /** 2. 권한 요청 (PERMISSION_GRANTED = permission 인정;허가) **/
        // 이 App에 대해 다음 permission들이 하나라도 허가되지 않았다면,
        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            // 액티비티에서 permission들 요청
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READWRITE_STOREAGE
            );
        }

        // Fragment Build
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new FileManagerFragment()).commit();
        }
    }

    public void pushFileManagerFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new FileManagerFragment()).addToBackStack(null).commit();
    }

    public void popFileManagerFragment() {
        getSupportFragmentManager().popBackStack();
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
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
                return;
        }
        // other 'case' lines to check for other permissions this app might request
    }


    public interface OnBackPressedListener {
        public void onBackPressed();
    }
    OnBackPressedListener mOnBackPressedListener;

    public void setOnBackPressedListener (OnBackPressedListener listener) {
        mOnBackPressedListener = listener;
    }

    @Override
    public void onBackPressed() {
        if (mOnBackPressedListener != null) {
            mOnBackPressedListener.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}