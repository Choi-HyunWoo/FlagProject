package com.corcow.hw.flagproject.activity.login;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.manager.UserManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    Button btn;

    public LoginFragment() {
        // Required empty public constructor
        /*
        UserManager.getInstance().setLoginState(false);
        UserManager.getInstance().setUser_id("");
        UserManager.getInstance().setUserEmail("");
        UserManager.getInstance().setUserPassword("");
        UserManager.getInstance().setUserProfileImageURL("");
        */
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // 회원가입
        btn = (Button)view.findViewById(R.id.btn_signup);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity)getActivity()).pushSignUpFragment();
            }
        });
        return view;
    }


}
