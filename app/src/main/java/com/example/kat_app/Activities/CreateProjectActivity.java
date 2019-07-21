package com.example.kat_app.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kat_app.Models.Project;
import com.example.kat_app.R;
import com.example.kat_app.Models.Request;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CreateProjectActivity extends AppCompatActivity {

    private Button btnAdd;
    private Button btnPublish;
    private EditText etName;
    private EditText etDescription;
    private ImageView ivThumbnailImage;
    private TextView tvUpload2;
    private Spinner spinnerCategories;
    public ConstraintLayout constraintLayout;

    ArrayList<String> requests;
    ArrayList<Float> prices;
    ArrayList<String> requestWithPrice;
    ArrayAdapter<String> requestsAdapter;
    ListView lvRequest;

    ParseFile photoFile;

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String KEY_THUMBNAIL_IMAGE = "thumbnail";
    private final static int PICK_PHOTO_CODE = 1034;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        setUploadProfileImage();

        //link fileprovider
        btnAdd = findViewById(R.id.btnAdd);
        btnPublish = findViewById(R.id.btnPublish);
        constraintLayout = (ConstraintLayout) findViewById(R.id.root);
        lvRequest = findViewById(R.id.lvRequests);
        etDescription = findViewById(R.id.etDescription);
        etName = findViewById(R.id.etName);
        spinnerCategories = findViewById(R.id.spinnerCategory);

        //setup spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(CreateProjectActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.categories));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerCategories.setAdapter(spinnerAdapter);


        //create new edit text for specific item requests
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddRequest(v);
            }
        });

        //init ArrayLists
        requests = new ArrayList<>();
        prices = new ArrayList<>();
        requestWithPrice = new ArrayList<>();

        //create and set adapter for listview of requests
        requestsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, requestWithPrice);
        lvRequest = (ListView) findViewById(R.id.lvRequests);
        lvRequest.setAdapter(requestsAdapter);

        //publish post
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String description = etDescription.getText().toString();
                String category = spinnerCategories.getSelectedItem().toString();
                Project proj = createProject(name, description, ParseUser.getCurrentUser(), category, photoFile);
                for(int i = 0; i < requests.size(); i++){
                    createRequest(requests.get(i), prices.get(i), proj);
                }
                Intent create2main = new Intent(CreateProjectActivity.this, MainActivity.class);
                startActivity(create2main);
                finish();
            }
        });
    }

    private void setUploadProfileImage() {
        // Find references for the views
        ivThumbnailImage = findViewById(R.id.ivThumbnailImage);
        tvUpload2 = findViewById(R.id.tvUpload2);

        Glide.with(this)
                    .load(R.drawable.default_thumbnail_image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivThumbnailImage);

        tvUpload2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadThumbnailPic();
            }
        });
    }

    public void uploadThumbnailPic() {
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
        if (requestCode == PICK_PHOTO_CODE) {
            if (data != null) {
                Uri photoUri = data.getData();

                ivThumbnailImage = findViewById(R.id.ivThumbnailImage);

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
                        .into(ivThumbnailImage);

                // Load the parseFile
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                chosenImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] image = stream.toByteArray();
                photoFile = new ParseFile(ParseUser.getCurrentUser().getUsername() + randomAlphaNumeric(6) + "thumbnail_pic.jpg", image);
            }
        }
    }


    //create requests that will be pointing to project it is a part of and upload to Parse
    public void createRequest(String request, Float price, Project project){
        final Request newRequest = new Request();
        newRequest.setPrice(price);
        newRequest.setRequest(request);
        newRequest.setProject(project);
        newRequest.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Log.d("RequestCreate", "create Request Success!");
                }else{
                    e.printStackTrace();
                    Log.e("RequestCreate", "Failed creating request");
                }
            }
        });
    }

    //add request for project to listview and corresponding arraylists, add info to adapter
    public void onAddRequest(View v){
        EditText etRequest = findViewById(R.id.etRequest);
        EditText etPrice = findViewById(R.id.etPrice);
        String request = etRequest.getText().toString();
        String price = etPrice.getText().toString();

        //add request and price to corresponding arraylists to make dictionary of requests later
        requests.add(request);
        prices.add(Float.parseFloat(price));
        requestsAdapter.add(request + " - $" + price);
        etRequest.setText("");
        etPrice.setText("");
        Toast.makeText(getApplicationContext(),"Request added to list!", Toast.LENGTH_SHORT).show();
    }

    //create project and upload to Parse, return project to be used in createRequest
    private Project createProject(String name, String description, ParseUser user, String category, ParseFile thumbnail){
        final Project newProject = new Project();
        newProject.setDescription(description);
        newProject.setName(name);
        newProject.setCategory(category);
        newProject.setUser(user);
        JSONArray emptyFollowers = new JSONArray();
        JSONArray emptyInvestors = new JSONArray();
        JSONArray emptyMedia = new JSONArray();
        newProject.put("followers", emptyFollowers);
        newProject.put("investors", emptyInvestors);
        newProject.put("media", emptyMedia);
        // Check if user uploaded a thumbnail photo
        if (thumbnail != null) {
            newProject.put(KEY_THUMBNAIL_IMAGE, thumbnail);
        }
        newProject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Log.d("ProjectCreate", "create Project Success!");
                    Toast.makeText(CreateProjectActivity.this, "Project created!", Toast.LENGTH_LONG).show();
                }else{
                    e.printStackTrace();
                    Log.e("ProjectCreate", "Failed creating project");
                    Toast.makeText(CreateProjectActivity.this, "Post creation Failed :(", Toast.LENGTH_LONG).show();
                }
            }
        });

        return newProject;
    }

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

}
