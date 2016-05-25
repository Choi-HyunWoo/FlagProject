package com.corcow.hw.flagproject.fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.manager.NetworkManager;
import com.corcow.hw.flagproject.manager.UserManager;
import com.corcow.hw.flagproject.model.json.FileInfo;
import com.corcow.hw.flagproject.util.Utilities;

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


    // Upload idle_mode views
    LinearLayout idleContainer;
    ImageView selectStartView;
    FileSelectDialog dialog;
    // Upload selected_mode views
    LinearLayout selectedInputContainer;
    EditText selectedInputFlagView;
    Button selectedCancelBtn;
    RadioGroup selectedIsPublicView;
    Button uploadStartBtn;
    // File icon views
    LinearLayout fileIconContainer;
    ImageView selectedFileIconView;
    TextView selectedFileNameView;

    // selected file info
    String selectedFileName = "";
    String selectedFilePath = "";


    // Download views in Toolbar
    EditText downloadInputView;
    Button downloadBtn;


    // Current user ID (signed in)
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

        // Current user ID
        userID = UserManager.getInstance().getUserID();

        // Download views in Toolbar initialize
        downloadInputView = (EditText) getActivity().findViewById(R.id.toolbar_download_editText);
        downloadBtn = (Button) getActivity().findViewById(R.id.toolbar_download_btn);

        // Upload views initialize
        // idle mode
        idleContainer = (LinearLayout) view.findViewById(R.id.idle_container);
        selectStartView = (ImageView) view.findViewById(R.id.btn_file_upload);

        // selected mode
        selectedInputContainer = (LinearLayout) view.findViewById(R.id.selected_input_container);
        selectedInputFlagView = (EditText) view.findViewById(R.id.selected_flagInputView);
        selectedIsPublicView = (RadioGroup) view.findViewById(R.id.selected_isPublicView);
        selectedCancelBtn = (Button) view.findViewById(R.id.selected_cancelBtn);
        uploadStartBtn = (Button) view.findViewById(R.id.upload_startBtn);

        // selected file icon
        selectedFileIconView = (ImageView) view.findViewById(R.id.selected_fileIconView);
        fileIconContainer = (LinearLayout) view.findViewById(R.id.file_icon_container);
        selectedFileNameView = (TextView) view.findViewById(R.id.selected_fileNameView);


        // when onCreateView, set Idle mode
        idleContainer.setVisibility(View.VISIBLE);
        selectedInputContainer.setVisibility(View.GONE);
        fileIconContainer.setVisibility(View.GONE);

        /** 파일 선택 */
        // 파일 선택 버튼
        selectStartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 파일 선택 Dialog 출력
                dialog = new FileSelectDialog();
                dialog.setDialogResult(new FileSelectDialog.OnDialogResult() {
                    @Override
                    public void finish(String name, String path) {
                        selectedFileName = name;
                        selectedFilePath = path;
                        setFileIcon();
                        setModeSelected();
                    }
                });
                dialog.show(getActivity().getSupportFragmentManager(), "");
            }
        });

        /** 파일 선택 후
         *  FLAG/공개여부 입력  >>  파일 업로드
         */
        // 선택 취소
        selectedCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setModeIdle();
            }
        });
        // 업로드 버튼 클릭
        uploadStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animationUploadAway = AnimationUtils.loadAnimation(getContext(), R.anim.upload_go_away);
                fileIconContainer.setAnimation(animationUploadAway);
            }
        });

        /** 파일 다운로드
         * ID/FLAG 입력  >>  버튼 클릭  >>  파일 다운로드
         */
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = downloadInputView.getText().toString();
                if (!TextUtils.isEmpty(input)) {
                    final String userID = input.split("[/]")[0];
                    final String flagName = input.split("[/]")[1];
                    NetworkManager.getInstance().fileInfo(getContext(), userID, flagName, new NetworkManager.OnResultListener<FileInfo>() {
                        @Override
                        public void onSuccess(FileInfo result) {
                            DownloadDialogFragment dlg = DownloadDialogFragment.newInstance(userID, flagName, result.fileName, result.fileSize);
                            dlg.show(getActivity().getSupportFragmentManager(), "");
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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        selectedFileName = "";
        selectedFilePath = "";
        idleContainer.setVisibility(View.VISIBLE);
        selectedInputContainer.setVisibility(View.GONE);
        fileIconContainer.setVisibility(View.GONE);
    }

    private void setModeSelected() {
        // 파일 선택 후 정보 입력 모드
        idleContainer.setVisibility(View.GONE);
        selectedInputContainer.setVisibility(View.VISIBLE);
        fileIconContainer.setVisibility(View.VISIBLE);
        Animation animLeftToCenter = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left_to_center);
        Animation animAlphaAppear = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_appear);
        selectedInputContainer.setAnimation(animLeftToCenter);
        fileIconContainer.setAnimation(animAlphaAppear);
    }

    private void setModeIdle() {
        // 평소 모드
        Animation animAlphaAppear = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_appear);
        idleContainer.setAnimation(animAlphaAppear);
        idleContainer.setVisibility(View.VISIBLE);
        selectedInputFlagView.setFocusable(false);
        Animation animCenterToRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_center_to_right);
        selectedInputContainer.setAnimation(animCenterToRight);
        Animation animAlphaDisappear = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_disappear);
        fileIconContainer.setAnimation(animAlphaDisappear);
        mHandler.postDelayed(animWaitRunnable, 500);
    }

    Handler mHandler = new Handler();
    Runnable animWaitRunnable = new Runnable() {
        @Override
        public void run() {
            selectedInputContainer.setVisibility(View.GONE);
            fileIconContainer.setVisibility(View.GONE);
            selectedInputFlagView.setFocusable(true);
            selectedInputFlagView.setText("");
            mHandler.removeCallbacks(this);
        }
    };

    private void setFileIcon() {
        if (!TextUtils.isEmpty(selectedFileName)) {
            selectedFileNameView.setText(selectedFileName);
            String extension;
            File f = new File(selectedFilePath);
            if (f.isDirectory()) {
                selectedFileIconView.setImageResource(R.drawable.icon_file_folder_small);
            } else {
                extension = Utilities.getExtension(selectedFileName);
                if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")
                        || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("bmp")
                        || extension.equalsIgnoreCase("gif")) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(selectedFilePath);
                    selectedFileIconView.setImageBitmap(myBitmap);
                } else if (extension.equalsIgnoreCase("avi") || extension.equalsIgnoreCase("mp4")) {
                    Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(selectedFilePath, MediaStore.Video.Thumbnails.MICRO_KIND);
                    selectedFileIconView.setImageBitmap(bmThumbnail);
                } else if (extension.equalsIgnoreCase("mp3")) {
                    selectedFileIconView.setImageResource(R.drawable.icon_file_mp3_small);
                } else if (extension.equalsIgnoreCase("wmv")) {
                    selectedFileIconView.setImageResource(R.drawable.icon_file_wmv_small);
                } else if (extension.equalsIgnoreCase("hwp")) {
                    selectedFileIconView.setImageResource(R.drawable.icon_file_hwp_small);
                } else if (extension.equalsIgnoreCase("ppt") || (extension.equalsIgnoreCase("pptx"))) {
                    selectedFileIconView.setImageResource(R.drawable.icon_file_ppt_small);
                } else if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")
                        || extension.equalsIgnoreCase("xlsm") || extension.equalsIgnoreCase("csv")) {
                    selectedFileIconView.setImageResource(R.drawable.icon_file_xls_small);
                } else if (extension.equalsIgnoreCase("pdf")) {
                    selectedFileIconView.setImageResource(R.drawable.icon_file_pdf_small);
                } else {
                    selectedFileIconView.setImageResource(R.drawable.icon_file_unknown_small);
                }
            }
        } else {
            Toast.makeText(getContext(), "파일을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            selectedFileIconView.setImageResource(R.mipmap.ic_launcher);
            selectedFileNameView.setText("");
            setModeIdle();
        }
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

