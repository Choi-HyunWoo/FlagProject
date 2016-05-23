package com.corcow.hw.flagproject.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.manager.NetworkManager;

import java.io.File;

import is.arontibo.library.ElasticDownloadView;

/**
 * Created by multimedia on 2016-05-23.
 */
public class LoadingDialogFragment extends DialogFragment {
    public LoadingDialogFragment() {
    }

    ElasticDownloadView elasticDownloadView;
    String userName;
    String flagName;
    private static final String FILE_DOWNLOAD_PATH = "";


    // Dialog width, height setting
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
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading_dialog, container, false);

        elasticDownloadView = (ElasticDownloadView)view.findViewById(R.id.elastic_download_view);

        elasticDownloadView.startIntro();

/*
        NetworkManager.getInstance().fileDownload_BINARY(getContext(), "test", "TESTTTTT", new NetworkManager.OnFileResultListener<byte[]>() {
            @Override
            public void onSuccess(byte[] result) {
                // File
                Log.d("SUCCESS", ""+result.length);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                Log.d("PROGRESS", "bytesWritten" + bytesWritten + ", totalSize" + totalSize + " : " + (bytesWritten / totalSize) + "%");
            }

            @Override
            public void onFail(int code) {
                Log.d("FAIL", "" + code);
            }
        });
*/

        NetworkManager.getInstance().fileDownload_FILE(getContext(), "test", "TESTTTTT", new NetworkManager.OnFileResultListener<File>() {
            @Override
            public void onSuccess(File result) {
                // File
                Log.d("SUCCESS", result.getAbsolutePath());
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                Log.d("PROGRESS", "bytesWritten" + bytesWritten + ", totalSize" + totalSize + " : " + (bytesWritten / totalSize)*100 + "%");
            }

            @Override
            public void onFail(int code) {
                Log.d("FAIL", "" + code);
            }
        });

        return view;
    }
}

