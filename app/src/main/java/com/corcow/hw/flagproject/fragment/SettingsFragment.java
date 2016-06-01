package com.corcow.hw.flagproject.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.activity.LoginActivity;
import com.corcow.hw.flagproject.activity.MainActivity;
import com.corcow.hw.flagproject.manager.NetworkManager;
import com.corcow.hw.flagproject.manager.PropertyManager;
import com.corcow.hw.flagproject.manager.UserManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    Button loginBtn;
    ImageView userImageView;
    TextView userIdView;
    String loggedInID;

    // login state
    boolean isLogined;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        isLogined = UserManager.getInstance().getLoginState();

        // View Initialize
        userImageView = (ImageView)view.findViewById(R.id.image_userProfileImage);
        userIdView = (TextView)view.findViewById(R.id.text_userID);
        loginBtn = (Button)view.findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogined)
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                else {
                    logout();
                }
            }
        });


        setViewLogined();
        return view;
    }

    private void logout() {
        NetworkManager.getInstance().signOut(getContext(), UserManager.getInstance().getUserID(), new NetworkManager.OnResultListener<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                PropertyManager.getInstance().setAutoLoginMode(getContext(), false);        // Auto Login 해제
                UserManager.getInstance().logoutClear();        // UserManager clear
                isLogined = false;      // 변수 갱신후
                ((MainActivity)getActivity()).loggedInID = UserManager.getInstance().getUserID();
                setViewLogined();       // 뷰 갱신
            }

            @Override
            public void onFail(int code) {
                Toast.makeText(getActivity(), "연결 실패 error" + code, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        isLogined = UserManager.getInstance().getLoginState();
        loggedInID = UserManager.getInstance().getUserID();
        setViewLogined();
    }

    // 로그인 시 변화될 부분들
    // create, resume 시에 부르자..
    private void setViewLogined() {
        if (isLogined) {
            // 로그인 상태
            userImageView.setImageResource(R.drawable.icon_user_login_true);
            userIdView.setText(UserManager.getInstance().getUserID());
            loginBtn.setText("로그아웃");
        }
        else {
            // 로그아웃 상태
            userImageView.setImageResource(R.drawable.icon_user_login_false);
            userIdView.setText("로그인 해주세요");
            loginBtn.setText("로그인");
            userImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            });
        }
    }
}
