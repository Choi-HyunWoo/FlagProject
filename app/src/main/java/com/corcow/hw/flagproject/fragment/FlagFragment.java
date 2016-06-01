package com.corcow.hw.flagproject.fragment;


import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.activity.LoginActivity;
import com.corcow.hw.flagproject.activity.MainActivity;
import com.corcow.hw.flagproject.activity.UserPageActivity;
import com.corcow.hw.flagproject.manager.NetworkManager;
import com.corcow.hw.flagproject.manager.UserManager;
import com.corcow.hw.flagproject.model.json.FileInfo;
import com.corcow.hw.flagproject.model.json.UserPageResult;
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
    RadioButton publicBtn;
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
    ImageView downloadBtn;


    // Current user ID (signed in)
    String loggedInID;
    String input;

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
        loggedInID = UserManager.getInstance().getUserID();

        // Download views in Toolbar initialize
        downloadInputView = (EditText) getActivity().findViewById(R.id.toolbar_download_editText);
        downloadBtn = (ImageView) getActivity().findViewById(R.id.toolbar_download_btn);
//        startBtnBlingAnimation();
        downloadBtn.setBackgroundResource(R.drawable.background_round_corner);

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
        publicBtn = (RadioButton) view.findViewById(R.id.radio_btn_public);
        publicBtn.setChecked(true);
        inputisPublic = UPLOAD_MODE_PUBLIC;

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
                ((MainActivity)getActivity()).fabMenu.close(true);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isAcceptingText()) {
                    hideKeyboard();Log.d("KEYBOARD", "보임");
                } else {
                    Log.d("KEYBOARD", "안보임");
                }
                dialog = new FileSelectDialog();
                dialog.setDialogResult(new FileSelectDialog.OnDialogResult() {
                    @Override
                    public void finish(String name, String path) {
                        ((MainActivity)getActivity()).fabMenu.close(true);
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
                ((MainActivity)getActivity()).fabMenu.close(true);
                inputFlagName = selectedInputFlagView.getText().toString();
                if (TextUtils.isEmpty(loggedInID)) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    Toast.makeText(getContext(), "업로드 시 로그인이 필요합니다 로그인 해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    if (TextUtils.isEmpty(inputFlagName)) {
                        Toast.makeText(getContext(), "업로드할 파일의 별명을 지어주세요!", Toast.LENGTH_SHORT).show();
                    } else if (Utilities.specialWordCheck(inputFlagName)) {
                        Toast.makeText(getContext(), "파일의 별명에 특수문자를 붙일 수 없습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm.isAcceptingText()) {
                            hideKeyboard();
                            Log.d("KEYBOARD", "보임");
                        } else {
                            Log.d("KEYBOARD", "안보임");
                        }
                        startUpload();
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
                startDownload();
            }
        });

        // 툴바 엔터 입력 시 이동
        downloadInputView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    startDownload();
                    return true;
                }
                return false;
            }
        });



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loggedInID = UserManager.getInstance().getUserID();
        input = "";

        idleContainer.setVisibility(View.VISIBLE);
        selectedInputContainer.setVisibility(View.INVISIBLE);
        fileIconContainer.setVisibility(View.INVISIBLE);
        resetVariables();
    }

    private void startDownload() {
        ((MainActivity)getActivity()).fabMenu.close(true);
        input = downloadInputView.getText().toString();
        if (!TextUtils.isEmpty(input)) {
            if(input.contains("/") && !input.endsWith("/")) {
                final String userID = input.split("[/]")[0];
                final String flagName = input.split("[/]")[1];
                NetworkManager.getInstance().fileInfo(getContext(), userID, flagName, new NetworkManager.OnResultListener<FileInfo>() {
                    @Override
                    public void onSuccess(FileInfo result) {
                        DownloadDialog dlg = DownloadDialog.newInstance(userID, flagName, result.fileName, result.fileSize);
                        dlg.show(getActivity().getSupportFragmentManager(), "");
                        downloadInputView.setText("");
                        input="";
                    }
                    @Override
                    public void onFail(int code) {
                        Toast.makeText(getContext(), "없는 사용자이거나 FLAG명이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                // 유저 페이지 실행
                NetworkManager.getInstance().userFileList(getContext(), input, new NetworkManager.OnResultListener<UserPageResult>() {
                    @Override
                    public void onSuccess(UserPageResult result) {
                        Intent intent = new Intent(getActivity(), UserPageActivity.class);
                        intent.putExtra(UserPageActivity.EXTRA_KEY_WHOS_PAGE, input);
                        startActivity(intent);
                        downloadInputView.setText("");
                        input="";
                    }
                    @Override
                    public void onFail(int code) {
                        Toast.makeText(getContext(), "없는 사용자입니다. ID를 확인해주세요!", Toast.LENGTH_SHORT).show();
                    }
                });
                hideKeyboard();
            }
        } else {
            Toast.makeText(getContext(), "ID 혹은 ID/FLAG 를 입력하세요", Toast.LENGTH_SHORT).show();
        }
    }


    private void setSelectedAnimation() {
        ((MainActivity)getActivity()).fabMenu.close(true);
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
        ((MainActivity)getActivity()).fabMenu.close(true);
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
            final ProgressDialog dlg = new ProgressDialog(getContext());
            dlg.setMessage("파일을 전송합니다.");
            dlg.setCancelable(false);
            dlg.show();

            NetworkManager.getInstance().fileUpload(getContext(), selectedFileName, selectedFilePath, inputFlagName, inputisPublic, loggedInID, new NetworkManager.OnFileResultListener<String>() {
                @Override
                public void onSuccess(String result) {
                    dlg.dismiss();
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
                    dlg.setProgress((int)((selectedFileSize > 0) ? (bytesWritten * 1.0 / selectedFileSize) * 100 : -1));
                }

                @Override
                public void onFail(int code) {
                    dlg.dismiss();
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
        inputisPublic = UPLOAD_MODE_PUBLIC;
        selectedFileSize = 0L;
    }

    ValueAnimator animator;
    private void startBtnBlingAnimation() {
        animator = (ValueAnimator) AnimatorInflater.loadAnimator(getContext(), R.animator.text_color_greentowhite);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (Integer)animation.getAnimatedValue();
                downloadBtn.setBackgroundColor(color);
            }
        });
        animator.start();
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

    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}

