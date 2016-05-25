package com.corcow.hw.flagproject.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.manager.NetworkManager;
import com.corcow.hw.flagproject.util.Utilities;

import java.io.File;

import is.arontibo.library.ElasticDownloadView;

/**
 * Created by multimedia on 2016-05-23.
 */
public class DownloadDialogFragment extends DialogFragment {

    public DownloadDialogFragment() {

    }

    ElasticDownloadView elasticDownloadView;
    TextView textNoticeView;

    String userID;
    String flagName;
    String fileName;

    long fileSize;
    int progress = 0;

    private static final String FILE_DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/FLAG";
    private static final String PARAM_KEY_USERID = "userID";
    private static final String PARAM_KEY_FLAGNAME = "flagName";
    private static final String PARAM_KEY_FILENAME = "fileName";
    private static final String PARAM_KEY_FILESIZE = "fileSize";

    public static DownloadDialogFragment newInstance(String userID, String flagName, String fileName, long fileSize) {
        DownloadDialogFragment f = new DownloadDialogFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(PARAM_KEY_USERID, userID);
        args.putString(PARAM_KEY_FLAGNAME, flagName);
        args.putString(PARAM_KEY_FILENAME, fileName);
        args.putLong(PARAM_KEY_FILESIZE, fileSize);
        f.setArguments(args);
        return f;
    }

    // Dialog width, height setting
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dlg = getDialog();
        int width = getResources().getDimensionPixelSize(R.dimen.download_dlalog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.download_dlalog_height);
        getDialog().getWindow().setLayout(width, height);
        dlg.getWindow().setLayout(width, height);
        WindowManager.LayoutParams params = dlg.getWindow().getAttributes();
        dlg.getWindow().setAttributes(params);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userID = getArguments().getString(PARAM_KEY_USERID);
        flagName = getArguments().getString(PARAM_KEY_FLAGNAME);
        fileName = getArguments().getString(PARAM_KEY_FILENAME);
        fileSize = getArguments().getLong(PARAM_KEY_FILESIZE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading_dialog, container, false);

        elasticDownloadView = (ElasticDownloadView) view.findViewById(R.id.elastic_download_view);
        textNoticeView = (TextView)view.findViewById(R.id.text_download_notice);

        handler.post(downloadRunnable);
        handler.postDelayed(startProgressRunnable, 1000);

        return view;
    }


    Handler handler = new Handler();
    Runnable startProgressRunnable = new Runnable() {
        @Override
        public void run() {
            elasticDownloadView.startIntro();
            handler.postDelayed(progressRunnable, 600);
        }
    };
    Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (progress < 100) {
                elasticDownloadView.setProgress(progress);
                handler.post(this);
            } else {
                elasticDownloadView.success();
                elasticDownloadView.onEnterAnimationFinished();
                textNoticeView.setText("다운로드 완료!");
                handler.removeCallbacks(startProgressRunnable);
                handler.removeCallbacks(progressRunnable);
            }
        }
    };
    Runnable downloadRunnable = new Runnable() {
        @Override
        public void run() {
            NetworkManager.getInstance().fileDownload(getContext(), userID, flagName, new NetworkManager.OnFileResultListener<File>() {
                @Override
                public void onSuccess(File result) {
                    // File
                    Log.d("SUCCESS", result.getAbsolutePath());
                    elasticDownloadView.success();
                    Utilities.moveDownloadFile(result.getAbsolutePath(), FILE_DOWNLOAD_PATH, fileName);
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    Log.d("PROGRESS", String.format("Progress %d from %d (%2.0f%%)", bytesWritten, fileSize, (fileSize > 0) ? (bytesWritten * 1.0 / fileSize) * 100 : -1));
                    progress = (int)((fileSize > 0) ? (bytesWritten * 1.0 / fileSize) * 100 : -1);
                }
                @Override
                public void onFail(int code) {
                    elasticDownloadView.fail();
                    Log.d("FAIL", "" + code);
                }
            });
        }
    };
}


