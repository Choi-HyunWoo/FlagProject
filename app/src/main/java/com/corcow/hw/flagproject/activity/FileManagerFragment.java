package com.corcow.hw.flagproject.activity;


import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.util.Utilities;

import org.askerov.dynamicgrid.DynamicGridView;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class FileManagerFragment extends Fragment implements MainActivity.OnBackPressedListener {


    public FileManagerFragment() {
        // Required empty public constructor
    }

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
     * 0. Adapter에 여러 종류의 파일들을 multi-type item들로 관리할 수 있도록 변경
     *   폴더 / 이미지(thumbnail) / 문서(종류별로 아이콘..?) 등등 파일 구분 방법 찾기.
     *
     * 1. 다른 화면에 대한 UI 짜기
     *  - 툴바!!!!!!!!!!!!!!!!!!!! << 햄버거+슬라이딩메뉴 ㄱㄱ
     *  - 로그인 및 회원가입은 툴바의 햄버거 버튼과 슬라이딩 메뉴에서 < ?
     *  - 상세 페이지
     *  - 다운로드
     *
     * 2. 로그인 및 회원가입 구현 , Auto login...
     *
     * 3. 파일 전송 및 내려받기 <<< 핵심
     *
     *  <추가>
     *  1) 파일 단일 클릭 시 파일 선택.. (선택 시 Background 변경...)
     *  2) 선택 상태에서 할 일 구현
     *    - Copy & Paste
     *    - Delete
     *    - Modify..rename?
     *    - File Upload !!     << 제일중요!
     *  3) 부모 폴더로의 파일 이동은?    <<<< ***** 중요!!!
     *
     *  <ISSUE>
     *  1. 파일 이동 시 ISSUE...
     *     mMobileView의 position 인식 X..
     *     스크롤링 시 App 비정상 종료
     *  2. Permission 설정 시 ISSUE
     *     맨 처음 앱 구동시 권한 체크 전에 작업이 수행되어 앱이 꺼짐
     *     그 뒤 권한 체크를 하고 ALLOW 할 시, 앱이 정상실행..
     *     권한 체크를 어디서 할지 << ?
     * */




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_file_manager, container, false);


        // View Initialize
        fileGridView = (DynamicGridView)view.findViewById(R.id.fileGridView);
        currentPathView = (TextView)view.findViewById(R.id.currentPathView);
        mAdapter = new FileGridAdpater(getActivity(), fileGridView.getNumColumns());
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
                        Utilities.openFile(getActivity(), selectedFile);                             // 파일 실행
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
                if (draggingPosition != -1 && draggingPosition != originalPosition) {
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

        return view;
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

    // MainActivity의 OnBackPressedListener의 구현 함수 override
    @Override
    public void onBackPressed() {
        if (currentPath.equals(rootPath)) {
            // 최상위 Path에서 BackKey가 눌린 경우
            if (!isBackPressed) {
                isBackPressed = true;
                Toast.makeText(getActivity(), "뒤로가기를 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
                mHandler.sendEmptyMessageDelayed(MESSAGE_BACKKEY_TIMEOUT, TIMEOUT_BACKKEY_DELAY);
            } else {
                // TIMEOUT_BACKKEY_DELAY 안에 또 눌린경우
                mHandler.removeMessages(MESSAGE_BACKKEY_TIMEOUT);
                getActivity().finish();
            }
        } else {
            // 하위 Path에서 BackKey가 눌린 경우
            currentPath = new File(currentPath).getParent();        // 상위 경로로 이동
            showFileList(currentPath);                              // 경로명 변경
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity)context).setOnBackPressedListener(this);
    }


}
