package com.example.kat_app.Activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kat_app.R;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.jaeger.library.StatusBarUtil;
import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;

/* FBU 2019
 * Login allows users to sign in, or sign up for a new account.
   Accounts are hosted on via Heroku. If a user is already logged in,
   they are automatically redirected to the TimelineActivity.
 */
public class LoginActivity extends AppCompatActivity {

    private final static String TAG = "LoginActivity";

    @BindView(R.id.etLoginUsername) EditText usernameInput;
    @BindView(R.id.etLoginPassword) EditText passwordInput;
    @BindView(R.id.btnLogIn) Button loginBtn;
    @BindView(R.id.tvSignUp)
    TextView tvSignUp;
    @BindView(R.id.loginBackground)
    ConstraintLayout loginBackground;
    @BindView(R.id.tvFacebookLogin)
    TextView tvFacebookLogin;

    private Boolean passwordShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        generateKeyHash();
        StatusBarUtil.setTransparent(this);
        setupParseLogIn();
        setupFacebookLogIn();
    }

    private void setupParseLogIn() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            loginToHome();
        } else {

            setContentView(R.layout.activity_login);
            ButterKnife.bind(this);
            AnimationDrawable animationDrawable = (AnimationDrawable) loginBackground.getBackground();
            animationDrawable.setEnterFadeDuration(2000);
            animationDrawable.setExitFadeDuration(4000);
            animationDrawable.start();


            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String username = usernameInput.getText().toString();
                    final String password = passwordInput.getText().toString();

                    login(username, password);
                }
            });

            // Set up listener for username/password input
            usernameInput.addTextChangedListener(loginAvailable);
            passwordInput.addTextChangedListener(loginAvailable);

            // Initially disable button
            loginBtn.setEnabled(false);
            loginBtn.setAlpha((float) 0.5);

            // Default password not shown
            passwordShown = false;

            // Set up listener for password input
            passwordInput.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_RIGHT = 2;

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (passwordInput.getRight() - passwordInput.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            if (passwordShown) {
                                passwordShown = false;
                                passwordInput.setInputType(129);
                                passwordInput.setTypeface(passwordInput.getTypeface());
                            } else {
                                passwordShown = true;
                                passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            }
                            return true;
                        }
                    }
                    return false;
                }
            });

            tvSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent loginToSignup = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(loginToSignup);
                }
            });

        }
    }

    private void setupFacebookLogIn() {
        tvFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this,
                        null, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException err) {
                                if (err != null) {
                                    Log.d(TAG, "Error occurred" + err.toString());
                                    err.printStackTrace();
                                    generateKeyHash();
                                } else if (user == null) {
                                    Log.d(TAG, "The user cancelled the Facebook login.");
                                } else {
                                    checkUserStatus(user);
                                }
                            }
                        }
                );
            }
        });
    }

    private void checkUserStatus(ParseUser user) {
        if (user.isNew()) {
            Intent register = new Intent(LoginActivity.this,
                    RegisterNewUserActivity.class);
            register.putExtra("user", Parcels.wrap(user));
            startActivity(register);
        } else {
            startActivity(new Intent(LoginActivity.this,
                    MainActivity.class));
        }
        finish();
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
               /*ParseACL parseACL = new ParseACL(ParseUser.getCurrentUser());
               parseACL.setPublicReadAccess(true);
               ParseUser.getCurrentUser().setACL(parseACL);*/
            }
        });
    }

    // Wait till text is entered into username/password inputs to enable login button
    private final TextWatcher loginAvailable = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (passwordInput.getText().toString().trim().length() > 0 &&
                    passwordInput.getText().toString().trim().length() > 0) {
                loginBtn.setEnabled(true);
                loginBtn.setBackground(getDrawable(R.drawable.btn_bg));
                loginBtn.setAlpha((float) 1);
                loginBtn.setTextColor(getColor(R.color.login_form_details));
            } else {
                loginBtn.setEnabled(false);
                loginBtn.setBackground(getDrawable(R.drawable.btn_bg));
                loginBtn.setAlpha((float) 0.5);
                loginBtn.setTextColor(getColor(R.color.login_form_details_medium));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void loginToHome() {
        final Intent loginToHome = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(loginToHome);
        finish();
    }

    private void generateKeyHash() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.katapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyHash:", e.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
