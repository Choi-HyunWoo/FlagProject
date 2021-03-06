package com.corcow.hw.flagproject.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.adapter.FileGridAdpater;
import com.corcow.hw.flagproject.manager.UserManager;
import com.corcow.hw.flagproject.model.FileGridItem;
import com.corcow.hw.flagproject.util.Utilities;

import org.askerov.dynamicgrid.DynamicGridView;

import java.io.File;

/**
 * Created by multimedia on 2016-05-17.
 */
public class FileSelectDialog extends DialogFragment {

    /*--- Click Event Handler ---*/
    // 더블클릭 시 파일 실행
    boolean isFirstClicked = false;
    int mFirstTouchedPosition = -1;
    boolean isSelected = false;
    private static final int MESSAGE_DOUBLE_TOUCH_TIMEOUT = 2;         // Handler message
    private static final int TIMEOUT_DOUBLE_TOUCH_DELAY = 1000;        // timeout delay
    // Timeout handler
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_DOUBLE_TOUCH_TIMEOUT :
                    isFirstClicked = false;
                    break;
            }
        }
    };

    // Views
    DynamicGridView fileGridView;
    TextView currentPathView;
    FileGridAdpater mAdapter;
    Button selectBtn, cancelBtn;

    // Variables
    int originalPosition = -1;           // in Edit mode
    int draggingPosition = -1;           // in Edit mode
    String rootPath;                     // SD card root storage path   ... back 키 조작 시 참조
    String currentPath;                  // current path (현재 경로)
    boolean isScrolled = false;

    // select
    int preSelectedPos = -1;
    int selectedPos = -1;

    // logged in ID
    String loggedInID;


    // Dialog Result interface
    public interface OnDialogResult {
        void finish (String name, String path);
    }
    OnDialogResult mDialogResult; // the callback
    public void setDialogResult(OnDialogResult dialogResult){
        mDialogResult = dialogResult;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dlg = getDialog();
        int width = getResources().getDimensionPixelSize(R.dimen.fileselect_dlalog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.fileselect_dlalog_height);
        getDialog().getWindow().setLayout(width, height);
        dlg.getWindow().setLayout(width, height);
        WindowManager.LayoutParams params = dlg.getWindow().getAttributes();
        dlg.getWindow().setAttributes(params);

        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_select_dialog, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // View Initialize
        selectBtn = (Button)view.findViewById(R.id.btn_select);
        cancelBtn = (Button)view.findViewById(R.id.btn_cancel);
        fileGridView = (DynamicGridView)view.findViewById(R.id.fileGridView);
        currentPathView = (TextView)view.findViewById(R.id.currentPathView);
        mAdapter = new FileGridAdpater(getActivity(), fileGridView.getNumColumns());
        fileGridView.setAdapter(mAdapter);

        // 시작은 최상위 root directory.
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        currentPath = rootPath;               // current path 를 root directory로
        showFileList(currentPath);

        fileGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFileName = ((FileGridItem)mAdapter.getItem(position)).fileName;
                String selectedFilePath = ((FileGridItem)mAdapter.getItem(position)).absolutePath;
                File selectedFile = new File(selectedFilePath);
                if (selectedFile.isDirectory()) {
                    Toast.makeText(getContext(), "폴더는 선택할 수 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    mDialogResult.finish(selectedFileName, selectedFilePath);
                    FileSelectDialog.this.dismiss();
                }
                return true;
            }
        });

        // FileGridView listeners
        fileGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 파일 선택
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
                // 첫 클릭 후 TIMEOUT 대기상태에 클릭되었다면 (첫 클릭이 있은 후 시간제한이 지나지 않았다면)
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

        // 선택 버튼
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 선택된 파일을 Parent Fragment로
                if (selectedPos != -1) {
                    String selectedFileName = ((FileGridItem)mAdapter.getItem(selectedPos)).fileName;
                    String selectedFilePath = ((FileGridItem)mAdapter.getItem(selectedPos)).absolutePath;
                    File selectedFile = new File(selectedFilePath);
                    if (selectedFile.isDirectory()) {
                        Toast.makeText(getContext(), "폴더는 선택할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        mDialogResult.finish(selectedFileName, selectedFilePath);
                        FileSelectDialog.this.dismiss();
                    }
                } else {
                    Toast.makeText(getContext(), "파일을 선택해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 파일 선택 취소 버튼
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dismiss
                FileSelectDialog.this.dismiss();
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
        Utilities.sortByFilename(files);

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
            } else if (item.extension.equalsIgnoreCase("avi") || item.extension.equalsIgnoreCase("mp4")
                    || item.extension.equalsIgnoreCase("wmv")) {
                item.iconImgResource = FileGridItem.IS_VIDEO_FILE;
            } else if (item.extension.equalsIgnoreCase("mp3")) {
                item.iconImgResource = R.drawable.icon_file_mp3_small;
            } else if (item.extension.equalsIgnoreCase("hwp")) {
                item.iconImgResource = R.drawable.icon_file_hwp_small;
            } else if (item.extension.equalsIgnoreCase("ppt") || (item.extension.equalsIgnoreCase("pptx"))) {
                item.iconImgResource = R.drawable.icon_file_ppt_small;
            } else if (item.extension.equalsIgnoreCase("xls") || item.extension.equalsIgnoreCase("xlsx")
                    || item.extension.equalsIgnoreCase("xlsm") || item.extension.equalsIgnoreCase("csv")) {
                item.iconImgResource = R.drawable.icon_file_xls_small;
            } else if (item.extension.equalsIgnoreCase("pdf")) {
                item.iconImgResource = R.drawable.icon_file_pdf_small;
            } else if (item.extension.equalsIgnoreCase("zip")) {
                item.iconImgResource = R.drawable.icon_file_zip;
            } else {
                item.iconImgResource = R.drawable.icon_file_unknown_small;
            }

            if (!f.getName().startsWith(".")) {
                mAdapter.add(item);
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getContext(), getTheme()) {
            @Override
            public void onBackPressed() {
                if (currentPath.equals(rootPath)) {
                    // 최상위 Path에서 BackKey가 눌린 경우
                    super.onBackPressed();
                } else {
                    // 하위 Path에서 BackKey가 눌린 경우
                    currentPath = new File(currentPath).getParent();        // 상위 경로로 이동
                    showFileList(currentPath);                              // 경로명 변경
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        loggedInID = UserManager.getInstance().getUserID();
    }
}
