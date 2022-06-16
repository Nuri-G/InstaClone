package com.codepath.nurivan.instaclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.nurivan.instaclone.R;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button bLogin;
    private Button bSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        bLogin = findViewById(R.id.bLogin);

        bLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            loginUser(username, password);
        });

        bSignUp = findViewById(R.id.bSignUp);

        bSignUp.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            signUpUser(username, password);
        });
    }

    private void signUpUser(String username, String password) {
        ParseUser.logOut();
        ParseUser user = new ParseUser();
        user.setPassword(password);
        user.setUsername(username);

        user.signUpInBackground(e -> {
            if (e == null) {
                Toast.makeText(LoginActivity.this, "Success!!", Toast.LENGTH_SHORT).show();
                goMainActivity();
            } else {
                Toast.makeText(LoginActivity.this, "Failed to make account.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String username, String password) {

        ParseUser.logOut();
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if(e != null) {
                Log.e(TAG, "Login issue", e);
                return;
            }


            goMainActivity();

            Toast.makeText(LoginActivity.this, "Success!!", Toast.LENGTH_SHORT).show();
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}