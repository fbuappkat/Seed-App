package com.example.kat_app.Activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kat_app.Models.Balance;
import com.example.kat_app.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SignupActivity extends AppCompatActivity {

    @BindView(R.id.btnSignup)
    Button btnSignup;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etConfirmpassword)
    EditText etConfirmpassword;
    @BindView(R.id.ConstraintLayout)
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ButterKnife.bind(this);

        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get values for user creation
                final String name = etName.getText().toString();
                final String username = etUsername.getText().toString();
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();
                final String confirmPassword = etConfirmpassword.getText().toString();

                //check if password and confirmation are the same
                if (password.equals(confirmPassword)) {
                    signup(username, password, email, name);
                } else {
                    Toast.makeText(SignupActivity.this, "Passwords do not match!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    //create new user object on parse database
    private void signup(String username, String password, String email, String name) {
        final ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put("name", name);
        user.put("balance", 0);

        //signup in background, check if successful, return to home
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    //enter home
                    Log.d("LoginActivity", "Signup succesful");
                    final Intent signup2home = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(signup2home);
                    finish();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    Log.e("LoginActivity", "Sign up failure");
                    e.printStackTrace();
                }
                final Balance balance = new Balance();
                balance.setUser(user);
                balance.setAmount(0);


                balance.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                        user.put("money", balance);
                        user.saveInBackground();
                    }

                });
            }
        });
    }
}
