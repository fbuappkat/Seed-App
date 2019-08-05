package com.example.kat_app.Activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kat_app.Models.Balance;
import com.example.kat_app.Models.Followers;
import com.example.kat_app.R;
import com.google.gson.JsonArray;
import com.jaeger.library.StatusBarUtil;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONArray;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SignupActivity extends AppCompatActivity {

    @BindView(R.id.btnSignUp)
    Button btnSignup;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etConfirmPassword)
    EditText etConfirmpassword;
    @BindView(R.id.btnSignIn)
    TextView btnSignIn;
    @BindView(R.id.signinBackground)
    ConstraintLayout signinBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ButterKnife.bind(this);

        AnimationDrawable animationDrawable = (AnimationDrawable) signinBackground.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        // Initially disable button
        btnSignup.setEnabled(false);
        btnSignup.setAlpha((float) 0.5);

        // Set up listener for username/password input
        etName.addTextChangedListener(signupAvailable);
        etEmail.addTextChangedListener(signupAvailable);
        etUsername.addTextChangedListener(signupAvailable);
        etPassword.addTextChangedListener(signupAvailable);
        etConfirmpassword.addTextChangedListener(signupAvailable);

        // Set up listener for sign in button
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

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


        StatusBarUtil.setTransparent(this);
    }


    //create new user object on parse database
    private void signup(String username, String password, String email, String name) {
        final ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        JSONArray empty = new JSONArray();
        user.put("followers", empty);
        user.setPassword(password);
        user.setEmail(email);
        user.put("name", name);

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

                final Followers followers = new Followers();
                JSONArray empty = new JSONArray();
                followers.setFollowers(empty);
                followers.setUser(user);
                followers.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });
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

    // Wait till text is entered into username/password inputs to enable sign up button
    private final TextWatcher signupAvailable = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (etName.getText().toString().trim().length() > 0 &&
                    etEmail.getText().toString().trim().length() > 0 &&
                    etUsername.getText().toString().trim().length() > 0 &&
                    etPassword.getText().toString().trim().length() > 0 &&
                    etConfirmpassword.getText().toString().trim().length() > 0) {
                btnSignup.setEnabled(true);
                btnSignup.setBackground(getDrawable(R.drawable.btn_bg));
                btnSignup.setAlpha((float) 1);
                btnSignup.setTextColor(getColor(R.color.login_form_details));
            } else {
                btnSignup.setEnabled(false);
                btnSignup.setBackground(getDrawable(R.drawable.btn_bg));
                btnSignup.setAlpha((float) 0.5);
                btnSignup.setTextColor(getColor(R.color.login_form_details_medium));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
