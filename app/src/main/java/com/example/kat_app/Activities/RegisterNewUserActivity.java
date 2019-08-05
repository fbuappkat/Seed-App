package com.example.kat_app.Activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kat_app.Models.Balance;
import com.example.kat_app.R;
import com.jaeger.library.StatusBarUtil;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONArray;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterNewUserActivity extends AppCompatActivity {

    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.btnSignUp)
    Button btnSignup;
    @BindView(R.id.signinBackground)
    ConstraintLayout signinBackground;

    private ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);

        ButterKnife.bind(this);

        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        AnimationDrawable animationDrawable = (AnimationDrawable) signinBackground.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        // Initially disable button
        btnSignup.setEnabled(false);
        btnSignup.setAlpha((float) 0.5);

        // Set up listener for username/password input
        etName.addTextChangedListener(signupAvailable);
        etUsername.addTextChangedListener(signupAvailable);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get values for user creation
                final String name = etName.getText().toString();
                final String username = etUsername.getText().toString();

                register(username, name);
            }
        });

        StatusBarUtil.setTransparent(this);
    }

    //register new Facebook user object on parse database
    private void register(String username, String name) {
        // Set core properties
        user.setUsername(username);
        JSONArray empty = new JSONArray();
        user.put("followers", empty);
        user.put("name", name);
        user.put("facebookUser", true);

        user.saveInBackground(new SaveCallback() {
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

        final Intent signup2home = new Intent(RegisterNewUserActivity.this, MainActivity.class);
        startActivity(signup2home);
        finish();
    }

    // Wait till text is entered into username/password inputs to enable sign up button
    private final TextWatcher signupAvailable = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (etName.getText().toString().trim().length() > 0 &&
                    etUsername.getText().toString().trim().length() > 0) {
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
