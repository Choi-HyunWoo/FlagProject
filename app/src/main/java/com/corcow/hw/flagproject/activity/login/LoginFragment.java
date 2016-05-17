package com.corcow.hw.flagproject.activity.login;


import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.json.LoginResult;
import com.corcow.hw.flagproject.manager.NetworkManager;
import com.corcow.hw.flagproject.manager.PropertyManager;
import com.corcow.hw.flagproject.manager.UserManager;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    Button btn;
    EditText inputIdView, inputPasswordView;
    String inputId, inputPassword;
    CheckBox autoLoginCheckView;


    public LoginFragment() {
        // Required empty public constructor
        UserManager.getInstance().setLoginState(false);
        UserManager.getInstance().set_id("");
        UserManager.getInstance().setUserID("");
        UserManager.getInstance().setUserPW("");
        UserManager.getInstance().setUserEmail("");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        inputIdView = (EditText)view.findViewById(R.id.edit_id);
        inputPasswordView = (EditText)view.findViewById(R.id.edit_password);
        autoLoginCheckView = (CheckBox)view.findViewById(R.id.checkBox_autologin);

        // 로그인
        btn = (Button)view.findViewById(R.id.btn_login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputId = inputIdView.getText().toString();
                inputPassword = inputPasswordView.getText().toString();
                if (TextUtils.isEmpty(inputId)) {
                    Toast.makeText(getContext(), "아이디를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(inputPassword)) {
                    Toast.makeText(getContext(), "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    if (autoLoginCheckView.isChecked()) {
                        PropertyManager.getInstance().setAutoLogin(true);
                        PropertyManager.getInstance().setAutoLoginId(inputId);
                        PropertyManager.getInstance().setAutoLoginPassword(inputPassword);
                    }

                    NetworkManager.getInstance().signIn(getContext(), inputId, inputPassword, new NetworkManager.OnResultListener<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult result) {
                            UserManager.getInstance().setLoginState(true);
                            UserManager.getInstance().set_id(result.user._id);
                            UserManager.getInstance().setUserID(result.user.userID);
                            UserManager.getInstance().setUserPW(result.user.userPW);
                            UserManager.getInstance().setUserEmail(result.user.email);

                            getActivity().finish();
                        }

                        @Override
                        public void onFail(int code) {
                            Toast.makeText(getActivity(), "로그인 실패. 아이디와 비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        // 회원가입
        btn = (Button) view.findViewById(R.id.btn_signup);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity)getActivity()).pushSignUpFragment();
            }
        });
        return view;
    }


}
