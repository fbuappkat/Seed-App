package com.example.kat_app.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import butterknife.BindView;
import butterknife.ButterKnife;

/* AddUpdateActivity allows the user to add an update about a specific project. */
public class AddUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "Add Update Activity";

    @BindView(R.id.ivAddUpdate)
    Button btnAddUpdate;
    @BindView(R.id.etUpdateCaption)
    EditText etUpdate;
    @BindView(R.id.sProjectChoice)
    Spinner spinner;
    @BindView(R.id.ivUpdateToFeed)
    ImageView ivBack;
    @BindView(R.id.lnrImages)
    LinearLayout lnrImages;
    @BindView(R.id.btnAddPhots)
    Button btnAddPhots;

    protected ArrayList<Project> projects = new ArrayList<>();
    protected SpinAdapter spinAdapter;
    private Project chosenProject;
    private String chosenProjectName;
    private ArrayList<String> imagesPathList;
    private ArrayList<ParseFile> images;
    private Bitmap yourbitmap;
    private Bitmap resized;
    private ProgressDialog LoadingBar;

    public final String photoFileName = "photo.jpg";
    private ParseFile photoFile;

    private final int PICK_IMAGE_MULTIPLE =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update);

        ButterKnife.bind(this);

        btnAddPhots.setOnClickListener(this);

        LoadingBar = new ProgressDialog(this);

        setBackButton();
        setAddUpdateButton();
        setSpinner();
        setETUpdate();
        getProjects();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddPhots:
                Intent intent = new Intent(AddUpdateActivity.this,CustomPhotoGalleryActivity.class);
                startActivityForResult(intent,PICK_IMAGE_MULTIPLE);
                break;
        }
    }

    private void setAddUpdateButton() {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if(requestCode == PICK_IMAGE_MULTIPLE){
                imagesPathList = new ArrayList<String>();
                String[] imagesPath = data.getStringExtra("data").split("\\|");
                try{
                    lnrImages.removeAllViews();
                }catch (Throwable e){
                    e.printStackTrace();
                }
                for (int i=0;i<imagesPath.length;i++){
                    images = new ArrayList<>();
                    imagesPathList.add(imagesPath[i]);
                    yourbitmap = BitmapFactory.decodeFile(imagesPath[i]);
                    ParseFile convertedImage = conversionBitmapParseFile(yourbitmap);
                    images.add(convertedImage);
                    ImageView imageView = new ImageView(this);
                    imageView.setImageBitmap(yourbitmap);
                    imageView.setAdjustViewBounds(true);
                    lnrImages.addView(imageView);
                }
            }
        }

    }


    private void postUpdate(String update, ParseUser currentUser, Project project, ArrayList<ParseFile> images) {

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

        if (images != null || images.size() !=0) {
            for(ParseFile image : images) {
                project.setMedia(image);
            }

            project.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.d(TAG, "Error while saving");
                        e.printStackTrace();
                        return;
                    }
                    Log.d(TAG, "Success adding media!");
                }
            });
        }
    }

    private void getProjects() {
        ParseQuery<Project> projectQuery = new ParseQuery<Project>("Project");
        projectQuery.whereEqualTo("author", ParseUser.getCurrentUser());
        projectQuery.addDescendingOrder("createdAt");


        projectQuery.findInBackground(new FindCallback<Project>() {
            @Override
            public void done(List<Project> projs, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with query");
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
                    Log.e(TAG, "Error with query");
                    e.printStackTrace();
                    return;
                }
                String caption = etUpdate.getText().toString();
                ParseUser currUser = ParseUser.getCurrentUser();
                Project selected = projs.get(0);
                postUpdate(caption, currUser, selected, images);
                finish();

            }
        });
    }

    private void setBackButton() {
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

    private ArrayList<String> getProjectNames(ArrayList<Project> projs) {
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < projs.size(); i++) {
            names.add(projs.get(i).getName());
        }
        return names;
    }

    public ParseFile conversionBitmapParseFile(Bitmap imageBitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("image_file.png",imageByte);
        return parseFile;
    }
}
