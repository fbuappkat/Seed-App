package com.example.kat_app.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kat_app.Adapters.SpinAdapter;
import com.example.kat_app.Models.Project;
import com.example.kat_app.Models.Update;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* AddUpdateActivity allows the user to add an update about a specific project. */
public class AddUpdateActivity extends AppCompatActivity {

    private Button btnAddUpdate;
    private EditText etUpdate;
    private TextView tvUpload3;
    private final String TAG = "Add Update Activity";
    private Spinner spinner;
    protected ArrayList<Project> projects = new ArrayList<>();
    protected SpinAdapter spinAdapter;
    private Project chosenProject;
    private String chosenProjectName;
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


        LoadingBar = new ProgressDialog(this);
        ivUpdateImage = findViewById(R.id.ivUpdateImage);

        setBackButton();
        setAddUpdateButton();
        setUploadUpdateImage();
        setSpinner();
        setETUpdate();
        getProjects();
        //setUploadUpdateImage();
    }

    private void setUploadUpdateImage() {
        // Find references for the views
        ivUpdateImage = findViewById(R.id.ivUpdateImage);
        tvUpload3 = findViewById(R.id.tvUpload3);

        tvUpload3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadUpdatePic();
            }
        });
    }

    public void uploadUpdatePic() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(this.getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    private void setAddUpdateButton() {
        btnAddUpdate = findViewById(R.id.ivAddUpdate);
        btnAddUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String caption = etUpdate.getText().toString();
                ParseUser currUser = ParseUser.getCurrentUser();
                /*if (photoFile == null || ivUpdateImage.getDrawable() == null) {
                    Log.e(TAG, "no photo to submit");
                    Toast.makeText(context,"No photo to display!", Toast.LENGTH_SHORT).show();
                } else {*/
                //}
                queryProject();
            }
        });
    }

    private void setETUpdate() {
        etUpdate = findViewById(R.id.etUpdateCaption);
        etUpdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    etUpdate.setText("");
                else
                    etUpdate.setText("What's new with your project?");
            }
        });
    }

    private void setSpinner() {
        spinner = findViewById(R.id.sProjectChoice);

        ArrayList<String> names = getProjectNames(projects);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenProjectName = parent.getSelectedItem().toString();
                //displayUserData(project);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)  {
        if (data != null) {
            Uri photoUri = data.getData();

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
                    .into(ivUpdateImage);

            // Load the parseFile
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            chosenImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] image = stream.toByteArray();
            photoFile = new ParseFile(image);
        }
    }


    private void postUpdate(String update, ParseUser currentUser, Project project, ParseFile photoFile) {

        Update newUpdate = new Update();
        newUpdate.setCaption(update);
        newUpdate.setUser(currentUser);
        newUpdate.put("project", project);
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
        projectQuery.whereEqualTo("author", ParseUser.getCurrentUser());
        projectQuery.addDescendingOrder("createdAt");


        projectQuery.findInBackground(new FindCallback<Project>() {
            @Override
            public void done(List<Project> projs, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error with query");
                    e.printStackTrace();
                    return;
                }
                projects.addAll(projs);
                setSpinner();
            }
        });
    }

    private void queryProject() {
        ParseQuery<Project> projectQuery = new ParseQuery<Project>("Project");
        projectQuery.whereEqualTo("name", chosenProjectName);


        projectQuery.findInBackground(new FindCallback<Project>() {
            @Override
            public void done(List<Project> projs, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error with query");
                    e.printStackTrace();
                    return;
                }
                String caption = etUpdate.getText().toString();
                ParseUser currUser = ParseUser.getCurrentUser();
                Project selected = projs.get(0);
                postUpdate(caption, currUser, selected, null);
                finish();

            }
        });
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

    private ArrayList<String> getProjectNames(ArrayList<Project> projs){
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < projs.size(); i++){
            names.add(projs.get(i).getName());
        }
        return names;
    }
}
