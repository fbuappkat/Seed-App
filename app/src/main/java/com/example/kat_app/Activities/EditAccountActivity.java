package com.example.kat_app.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kat_app.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class EditAccountActivity extends AppCompatActivity {

    private TextView tvUpload;
    private ImageView ivProfileImage;
    private EditText etCurrName;
    private EditText etCurrUsername;
    private EditText etCurrEmail;
    private EditText etCurrLocation;
    private EditText etCurrBio;
    private Button bEditName;
    private Button bEditUsername;
    private Button bEditEmail;
    private Button bEditBio;
    private Button bSave;
    private Button bCancel;

    private static final String TAG = "EditAccountActivity";
    private static final String KEY_NAME = "name";
    private static final String KEY_PROFILE_IMAGE = "profile_image";
    private static final String KEY_BIO = "bio";

    private final static int PICK_PHOTO_CODE = 1034;
    ParseFile photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        setStatusBarColor(R.color.kat_white);
        setUploadProfileImage();
        setCancelButton();
        setTextInputs();
        setEditButtons();
        setSaveButton();
    }

    private void setUploadProfileImage() {
        // Find references for the views
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvUpload = findViewById(R.id.tvUpload);

        // Get the current user
        ParseUser currUser  = ParseUser.getCurrentUser();

        // Populate the profile picture holder
        ParseFile profileImage = currUser.getParseFile(KEY_PROFILE_IMAGE);
        if (profileImage != null) {
            Glide.with(this)
                    .load(profileImage.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }
        else {
            Glide.with(this)
                    .load(R.drawable.default_profile_image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }

        tvUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfilePic();
            }
        });
    }

    private void setStatusBarColor(int statusBarColor) {
        Window window = this.getWindow();

        // Make sure that status bar text is still visible
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(this.getResources().getColor(statusBarColor));
    }

    private void setCancelButton() {
        // Find reference for the view
        bCancel = findViewById(R.id.bCancel);

        // Set on-click listener for for image view to launch edit account activity
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void changeProfilePic() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(this.getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)  {
        if (data != null) {
            Uri photoUri = data.getData();

            ivProfileImage = findViewById(R.id.ivProfileImage);

            // by this point we have the camera photo on disk
            Bitmap chosenImage = null;
            try {
                chosenImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Load the taken image into a preview
            Glide.with(this)
                    .asBitmap()
                    .load(chosenImage)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);

            // Load the parseFile
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            chosenImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] image = stream.toByteArray();
            photoFile = new ParseFile(etCurrUsername + "profile_pic.jpg", image);
        }
    }

    private void setTextInputs() {
        // Find references for the views
        etCurrName = findViewById(R.id.etCurrName);
        etCurrUsername = findViewById(R.id.etCurrUsername);
        etCurrEmail = findViewById(R.id.etCurrEmail);
        etCurrBio = findViewById(R.id.etCurrBio);

        // Set initial text to current user's info
        ParseUser currUser = ParseUser.getCurrentUser();
        etCurrName.setText(currUser.getString(KEY_NAME));
        etCurrUsername.setText(currUser.getUsername());
        etCurrEmail.setText(currUser.getEmail());
        etCurrBio.setText(currUser.getString(KEY_BIO));

        // Initially disable the text inputs
        etCurrName.setEnabled(false);
        etCurrUsername.setEnabled(false);
        etCurrEmail.setEnabled(false);
        etCurrBio.setEnabled(false);

        // Disable the text input once enter key is pressed
        finishTextInput(etCurrName);
        finishTextInput(etCurrUsername);
        finishTextInput(etCurrEmail);
        finishTextInput(etCurrBio);
    }

    private void setSaveButton() {
        // Find reference for the view
        bSave = findViewById(R.id.bSave);

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void setEditButtons() {
        // Find references for the views
        bEditName = findViewById(R.id.bEditName);
        bEditUsername = findViewById(R.id.bEditUsername);
        bEditEmail = findViewById(R.id.bEditEmail);
        bEditBio = findViewById(R.id.bEditBio);

        // Set on click listener for edit buttons to enable respective text inputs
        bEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etCurrName.setEnabled(true);
                etCurrName.getText().clear();
                etCurrName.requestFocus();
                showKeyboard();
            }
        });

        // Set on click listener for edit buttons to enable respective text inputs
        bEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etCurrUsername.setEnabled(true);
                etCurrUsername.getText().clear();
                etCurrUsername.requestFocus();
                showKeyboard();
            }
        });

        // Set on click listener for edit buttons to enable respective text inputs
        bEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etCurrEmail.setEnabled(true);
                etCurrEmail.getText().clear();
                etCurrEmail.requestFocus();
                showKeyboard();
            }
        });

        // Set on click listener for edit buttons to enable respective text inputs
        bEditBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etCurrBio.setEnabled(true);
                etCurrBio.getText().clear();
                etCurrBio.requestFocus();
                showKeyboard();
            }
        });
    }

    private void finishTextInput(final EditText editText) {
        // Disable the text input once enter key is pressed
        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    editText.setEnabled(false);
                    return true;
                }
                return false;
            }
        });
    }

    private void saveProfile() {
        // Get the current user
        ParseUser currUser  = ParseUser.getCurrentUser();

        // Update the user's details with text from inputs
        currUser.put(KEY_NAME, etCurrName.getText().toString());
        currUser.setUsername(etCurrUsername.getText().toString());
        currUser.setEmail(etCurrEmail.getText().toString());
        currUser.put(KEY_BIO, etCurrBio.getText().toString());

        // Check if user uploaded new profile photo
        if (photoFile != null) {
            currUser.put(KEY_PROFILE_IMAGE, photoFile);
        }

        // Save details to the parse server
        currUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Details saved successfully!");
                    onBackPressed();
                } else {
                    Log.e(TAG, "Error while saving.");
                    e.printStackTrace();
                }
            }
        });
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
