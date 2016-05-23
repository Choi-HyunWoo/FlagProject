package com.corcow.hw.flagproject.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.manager.NetworkManager;
import com.corcow.hw.flagproject.manager.UserManager;

import is.arontibo.library.ElasticDownloadView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FlagFragment extends Fragment {

    /** TODO : 160511
     *
     *  툴바추가
     *  다운로드
     *  업로드 UI (Relative)
     *
     *
     *
     */

    private static final String UPLOAD_MODE_PRIVATE = "private";
    private static final String UPLOAD_MODE_PUBLIC = "public";

    LinearLayout selectContainer;
    ImageView uploadView;
    FileSelectDialog dialog;
    String selectedFileName = "";
    String selectedFilePath = "";

    String userID;

    public FlagFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_flag, container, false);
        userID = UserManager.getInstance().getUserID();

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

        Button testBtn = (Button)view.findViewById(R.id.button);
        Button test2Btn = (Button)view.findViewById(R.id.button2);
        elasticDownloadView = (ElasticDownloadView)view.findViewById(R.id.elastic_download_view);

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                elasticDownloadView.startIntro();
                handler.post(runnable);
            }
        });
        test2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadingDialogFragment dlg = new LoadingDialogFragment();
                dlg.show(getActivity().getSupportFragmentManager(), "");
            }
        });

        return view;
    }

    ElasticDownloadView elasticDownloadView;
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

    private void setModeInput() {
        // 파일 선택 후 정보 입력 모드

    }
    private void setModeIdle() {
        // 평소 모드

    }

    private void fileUpload(String selectedFileName, String selectedFilePath, String flagName, String publicMode) {
        // public/private를 String으로...
        NetworkManager.getInstance().fileUpload(getContext(), selectedFileName, selectedFilePath, "TESTTTTT", UPLOAD_MODE_PRIVATE, userID, new NetworkManager.OnResultListener<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(getContext(), "SUCCESS", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(int code) {
                Toast.makeText(getContext(), "FAIL" + code, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
