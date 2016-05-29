package com.corcow.hw.flagproject.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.util.Utilities;

/**
 * Created by multimedia on 2016-05-29.
 */
public class FolderNameInputDialog extends DialogFragment {

    public interface OnDialogResult {
        void onFinishDialog (String folderName);
    }
    OnDialogResult mDialogResult; // the callback
    public void setDialogResult(OnDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    EditText folderInputView;
    Button okBtn, cancelBtn;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dlg = getDialog();
        int width = getResources().getDimensionPixelSize(R.dimen.id_input_dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.id_input_dialog_height);
        getDialog().getWindow().setLayout(width, height);
        dlg.getWindow().setLayout(width, height);
        WindowManager.LayoutParams params = dlg.getWindow().getAttributes();
        dlg.getWindow().setAttributes(params);

        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View view = inflater.inflate(R.layout.fragment_folder_name_input_dialog, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        folderInputView = (EditText)view.findViewById(R.id.name_input_view);
        okBtn = (Button)view.findViewById(R.id.btn_ok);
        cancelBtn = (Button)view.findViewById(R.id.btn_cancel);


        folderInputView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    makeFolder();
                    return true;
                }
                return false;
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeFolder();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        return view;
    }

    private void makeFolder() {
        String folderName = folderInputView.getText().toString();
        if (Utilities.specialWordCheck(folderName)) {
            Toast.makeText(getActivity(), "폴더 이름에는 특수문자가 들어갈 수 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            mDialogResult.onFinishDialog(folderName);
            FolderNameInputDialog.this.dismiss();
        }
    }
}
