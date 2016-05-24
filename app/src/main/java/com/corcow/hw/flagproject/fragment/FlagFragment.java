package com.corcow.hw.flagproject.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.manager.NetworkManager;
import com.corcow.hw.flagproject.manager.UserManager;
import com.corcow.hw.flagproject.model.json.FileInfo;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class FlagFragment extends Fragment {

    /** TODO : 160524
     *  다운로드
     *  업로드 UI (Relative)
     */

    private static final String UPLOAD_MODE_PRIVATE = "private";
    private static final String UPLOAD_MODE_PUBLIC = "public";

    LinearLayout selectContainer;
    ImageView uploadView;
    FileSelectDialog dialog;
    String selectedFileName = "";
    String selectedFilePath = "";

    EditText downloadInputView;
    Button downloadBtn;

    String userID;

    public FlagFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        View view = inflater.inflate(R.layout.fragment_flag, container, false);
        userID = UserManager.getInstance().getUserID();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        downloadInputView = (EditText) getActivity().findViewById(R.id.toolbar_download_editText);
        downloadBtn = (Button) getActivity().findViewById(R.id.toolbar_download_btn);

        // View initialize
        selectContainer = (LinearLayout) view.findViewById(R.id.select_container);
        uploadView = (ImageView) view.findViewById(R.id.btn_file_upload);

        // 파일 선택 버튼
        uploadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 파일 선택 Dialog 출력
                dialog = new FileSelectDialog();
                dialog.setDialogResult(new FileSelectDialog.OnDialogResult() {
                    @Override
                    public void finish(String name, String path) {
                        selectedFileName = name;
                        selectedFilePath = path;
                        Toast.makeText(getContext(), selectedFileName, Toast.LENGTH_SHORT).show();
                        uploadView.setAlpha(120);
                    }
                });
                dialog.show(getActivity().getSupportFragmentManager(), "");
            }
        });


        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = downloadInputView.getText().toString();
                if (!TextUtils.isEmpty(input)) {
                    String userID = input.split("[/]")[0];
                    String flagName = input.split("[/]")[1];
                    NetworkManager.getInstance().fileInfo(getContext(), userID, flagName, new NetworkManager.OnResultListener<FileInfo>() {
                        @Override
                        public void onSuccess(FileInfo result) {
                            Toast.makeText(getContext(), "fileName"+result.fileName+",   fileSize"+result.fileSize, Toast.LENGTH_SHORT).show();
//                            LoadingDialogFragment dlg = new LoadingDialogFragment();
//                            dlg.show(getActivity().getSupportFragmentManager(), "");
                        }

                        @Override
                        public void onFail(int code) {
                            Toast.makeText(getContext(), "없는 사용자이거나 FLAG명이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "/ID/FLAG 를 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button test2Btn = (Button)view.findViewById(R.id.button2);

        test2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadingDialogFragment dlg = new LoadingDialogFragment();
                dlg.show(getActivity().getSupportFragmentManager(), "");
            }
        });

/**
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                elasticDownloadView.startIntro();
                handler.post(runnable);
            }
        });
 */

        return view;
    }

    /**
    int count = 0;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (count == 100) {
                elasticDownloadView.success();
                handler.removeCallbacks(runnable);
            } else {
                count++;
                Log.d("COUNT", "" + count);
                elasticDownloadView.setProgress(count);
                handler.postDelayed(this, 100);
            }
        }
    };
    */



    private void setModeInput() {
        // 파일 선택 후 정보 입력 모드

    }
    private void setModeIdle() {
        // 평소 모드

    }

    private void fileUpload(String selectedFileName, String selectedFilePath, String flagName, String publicMode) {
        // public/private를 String으로...
        NetworkManager.getInstance().fileUpload(getContext(), selectedFileName, selectedFilePath, "TESTTTTT", UPLOAD_MODE_PRIVATE, userID, new NetworkManager.OnFileResultListener<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(getContext(), "SUCCESS", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                //
            }

            @Override
            public void onFail(int code) {
                Toast.makeText(getContext(), "FAIL" + code, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
