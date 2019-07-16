package com.example.kat_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;
import com.parse.SignUpCallback;



public class SignupActivity extends AppCompatActivity {

    private Button btnSignup;
    private EditText etName;
    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //link button and edittexts to xml
        btnSignup = findViewById(R.id.btnSignup);
        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmpassword = findViewById(R.id.etConfirmpassword);


    }



    //create new user object on parse database
    private void signup(String username, String password, String email, String name) {
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        user.signUpInBackground(new SignUpCallback() {
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    //enter home
                    Log.d("LoginActivity", "Signup succesful");
//                    final Intent signup2home = new Intent(MainActivity.this, HomeActivity.class);
//                    startActivity(signup2home);
//                    finish();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    Log.e("LoginActivity", "Signup failure");
                    e.printStackTrace();
                }
            }
        });
    }
}
