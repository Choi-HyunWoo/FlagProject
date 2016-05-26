package com.corcow.hw.flagproject.fragment;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
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
import com.corcow.hw.flagproject.activity.LoginActivity;
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
    RadioGroup selectedIsPublicRadio;
    Button uploadStartBtn;
    // File icon views
    LinearLayout fileIconContainer;
    ImageView selectedFileIconView;
    TextView selectedFileNameView;

    // selected file info
    String selectedFileName = "";
    String selectedFilePath = "";
    String inputFlagName = "";
    String inputisPublic = "";
    long selectedFileSize = 0L;

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
        selectedIsPublicRadio = (RadioGroup) view.findViewById(R.id.selected_isPublicRadio);
        selectedCancelBtn = (Button) view.findViewById(R.id.selected_cancelBtn);
        uploadStartBtn = (Button) view.findViewById(R.id.upload_startBtn);

        // selected file icon
        selectedFileIconView = (ImageView) view.findViewById(R.id.selected_fileIconView);
        fileIconContainer = (LinearLayout) view.findViewById(R.id.file_icon_container);
        selectedFileNameView = (TextView) view.findViewById(R.id.selected_fileNameView);


        // when onCreateView, set Idle mode
        idleContainer.setVisibility(View.VISIBLE);
        selectedInputContainer.setVisibility(View.INVISIBLE);
        fileIconContainer.setVisibility(View.INVISIBLE);

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
                        setSelectedAnimation();
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
                setIdleAnimation();
            }
        });
        // 업로드 버튼 클릭
        uploadStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputFlagName = selectedInputFlagView.getText().toString();
                if (TextUtils.isEmpty(userID)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setIcon(R.drawable.app_logo);
                    builder.setTitle("로그인");
                    builder.setMessage("파일 업로드는 회원만 가능합니다\n로그인 페이지로 이동하시겠습니까?");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog dlg = builder.create();
                    dlg.show();
                } else {
                    if (!TextUtils.isEmpty(inputFlagName)) {
                        startUpload();
                    } else {
                        Toast.makeText(getContext(), "파일 별명을 지어주세요!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        selectedIsPublicRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_btn_public:
                        inputisPublic = UPLOAD_MODE_PUBLIC;
                        break;
                    case R.id.radio_btn_private:
                        inputisPublic= UPLOAD_MODE_PRIVATE;
                        break;
                    default:
                        break;
                }
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
        userID = UserManager.getInstance().getUserID();
        idleContainer.setVisibility(View.VISIBLE);
        selectedInputContainer.setVisibility(View.INVISIBLE);
        fileIconContainer.setVisibility(View.INVISIBLE);
        resetVariables();
    }

    private void setSelectedAnimation() {
        // 파일 선택 후 정보 입력 모드
        idleContainer.setVisibility(View.INVISIBLE);
        selectedInputContainer.setVisibility(View.VISIBLE);
        fileIconContainer.setVisibility(View.VISIBLE);
        animateWobble(selectedFileIconView);
        Animation animLeftToCenter = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left_to_center);
        Animation animAlphaAppear = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_appear);
        selectedInputContainer.setAnimation(animLeftToCenter);
        fileIconContainer.setAnimation(animAlphaAppear);
        selectedInputFlagView.setText("");
    }

    private void setIdleAnimation() {
        // 평소 모드
        stopWobble();
        Animation animAlphaAppear = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_appear);
        idleContainer.setAnimation(animAlphaAppear);
        idleContainer.setVisibility(View.VISIBLE);
        Animation animCenterToRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_center_to_right);
        selectedInputContainer.setAnimation(animCenterToRight);
        Animation animAlphaDisappear = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_disappear);
        fileIconContainer.setAnimation(animAlphaDisappear);
        mHandler.postDelayed(inputCloseRunnable, 500);
        resetVariables();
    }
    Handler mHandler = new Handler();
    Runnable inputCloseRunnable = new Runnable() {
        @Override
        public void run() {
            selectedInputContainer.setVisibility(View.INVISIBLE);
            fileIconContainer.setVisibility(View.INVISIBLE);
            selectedInputFlagView.setText("");
            mHandler.removeCallbacks(this);
        }
    };
    /** 실제 파일 전송
     *
     */
    private void startUpload() {
        selectedInputContainer.setVisibility(View.INVISIBLE);
        File f = new File(selectedFilePath);
        selectedFileSize = f.length();
        // 반드시 업로드 실패시 VISIBLE, 성공시 IDLE_MODE 로 전환할 것
        Animation animationUploadAway = AnimationUtils.loadAnimation(getContext(), R.anim.upload_go_away);
        fileIconContainer.setAnimation(animationUploadAway);
        mHandler.postDelayed(uploadRunnable, 500);
    }
    Runnable uploadRunnable = new Runnable() {
        @Override
        public void run() {
            fileIconContainer.setVisibility(View.INVISIBLE);
            NetworkManager.getInstance().fileUpload(getContext(), selectedFileName, selectedFilePath, inputFlagName, inputisPublic, userID, new NetworkManager.OnFileResultListener<String>() {
                @Override
                public void onSuccess(String result) {
                    Toast.makeText(getContext(), "전송이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    Animation animAlphaAppear = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_appear);
                    idleContainer.setAnimation(animAlphaAppear);
                    stopWobble();
                    idleContainer.setVisibility(View.VISIBLE);
                    selectedInputContainer.setVisibility(View.INVISIBLE);
                    fileIconContainer.setVisibility(View.INVISIBLE);
                    mHandler.removeCallbacks(uploadRunnable);
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    Log.d("PROGRESS", String.format("Progress %d from %d (%2.0f%%)", bytesWritten, selectedFileSize, (selectedFileSize > 0) ? (bytesWritten * 1.0 / selectedFileSize) * 100 : -1));
                }

                @Override
                public void onFail(int code) {
                    // setFailAnimation()
                    Toast.makeText(getContext(), "전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    fileIconContainer.setVisibility(View.VISIBLE);
                    selectedInputContainer.setVisibility(View.VISIBLE);
                    Animation animationUploadFailDrop = AnimationUtils.loadAnimation(getContext(), R.anim.upload_fail_drop);
                    Animation animAlphaAppear = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_appear);
                    fileIconContainer.setAnimation(animationUploadFailDrop);
                    selectedInputContainer.setAnimation(animAlphaAppear);
                    mHandler.removeCallbacks(uploadRunnable);
                }
            });
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
            setSelectedAnimation();
        }
    }

    private void resetVariables() {
        selectedFileName = "";
        selectedFilePath = "";
        inputFlagName = "";
        inputisPublic = "";
        selectedFileSize = 0L;
    }




    ObjectAnimator wobbleAnimator;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void animateWobble(View v) {
        wobbleAnimator = createBaseWobble(v);
        wobbleAnimator.setFloatValues(-2, 2);
        wobbleAnimator.start();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void stopWobble() {
        wobbleAnimator.cancel();
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private ObjectAnimator createBaseWobble(final View v) {
        ObjectAnimator animator = new ObjectAnimator();
        animator.setDuration(180);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setPropertyName("rotation");
        animator.setTarget(v);
        return animator;
    }
}

