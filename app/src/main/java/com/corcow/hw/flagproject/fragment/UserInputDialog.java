package com.corcow.hw.flagproject.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.activity.UserPageActivity;
import com.corcow.hw.flagproject.manager.NetworkManager;
import com.corcow.hw.flagproject.model.json.UserPageResult;

/**
 * Created by multimedia on 2016-05-29.
 */
public class UserInputDialog extends DialogFragment {

    EditText idInputView;
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
        View view = inflater.inflate(R.layout.fragment_user_input_dialog, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        idInputView = (EditText)view.findViewById(R.id.id_input_view);
        okBtn = (Button)view.findViewById(R.id.btn_ok);
        cancelBtn = (Button)view.findViewById(R.id.btn_cancel);


        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String inputId = idInputView.getText().toString();
                if (TextUtils.isEmpty(inputId)) {
                    Toast.makeText(getActivity(), "검색할 사용자 ID을 입력해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    NetworkManager.getInstance().userFileList(getContext(), inputId, new NetworkManager.OnResultListener<UserPageResult>() {
                        @Override
                        public void onSuccess(UserPageResult result) {
                            Intent intent = new Intent(getActivity(), UserPageActivity.class);
                            intent.putExtra(UserPageActivity.EXTRA_KEY_WHOS_PAGE, inputId);
                            startActivity(intent);
                        }

                        @Override
                        public void onFail(int code) {
                            Toast.makeText(getContext(), "없는 사용자입니다. ID를 확인해주세요!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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
}
