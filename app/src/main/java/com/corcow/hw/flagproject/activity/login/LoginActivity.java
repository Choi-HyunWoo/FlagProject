package com.corcow.hw.flagproject.activity.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.corcow.hw.flagproject.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


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
