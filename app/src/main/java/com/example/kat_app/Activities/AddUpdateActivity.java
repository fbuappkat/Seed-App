package com.example.kat_app.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kat_app.Adapters.SpinAdapter;
import com.example.kat_app.Models.Update;
import com.example.kat_app.Project;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddUpdateActivity extends AppCompatActivity {

    private Button btnAddUpdate;
    private EditText etUpdate;
    private final String TAG = "Add Update Activity";
    private Spinner spinner;
    protected Project[] projects;
    protected SpinAdapter spinAdapter;
    private Project chosenProject;
    private ImageView ivBack;
    private ProgressDialog LoadingBar;
    private ImageView ivUpdateImage;
    public final String photoFileName = "photo.jpg";
    private ParseFile photoFile;
    Context context = AddUpdateActivity.this;
    private final static int PICK_PHOTO_CODE = 1034;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update);

        btnAddUpdate = findViewById(R.id.ivAddUpdate);
        etUpdate = findViewById(R.id.etUpdateCaption);
        getProjects();
        spinner = findViewById(R.id.sProjectChoice);
        LoadingBar = new ProgressDialog(this);
        ivUpdateImage = findViewById(R.id.ivUpdateImage);

        List<Project> projectList = new ArrayList<>();
        Project projectList1 = new Project();
        projectList1.setName("katie");
        projectList.add(projectList1);
        Project projectList2 = new Project();
        projectList2.setName("timi");
        projectList.add(projectList2);
        Project projectList3 = new Project();
        projectList3.setName("andrew");
        projectList.add(projectList3);

        ArrayAdapter<Project> adapter = new ArrayAdapter<Project>(this,
                android.R.layout.simple_spinner_item, projectList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenProject = (Project) parent.getSelectedItem();
                //displayUserData(project);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etUpdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    etUpdate.setText("");
                else
                    etUpdate.setText("What's new with your project?");
            }
        });

        btnAddUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caption = etUpdate.getText().toString();
                ParseUser currUser = ParseUser.getCurrentUser();
                /*if (photoFile == null || ivUpdateImage.getDrawable() == null) {
                    Log.e(TAG, "no photo to submit");
                    Toast.makeText(context,"No photo to display!", Toast.LENGTH_SHORT).show();
                } else {*/
                    postUpdate(caption, currUser, chosenProject.getName());
                //}
            }
        });

        etUpdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    etUpdate.setText("");
                else
                    etUpdate.setText("What's new with your project?");
            }
        });

        setBackButton();
        //setUploadUpdateImage();
    }

    private void setUploadUpdateImage() {
        // Find references for the views
        ivUpdateImage = findViewById(R.id.ivProfileImage);

        btnAddUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent for picking a photo from the gallery
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Bring up gallery to select a photo
                    startActivityForResult(intent, PICK_PHOTO_CODE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)  {
        if (data != null) {
            Uri photoUri = data.getData();

            ivUpdateImage = findViewById(R.id.ivProfileImage);

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
                    .into(ivUpdateImage);

            // Load the parseFile
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            chosenImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] image = stream.toByteArray();
            photoFile = new ParseFile(image);
        }
    }

    private void postUpdate(String update, ParseUser currentUser, String project) {
        showLoadingBar();
        Update newUpdate = new Update();
        newUpdate.setCaption(update);
        newUpdate.setUser(currentUser);
        //TODO be able to set project pointers
        //newUpdate.setProjectPointer("IGXprIUSYd");
        newUpdate.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Error while saving");
                    e.printStackTrace();
                    return;
                }
                Log.d(TAG, "Success!");
                etUpdate.setText("");
                LoadingBar.hide();
            }
        });
    }

    private void getProjects() {
        ParseQuery<Project> projectQuery = new ParseQuery<Project>("Project");
        projectQuery.addDescendingOrder("createdAt");

        projectQuery.findInBackground(new FindCallback<Project>() {
            @Override
            public void done(List<Project> projects, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error with query");
                    e.printStackTrace();
                    return;
                }
                projects.addAll(projects);
                //adapter.notifyDataSetChanged();
            }
        });
        /*for (int i = 0; i < projects.length; i++) {
            Log.d("project",projects[i].getName());
        }*/
    }

    private void setBackButton() {
        // Find reference for the view
        ivBack = findViewById(R.id.ivUpdateToFeed);

        // Set on-click listener for for image view to launch edit account activity
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void showLoadingBar() {
        LoadingBar.setTitle("Posting to KAT");
        LoadingBar.setMessage("Give us a moment! Your update will be live soon.");
        LoadingBar.setCanceledOnTouchOutside(true);
        LoadingBar.show();
    }
}
