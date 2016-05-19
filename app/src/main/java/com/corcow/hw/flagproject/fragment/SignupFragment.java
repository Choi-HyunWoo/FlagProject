package com.corcow.hw.flagproject.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.activity.LoginActivity;
import com.corcow.hw.flagproject.model.json.LoginResult;
import com.corcow.hw.flagproject.manager.NetworkManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment {

    // View
    EditText idView, passwordView, passwordCheckView, emailView;
    Button btn_submit, btn_cancel;

    // Variables
    String inputID, inputPassword, inputPasswordCheck, inputEmail;


    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        idView = (EditText)view.findViewById(R.id.edit_idView);
        passwordView = (EditText)view.findViewById(R.id.edit_passwordView);
        passwordCheckView = (EditText)view.findViewById(R.id.edit_passwordCheckView);
        emailView = (EditText)view.findViewById(R.id.edit_emailView);

        btn_submit = (Button)view.findViewById(R.id.btn_submit);
        btn_cancel = (Button)view.findViewById(R.id.btn_cancel);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputID = idView.getText().toString();
                inputPassword = passwordView.getText().toString();
                inputPasswordCheck = passwordCheckView.getText().toString();
                inputEmail = emailView.getText().toString();

                if (TextUtils.isEmpty(inputID)) {
                    Toast.makeText(getContext(), "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(inputPassword)) {
                    Toast.makeText(getContext(), "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(inputEmail)) {
                    Toast.makeText(getContext(), "E-mail을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else if (!idCheck(inputID)) {
                    Toast.makeText(getContext(), "아이디는 영문, 숫자를 조합한 5~12자로 만들어주세요", Toast.LENGTH_SHORT).show();
                } else if (!inputPassword.equals(inputPasswordCheck)) {
                    Toast.makeText(getContext(), "비밀번호 확인에 입력된 비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
                } else if (!emailCheck(inputEmail)) {
                    Toast.makeText(getContext(), "올바른 E-mail 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                } else {
                    NetworkManager.getInstance().signUp(getContext(), inputID, inputPassword, inputEmail, new NetworkManager.OnResultListener<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult result) {
                            Toast.makeText(getContext(), "회원가입이 완료되었습니다. 로그인하세요", Toast.LENGTH_SHORT).show();
                            ((LoginActivity)getActivity()).popSignUpFragment();
                        }

                        @Override
                        public void onFail(int code) {
                            Toast.makeText(getContext(), "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity)getActivity()).popSignUpFragment();
            }
        });



        return view;
    }

    /*
        3. 아이디 형식 체크
        String regex = "^[a-zA-Z]{1}[a-zA-Z0-9_]{4,11}$";
        예) 시작은 영문으로만, '_'를 제외한 특수문자 안되며 영문, 숫자, '_'으로만 이루어진 5 ~ 12자 이하
    */
    private boolean idCheck(String inputID) {
        return inputID.matches("^[a-zA-Z]{1}[a-zA-Z0-9_]{4,11}$");
    }
    private boolean emailCheck(String inputEmail) {
        return inputEmail.matches("^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$");
    }
}
