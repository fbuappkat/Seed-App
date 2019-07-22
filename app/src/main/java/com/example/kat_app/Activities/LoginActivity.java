package com.example.kat_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kat_app.R;
import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;

/* FBU 2019
 * Login allows users to sign in, or sign up for a new account.
   Accounts are hosted on via Heroku. If a user is already logged in,
   they are automatically redirected to the TimelineActivity.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginBtn;
    private Button loginToSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            loginToHome();
        } else {
            setContentView(R.layout.activity_login);
            usernameInput = (EditText) findViewById(R.id.etLoginUsername);
            passwordInput = (EditText) findViewById(R.id.etLoginPassword);
            loginBtn = findViewById(R.id.btnLogin);
            loginToSignup = findViewById(R.id.btnLoginToSignup);

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String username = usernameInput.getText().toString();
                    final String password = passwordInput.getText().toString();

                    login(username, password);
                }
            });

            loginToSignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent loginToSignup = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(loginToSignup);
                    finish();
                }
            });
        }
    }

    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "Login successful");
                    loginToHome();
                    finish();
                } else {
                    Log.e("LoginActivity", "Login error");
                    Toast.makeText(LoginActivity.this, "Login credentials incorrect", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                ParseACL parseACL = new ParseACL(ParseUser.getCurrentUser());
                parseACL.setPublicReadAccess(true);
                ParseUser.getCurrentUser().setACL(parseACL);
            }
        });
    }

    private void loginToHome() {
        final Intent loginToHome = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(loginToHome);
        finish();
    }
}
