package com.codeu.teamjacob.groups.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.codeu.teamjacob.groups.R;

public class LoginActivity extends Activity {

    // Creates fake items
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(getIntent().getExtras()!=null){
            Intent intent = getIntent();
            String username = intent.getStringArrayExtra(UserSignUpFragment.EXTRA_MESSAGE)[0];
            String password = intent.getStringArrayExtra(UserSignUpFragment.EXTRA_MESSAGE)[1];
            EditText txtUsername = (EditText) findViewById(R.id.txt_username);
            EditText txtPassword = (EditText) findViewById(R.id.txt_password);
            txtUsername.setText(username);
            txtPassword.setText(password);
        }

    }
}