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
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.Utilities;

import org.askerov.dynamicgrid.DynamicGridView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    // Permission request const
    public static final int MY_PERMISSIONS_REQUEST_READWRITE_STOREAGE = 0;

    // Views
    DynamicGridView fileGridView;
    TextView pathView;
    FileGridAdpater mAdapter;

    // Variables
    int originalPosition = -1;              // in Edit mode
    int currentPosition = -1;            // in Edit mode
    String rootPath;
    File rootFile;

    /** TODO . 코드 다듬기
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
     * 6. 파일 드래그로 이동!!
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
        File[] files = rootFile.listFiles();        // 현재 경로의 File 리스트를 받아옴

        // add items
        for (File f : files) {
            FileItem item = new FileItem(f.getName(), f.getAbsolutePath());
            item.iconImgResource = f.isDirectory() ? R.drawable.folder : R.drawable.file ;
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
                        FileItem item = new FileItem(f.getName(), f.getAbsolutePath());
                        item.iconImgResource = f.isDirectory() ? R.drawable.folder : R.drawable.file ;
                        mAdapter.add(item);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    // 선택된 item이 파일인 경우 파일 실행
                    openFile(selectedFile);

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
                // Drag 시작 위치 저장
                originalPosition = position;
            }
            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {
                // Drag position이 변화 시 currentposition이 갱신
                currentPosition = newPosition;
            }
        });
        fileGridView.setOnDropListener(new DynamicGridView.OnDropListener() {
            @Override
            public void onActionDrop() {
                // Drop 시 position(currentPosition)에 grid 아이템이 존재한다면,
                // Drop position의 아이템이 파일인지 폴더인지 판별
                // 폴더라면 해당 폴더로 파일이 이동된다.  /  파일이라면 아무일 안생김
                if (currentPosition != -1) {
                    File droppedFile = new File(((FileItem) mAdapter.getItem(currentPosition)).absolutePath);
                    if (droppedFile.isDirectory()) {
                        moveFile(((FileItem) mAdapter.getItem(originalPosition)).absolutePath, droppedFile.getAbsolutePath());
                        mAdapter.delete(originalPosition);          // GridView 에서도 지워준다.
                    }
                }

                // Drop 시 Editmode는 종료, Drag를 시작한 position, Edit중인 current position을 초기화
                fileGridView.stopEditMode();
                originalPosition = -1;
                currentPosition = -1;
            }
        });

    }


    /** openFile()  ... 파일 실행 함수
     * selectedFile : 실행하고자 하는 파일
     *
     * .. 안드로이드는 확장자(extension)로 파일을 구분하여 실행하지 못함.
     * .. MIME TYPE으로 구분.
     * .. 파일 확장자(extension)와 MIME TYPE이 대응되는 MimeTypeMap 이라는 MAP이 존재함.
     */
    public void openFile(File selectedFile) {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();                        // MIME TYPE & extension map
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String fileExtension = Utilities.getExtension(selectedFile.getName());

        // MimeTypeMap에서 확장자에 해당하는 MIME type을 가져옴.
        String mimeType = myMime.getMimeTypeFromExtension(fileExtension);
        // MimeTypeMap에 없는녀석은 직접 MIME을 mapping.
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
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(MainActivity.this, "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }


    /** moveFile()  ... 파일 이동 함수
     * originPath : 이동 전 파일/폴더 경로 (Absolute Path)
     * newPath : 이동 후 파일/폴더 경로 (Absolute Path)
     */
    private void moveFile(String originPath, String newPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            //create output directory if it doesn't exist
            File originFile = new File(originPath);
            if (originFile.isDirectory()) {
                // 옮기려는 파일이 폴더인 경우
                File[] originFiles = originFile.listFiles();        // 폴더 내의 파일들
                for (File f : originFiles) {                        // 순회
                    // recursive !
                    moveFile(f.getAbsolutePath(), newPath + "/" + originFile.getName());
                    originFile.delete();                            // 기존 폴더 삭제
                }
            } else {
                // 옮기려는 파일이 단일 파일인 경우
                File newFile = new File (newPath);          // 생성할 파일
                if (!newFile.exists())                      // 디렉토리가없다면
                    newFile.mkdirs();                       // 디렉토리 만들기
                in = new FileInputStream(originPath);
                out = new FileOutputStream(newPath + "/" + originFile.getName());

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;

                // write the output file
                out.flush();
                out.close();
                out = null;

                // 기존 위치의 파일 삭제
                new File(originPath).delete();
            }
        }
        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }


    private void deleteFile(String inputPath, String inputFile) {
        try {
            // delete the original file
            new File(inputPath + inputFile).delete();
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    private void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {
            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
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