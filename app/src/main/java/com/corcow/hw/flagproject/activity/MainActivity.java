package com.corcow.hw.flagproject.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;

import org.askerov.dynamicgrid.DynamicGridView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    // Permission request const
    public static final int MY_PERMISSIONS_REQUEST_READWRITE_STOREAGE = 0;

    // Views
    DynamicGridView fileGridView;
    TextView pathView;
    FileGridAdpater mAdapter;

    // Variables
    int lastPosition;
    String rootPath;
    File rootFile;

    /** TODO . 코드 다듬기
     *
     * 1. 파일 경로 돌아가기 <<
     *
     * 2. Adapter에 여러 종류의 파일들을 multi-type item들로 관리할 수 있도록 변경
     *   폴더 / 이미지 / 문서 등등의 파일 구분.
     *
     * 3. 파일 실행  << ★★★★★       완료
     *
     * 4. 다른 화면에 대한 UI 기획
     *
     * 5. 로그인 및 회원가입
     *
     * 6. 파일 전송 및 내려받기...
     *
     * 7. 파일 드래그로 이동!!
     *
     * ----- 기능구현 완료! -----
     *
     * */

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

        // View Initialize
        fileGridView = (DynamicGridView)findViewById(R.id.fileGridView);
        pathView = (TextView)findViewById(R.id.pathView);
        mAdapter = new FileGridAdpater(MainActivity.this, 3);
        fileGridView.setAdapter(mAdapter);

        // root path, root directory, root file list 가져오기
        rootPath = Environment.getExternalStorageDirectory().getPath();
        rootFile = Environment.getExternalStorageDirectory();
        pathView.setText(rootFile.getPath());       // 현재 Path 확인
        File[] files = rootFile.listFiles();        // 현재 경로의 File 리스트 받아옴

        // add items
        for (File f : files) {
            FileItem item = new FileItem();
            item.iconImgResource = f.isDirectory() ? R.drawable.folder : R.drawable.file ;
            item.fileName = f.getName();
            item.isDirectory = f.isDirectory();
            item.absolutePath = f.getAbsolutePath();
            mAdapter.add(item);
        }

        // setting gridview callback
        fileGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedPath = ((FileItem) mAdapter.getItem(position)).absolutePath;
                File selectedFile = new File(selectedPath);
                if (selectedFile.isDirectory()) {
                    /**
                     * 이부분 코드 다듬기 필요!!
                     * */
                    // 선택된 item이 폴더인 경우
                    pathView.setText(selectedPath);     // 현재 경로명을 선택된 하위 경로로 변경
                    mAdapter.clear();
                    for (File f : selectedFile.listFiles()) {
                        FileItem item = new FileItem();
                        item.iconImgResource = f.isDirectory() ? R.drawable.folder : R.drawable.file ;
                        item.fileName = f.getName();
                        item.isDirectory = f.isDirectory();
                        item.absolutePath = f.getAbsolutePath();
                        mAdapter.add(item);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    // 선택된 item이 파일인 경우 파일 실행
                    openFile(MainActivity.this, selectedFile);

                }
            }
        });
        fileGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                fileGridView.startEditMode(position);
                return true;
            }
        });
        fileGridView.setOnDragListener(new DynamicGridView.OnDragListener() {
            @Override
            public void onDragStarted(int position) {
                // 드래그 시작시 현재 위치 저장
            }
            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {
                // newPosition의 아이템이 파일이고, newPosition에서 3초동안 머무른다면 (값이 3초동안 같다면) 폴더생성!
                Toast.makeText(MainActivity.this, "oldPosition"+oldPosition+", newPosition"+newPosition, Toast.LENGTH_SHORT).show();
            }
        });
        fileGridView.setOnDropListener(new DynamicGridView.OnDropListener() {
            @Override
            public void onActionDrop() {
                fileGridView.stopEditMode();
            }
        });

    }

    /**
     * 파일의 확장자 조회
     *
     * @param fileStr
     * @return
     */
    public static String getExtension(String fileStr) {
        return fileStr.substring(fileStr.lastIndexOf(".") + 1, fileStr.length());
    }

    /**
     * Viewer로 연결
     *
     * @param content
     * @param selectedFile
     */
    public static void openFile(Context content, File selectedFile) {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String fileExtension = getExtension(selectedFile.getName());

        String mimeType = myMime.getMimeTypeFromExtension(fileExtension);
        if (TextUtils.isEmpty(mimeType)) {
            if (fileExtension.equalsIgnoreCase("xlsx") || fileExtension.equalsIgnoreCase("xls") || fileExtension.equalsIgnoreCase("xlsm"))
                mimeType = "application/vnd.ms-excel";
            else if (fileExtension.equalsIgnoreCase("doc") || fileExtension.equalsIgnoreCase("docx"))
                mimeType = "application/msword";
            else if (fileExtension.equalsIgnoreCase("hwp"))
                mimeType = "application/hwp";
            else if (fileExtension.equalsIgnoreCase("ppt") || fileExtension.equalsIgnoreCase("pptx"))
                mimeType = "application/vnc.ms-powerpoint";
        }

        intent.setDataAndType(Uri.fromFile(selectedFile), mimeType);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            content.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(content, "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }

    /** 3. Permission 요청에 대한 응답을 Handle하는 callback 함수 override **/
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
}