package com.corcow.hw.flagproject.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.corcow.hw.flagproject.fragment.LoginFragment;
import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.fragment.SignupFragment;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        // Fragment Build
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new LoginFragment()).commit();
        }


    }

    public void pushSignUpFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignupFragment()).addToBackStack(null).commit();
    }

    public void popSignUpFragment() {
        getSupportFragmentManager().popBackStack();
    }

}
