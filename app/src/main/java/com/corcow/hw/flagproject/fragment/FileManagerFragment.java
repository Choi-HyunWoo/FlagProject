package com.corcow.hw.flagproject.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.activity.MainActivity;
import com.corcow.hw.flagproject.adapter.FileGridAdpater;
import com.corcow.hw.flagproject.model.FileGridItem;
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

    // Variables in Editmode
    int originalPosition = -1;
    int draggingPosition = -1;             // Drag 동안의 위치

    // CONST
    String rootPath;                     // SD card root storage path   ... back 키 조작 시 참조
    String currentPath;                  // current path (현재 경로)

    // scrolled check
    boolean isScrolled = false;

    // Double Touch
    // First position check
    int mFirstTouchedPosition = -1;

    // selected position
    int selectedPos = -1;
    int preSelectedPos = -1;

    /*--- Click Event Handler ---*/
    // BackKey 두번 누르면 종료
    boolean isBackPressed = false;
    private static final int MESSAGE_BACKKEY_TIMEOUT = 1;           // Handler message
    private static final int TIMEOUT_BACKKEY_DELAY = 2000;          // timeout delay
    // 더블클릭 시 파일 실행
    boolean isFirstClicked = false;
    private static final int MESSAGE_DOUBLE_TOUCH_TIMEOUT = 2;         // Handler message
    private static final int TIMEOUT_DOUBLE_TOUCH_DELAY = 1000;        // timeout delay
    // Timeout handler
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_BACKKEY_TIMEOUT :
                    isBackPressed = false;              // 시간이 지나도 안눌리면 false로
                    break;
                case MESSAGE_DOUBLE_TOUCH_TIMEOUT :
                    isFirstClicked = false;
                    mFirstTouchedPosition = -1;
                    break;
            }
        }
    };

    /** TODO .160524
     *
     * 1. 선택 상태에서 할 일 구현 (툴바)
     *   - Copy & Paste
     *   - Delete
     *   - Modify..rename?
     *   - File Upload !!     << 제일중요!
     * 2. 부모 폴더로의 파일 이동    <<<< ***** 중요!!!
     *
     *  <ISSUE>
     *  1. 파일 이동 시 ISSUE...
     *     mMobileView의 position 인식 X..
     * */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_file_manager, container, false);

        // View Initialize
        fileGridView = (DynamicGridView)view.findViewById(R.id.fileGridView);
        currentPathView = (TextView)view.findViewById(R.id.currentPathView);
        mAdapter = new FileGridAdpater(getActivity(), 3);
        fileGridView.setAdapter(mAdapter);


        // 시작은 최상위 root directory.
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        currentPath = rootPath;               // current path 를 root directory로
        showFileList(currentPath);

        // FileGridView listeners
        fileGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Item select
                selectedPos = position;
                if (preSelectedPos == -1) {
                    // 첫번째 클릭
                    mAdapter.setSelectedState(true, preSelectedPos, selectedPos);
                    Log.d("FIRST TOUCH", "preSelectedPos:" + preSelectedPos + "  selectedPos:" + selectedPos);
                    preSelectedPos = selectedPos;
                } else if (preSelectedPos != selectedPos) {
                    // 다른놈 클릭
                    mAdapter.setSelectedState(true, preSelectedPos, selectedPos);
                    Log.d("OTHER TOUCH", "preSelectedPos:" + preSelectedPos + "  selectedPos:" + selectedPos);
                    preSelectedPos = selectedPos;
                } else if (preSelectedPos == selectedPos) {
                    // 같은놈 클릭
                    Log.d("SAME TOUCH", "preSelectedPos:"+preSelectedPos+"  selectedPos:"+selectedPos);
                    mAdapter.setSelectedState(false, preSelectedPos, selectedPos);
                    selectedPos = -1;
                    preSelectedPos = -1;
                }

                /** 더블클릭 구현
                 *
                 * boolean isFirstClicked  :  TIMEOUT 시간 안에 첫번째 click이 있었는지 확인하는 변수
                 * 1) isFirstClicked == false
                 *   : 아무 아이템도 클릭이 안된 상태 (첫번째 클릭 시 true로 전환)
                 * 2) isFirstClicked == true
                 *   : 특정 아이템이 클릭된 상태 (TIMEOUT을 초과할 때까지 다음 입력이 없을 시 false로 전환)
                 *
                 * int mFirstTouchedPosition  :  첫번째 click이 있었다면, 어떤 position의 item을 클릭했는지 저장하는 변수
                 * 1) mFirstTouchedPosition == -1
                 *  : 아무것도 클릭이 안되었거나 TIMEOUT이 지나 isFirstClicked가 해제되었다면 mFirstTouchedPosition을 -1로 초기화
                 * 2) mFirstTouchedPosition != -1
                 *  : FirstClick이 있던 위치(position)가 mFirstTouchedPosition에 저장. 이 값은 TIMEOUT 시간 뒤에 -1로 초기화된다.
                 *
                 *  <Algorithm>
                 *  1. 클릭 시 첫번째 클릭이라면 (isFirstClicked가 FALSE 라면),
                 *  isFirstClicked를 TRUE로 전환 , mFirstTouchedPosition에 클릭된 item position을 저장.
                 *  handler에서 TIMEOUT 시간을 세는 동안 다음 클릭이 없는 경우 isFirstClicked = FALSE, mFirstTouchedPosition = -1로 reset
                 *
                 *  2. 클릭 시 두번째 클릭이라면 (isFirstClicked 가 TRUE 라면),
                 *  첫번째 클릭된 아이템 position(mFirstTouchedPosition)과 두번째 클릭된 아이템의 position이 같은지 확인.
                 *  - 같다면 DOUBLE TOUCH이다. >> 더블클릭 시 하려고 했던 원하는 Logic 수행
                 *  - 다르다면 DOUBLE Touch가 아닌, 다른 아이템의 클릭이다. >> 핸들러 대기상태를 삭제후 방금 전 클릭된 아이템으로 첫 클릭 로직 수행
                 *
                 *
                 */

                // 첫 클릭이라면,
                if (!isFirstClicked) {
                    isFirstClicked = true;
                    mFirstTouchedPosition = position;        // TIMEOUT 뒤에 position -1로 초기화
                    mHandler.sendEmptyMessageDelayed(MESSAGE_DOUBLE_TOUCH_TIMEOUT, TIMEOUT_DOUBLE_TOUCH_DELAY);           // TIMEOUT_FILE_OPEN_DELAY (1초) 기다림
                }
                // 첫 클릭 후 TIMEOUT 대기상태라면 (첫 클릭이 있은 후 시간제한이 지나지 않았다면)
                else {
                    // 뭐가 눌렸는지 확인하자
                    // TIMEOUT_DOUBLE_TOUCH_DELAY 안에 같은 아이템이 또 눌린경우 (더블터치 된 경우)
                    if (mFirstTouchedPosition == position) {

                        // Handler 대기상태 및 CHECK 변수 초기화
                        mHandler.removeMessages(MESSAGE_DOUBLE_TOUCH_TIMEOUT);
                        isFirstClicked = false;
                        mFirstTouchedPosition = -1;

                        // 작업 수행
                        String selectedPath = ((FileGridItem) mAdapter.getItem(position)).absolutePath;
                        File selectedFile = new File(selectedPath);
                        if (selectedFile.isDirectory()) {
                            // 선택된 item이 폴더인 경우
                            currentPath = selectedFile.getAbsolutePath();       // 경로 갱신
                            showFileList(currentPath);                          // view 갱신
                        } else {
                            // 선택된 item이 파일인 경우
                            Utilities.openFile(getActivity(), selectedFile);                             // 파일 실행
                        }

                        // 다른 폴더 진입시 변수 초기화
                        originalPosition = -1;
                        draggingPosition = -1;
                        selectedPos = -1;
                        preSelectedPos = -1;
                    }

                    // TIMEOUT_DOUBLE_TOUCH_DELAY 안에 눌리긴 했지만 다른 아이템이 눌린경우
                    else {    // << mFirstTouchedPosition != position
                        // Handler 대기상태 삭제
                        mHandler.removeMessages(MESSAGE_DOUBLE_TOUCH_TIMEOUT);
                        // 방금 눌려진 아이템에 대한 클릭 대기상태 시작
                        isFirstClicked = true;
                        mFirstTouchedPosition = position;        // TIMEOUT 뒤에 position -1로 초기화
                        mHandler.sendEmptyMessageDelayed(MESSAGE_DOUBLE_TOUCH_TIMEOUT, TIMEOUT_DOUBLE_TOUCH_DELAY);           // TIMEOUT_FILE_OPEN_DELAY (1초) 기다림
                    }
                }
            }
        });
        fileGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                isScrolled = true;
                Log.d("onScrollStateChanged", "scrollState:" + scrollState);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

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
                isScrolled = false;
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
                if (!isScrolled) {
                    // 스크롤 되지 않았을 때만 파일 이동이 가능!

                    // Drop 시 draggingPosition에 grid 아이템이 존재한다면, (draggingPosition != -1)
                    // Drop position의 아이템이 파일인지 폴더인지 판별.
                    // 폴더라면 해당 폴더로 파일이 이동된다.  /  파일이라면 아무일 안생김.
                    if (draggingPosition != -1 && draggingPosition != originalPosition) {
                        File droppedFile = new File(((FileGridItem) mAdapter.getItem(draggingPosition)).absolutePath);
                        if (droppedFile.isDirectory()) {
                            Utilities.moveFile(((FileGridItem) mAdapter.getItem(originalPosition)).absolutePath, droppedFile.getAbsolutePath());
                            mAdapter.delete(originalPosition);          // GridView 에서도 지워준다.
                        }
                    }

                    // Drop 시 Editmode는 종료, Drag를 시작한 position, Edit중인 current position을 초기화
                    fileGridView.stopEditMode();
                    originalPosition = -1;
                    draggingPosition = -1;
                } else {
                    // 스크롤 되었다면 Drop 시 스크롤 변수를 False로 초기화
                    isScrolled = false;
                    fileGridView.stopEditMode();
                    originalPosition = -1;
                    draggingPosition = -1;
                }
                selectedPos = -1;
                preSelectedPos = -1;
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

        mAdapter.clear();
        if (!currentPath.equals(rootPath)) {
            FileGridItem parentItem = new FileGridItem("이전 폴더로", currentDir.getParent());
            parentItem.iconImgResource = R.drawable.icon_file_folder_parent;
            mAdapter.add(parentItem);
        }

        // add items to adapter
        for (File f : files) {
            FileGridItem item = new FileGridItem(f.getName(), f.getAbsolutePath());
            if (f.isDirectory()) {
                item.iconImgResource = R.drawable.icon_file_folder_small;
            } else if (item.extension.equalsIgnoreCase("jpg") || item.extension.equalsIgnoreCase("jpeg")
                    || item.extension.equalsIgnoreCase("png") || item.extension.equalsIgnoreCase("bmp")
                    || item.extension.equalsIgnoreCase("gif")) {
                item.iconImgResource = FileGridItem.IS_IMAGE_FILE;
            } else if (item.extension.equalsIgnoreCase("avi") || item.extension.equalsIgnoreCase("mp4")) {
                item.iconImgResource = FileGridItem.IS_VIDEO_FILE;
            } else if (item.extension.equalsIgnoreCase("mp3")) {
                item.iconImgResource = R.drawable.icon_file_mp3_small;
            } else if (item.extension.equalsIgnoreCase("wmv")) {
                item.iconImgResource = R.drawable.icon_file_wmv_small;
            } else if (item.extension.equalsIgnoreCase("hwp")) {
                item.iconImgResource = R.drawable.icon_file_hwp_small;
            } else if (item.extension.equalsIgnoreCase("ppt") || (item.extension.equalsIgnoreCase("pptx"))) {
                item.iconImgResource = R.drawable.icon_file_ppt_small;
            } else if (item.extension.equalsIgnoreCase("xls") || item.extension.equalsIgnoreCase("xlsx")
                    || item.extension.equalsIgnoreCase("xlsm")) {
                item.iconImgResource = R.drawable.icon_file_xls_small;
            } else if (item.extension.equalsIgnoreCase("pdf")) {
                item.iconImgResource = R.drawable.icon_file_pdf_small;
            } else {
                item.iconImgResource = R.drawable.icon_file_unknown_small;
            }

            if (!f.getName().startsWith(".")) {
                mAdapter.add(item);
            }
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
