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

    // Views
    DynamicGridView fileGridView;
    TextView currentPathView;
    FileGridAdpater mAdapter;
    // Variables
    int originalPosition = -1;           // in Edit mode
    int draggingPosition = -1;           // in Edit mode
    String rootPath;                     // SD card root storage path   ... back 키 조작 시 참조
    String currentPath;                  // current path (현재 경로)

    /*--- Click Event Handler ---*/
    // BackKey 두번 누르면 종료
    boolean isBackPressed = false;
    private static final int MESSAGE_BACKKEY_TIMEOUT = 1;           // Handler message
    private static final int TIMEOUT_BACKKEY_DELAY = 2000;          // timeout delay
    // 더블클릭 시 파일 실행
    boolean isFirstClicked = false;
    private static final int MESSAGE_FILE_OPEN_TIMEOUT = 2;         // Handler message
    private static final int TIMEOUT_FILE_OPEN_DELAY = 1000;        // timeout delay
    // Timeout handler
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_BACKKEY_TIMEOUT :
                    isBackPressed = false;              // 시간이 지나도 안눌리면 false로
                    break;
                case MESSAGE_FILE_OPEN_TIMEOUT :
                    isFirstClicked = false;
                    break;
            }
        }
    };


    /** TODO .160508
     *
     * 1. 파일 경로 돌아가기 <<
     *
     * 2. Adapter에 여러 종류의 파일들을 multi-type item들로 관리할 수 있도록 변경
     *   폴더 / 이미지 / 문서 등등의 파일 구분.
     *
     * 3. 다른 화면에 대한 UI 짜기
     *
     * 4. 로그인 및 회원가입
     *
     * 5. 파일 전송 및 내려받기...
     *
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
        currentPathView = (TextView)findViewById(R.id.currentPathView);
        mAdapter = new FileGridAdpater(MainActivity.this, fileGridView.getNumColumns());
        fileGridView.setAdapter(mAdapter);

        // 시작은 최상위 root directory.
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        currentPath = rootPath;               // current path 를 root directory로
        showFileList(currentPath);

        // FileGridView listeners
        fileGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 더블클릭 시 실행 (isFileSelected로 확인)
                if (!isFirstClicked) {
                    isFirstClicked = true;
                    // Toast.makeText(MainActivity.this, "한번 더 누르면 실행됩니다.", Toast.LENGTH_SHORT).show();
                    mHandler.sendEmptyMessageDelayed(MESSAGE_FILE_OPEN_TIMEOUT, TIMEOUT_FILE_OPEN_DELAY);           // TIMEOUT_FILE_OPEN_DELAY (1초) 기다림
                } else {
                    // TIMEOUT_FILE_OPEN_DELAY 안에 또 눌린경우 (더블터치 한 경우)
                    mHandler.removeMessages(MESSAGE_FILE_OPEN_TIMEOUT);
                    isFirstClicked = false;
                    // 동작
                    String selectedPath = ((FileItem) mAdapter.getItem(position)).absolutePath;
                    File selectedFile = new File(selectedPath);
                    if (selectedFile.isDirectory()) {
                        // 선택된 item이 폴더인 경우
                        currentPath = selectedFile.getAbsolutePath();       // 경로 갱신
                        showFileList(currentPath);                          // view 갱신
                    } else {
                        // 선택된 item이 파일인 경우
                        Utilities.openFile(MainActivity.this, selectedFile);                             // 파일 실행
                    }

                }
            }
        });
        fileGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // LongClick 시 편집 모드 시작 (Drag Start)
                fileGridView.startEditMode(position);
                return true;
            }
        });
        fileGridView.setOnDragListener(new DynamicGridView.OnDragListener() {
            @Override
            public void onDragStarted(int position) {
                // Drag 시작 위치 저장
                originalPosition = position;
            }
            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {
                // Drag position이 변화 시 draggingPosition 갱신
                draggingPosition = newPosition;
            }
        });
        fileGridView.setOnDropListener(new DynamicGridView.OnDropListener() {
            @Override
            public void onActionDrop() {
                // Drop 시 draggingPosition에 grid 아이템이 존재한다면, (draggingPosition != -1)
                // Drop position의 아이템이 파일인지 폴더인지 판별.
                // 폴더라면 해당 폴더로 파일이 이동된다.  /  파일이라면 아무일 안생김.
                if (draggingPosition != -1) {
                    File droppedFile = new File(((FileItem) mAdapter.getItem(draggingPosition)).absolutePath);
                    if (droppedFile.isDirectory()) {
                        Utilities.moveFile(((FileItem) mAdapter.getItem(originalPosition)).absolutePath, droppedFile.getAbsolutePath());
                        mAdapter.delete(originalPosition);          // GridView 에서도 지워준다.
                    }
                }

                // Drop 시 Editmode는 종료, Drag를 시작한 position, Edit중인 current position을 초기화
                fileGridView.stopEditMode();
                originalPosition = -1;
                draggingPosition = -1;
            }
        });

    }

    /** showFileList() ... 인자로 받은 path에 있는 파일들을 GridView에 띄우는 함수
     * @param currentPath   :   currentPath를 갱신 후에 인자로 넣을 것.
     */
    private void showFileList(String currentPath) {
        currentPathView.setText(currentPath);        // 현재 Path 를 보여줌
        File currentDir = new File(currentPath);
        File[] files = currentDir.listFiles();       // 현재 경로의 File 리스트를 받아옴

        // add items to adapter
        mAdapter.clear();
        for (File f : files) {
            FileItem item = new FileItem(f.getName(), f.getAbsolutePath());
            item.iconImgResource = f.isDirectory() ? R.drawable.folder : R.drawable.file ;
            mAdapter.add(item);
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
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
                return;
        }
        // other 'case' lines to check for other permissions this app might request
    }


    @Override
    public void onBackPressed() {
        if (currentPath.equals(rootPath)) {
            if (!isBackPressed) {
                isBackPressed = true;
                Toast.makeText(MainActivity.this, "뒤로가기를 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
                mHandler.sendEmptyMessageDelayed(MESSAGE_BACKKEY_TIMEOUT, TIMEOUT_BACKKEY_DELAY);
            } else {
                // TIMEOUT_BACKKEY_DELAY 안에 또 눌린경우
                mHandler.removeMessages(MESSAGE_BACKKEY_TIMEOUT);
                super.onBackPressed();
            }
        } else {
            currentPath = new File(currentPath).getParent();
            showFileList(currentPath);
        }
    }
}