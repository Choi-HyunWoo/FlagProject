package com.corcow.hw.flagproject.fragment;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.activity.MainActivity;

/**
 * Created by multimedia on 2016-06-02.
 */
public class UploadCompleteDialog extends DialogFragment {

    public static final String PARAM_KEY_URL = "param_key_url";
    public static final String LABEL_COPY_URL = "label_copy_url";

    String url;

    Button okBtn, copyBtn;
    TextView urlTextView;

    public static UploadCompleteDialog newInstance(String url) {
        UploadCompleteDialog f = new UploadCompleteDialog();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(PARAM_KEY_URL, url);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString(PARAM_KEY_URL);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dlg = getDialog();
        int width = getResources().getDimensionPixelSize(R.dimen.upload_complete_dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.upload_complete_dialog_height);
        getDialog().getWindow().setLayout(width, height);
        dlg.getWindow().setLayout(width, height);
        WindowManager.LayoutParams params = dlg.getWindow().getAttributes();
        dlg.getWindow().setAttributes(params);

//        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_complete_dialog, container);

        okBtn = (Button) view.findViewById(R.id.btn_ok);
        copyBtn = (Button) view.findViewById(R.id.btn_copy);
        urlTextView = (TextView) view.findViewById(R.id.text_uploaded_url);
        urlTextView.setText(url);

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager)getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(LABEL_COPY_URL, url);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), url+" URL이 복사되었습니다.\n" +
                        "친구에게 URL로 파일을 공유하세요!", Toast.LENGTH_SHORT).show();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mUploadCompleteDialogResult.finish();
            }
        });

        return view;
    }


    // Dialog Result interface
    public interface OnUploadCompleteDialogResult {
        void finish ();
    }
    OnUploadCompleteDialogResult mUploadCompleteDialogResult; // the callback
    public void setOnUploadCompleteDialogResult(OnUploadCompleteDialogResult dialogResult){
        mUploadCompleteDialogResult = dialogResult;
    }

    @Override
    public void onDestroy() {
        mUploadCompleteDialogResult.finish();
        super.onDestroy();
    }
}
