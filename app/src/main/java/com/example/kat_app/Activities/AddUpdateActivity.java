package com.example.kat_app.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.azoft.carousellayoutmanager.CenterScrollListener;
import com.example.kat_app.Adapters.MediaAdapter;
import com.example.kat_app.Adapters.SpinAdapter;
import com.example.kat_app.Models.Comment;
import com.example.kat_app.Models.Project;
import com.example.kat_app.Models.Update;
import com.example.kat_app.R;
import com.facebook.login.Login;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/* AddUpdateActivity allows the user to add an update about a specific project. */
public class AddUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    private final String TAG = "Add Update Activity";

    @BindView(R.id.ivAddUpdate)
    Button btnAddUpdate;
    @BindView(R.id.etUpdateCaption)
    EditText etUpdate;
    @BindView(R.id.sProjectChoice)
    Spinner spinner;
    @BindView(R.id.ivBack)
    ImageButton ivBack;
    @BindView(R.id.rvImages)
    RecyclerView rvImages;
    @BindView(R.id.btnAddPhots)
    Button btnAddPhots;

    protected ArrayList<Project> projects = new ArrayList<>();
    protected SpinAdapter spinAdapter;
    private MediaAdapter mediaAdapter;
    private Project chosenProject;
    private String chosenProjectName;
    private ArrayList<String> imagesPathList;
    private ArrayList<ParseFile> images;
    private ArrayList<Bitmap> photos;
    private Bitmap yourbitmap;
    private Bitmap resized;
    private ProgressDialog LoadingBar;

    ContentValues values;
    Uri imageUri = null;

    public final String photoFileName = "photo.jpg";
    private ParseFile photoFile;

    private final int PICK_IMAGE_MULTIPLE =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update);

        ButterKnife.bind(this);

        MainActivity.setStatusBar(getWindow());

        btnAddPhots.setOnClickListener(this);

        LoadingBar = new ProgressDialog(this);

        images = new ArrayList<>();
        photos = new ArrayList<>();

        // create the adapter
        mediaAdapter = new MediaAdapter(this, photos, 1);
        // set the adapter on the recycler view
        final CarouselLayoutManager layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL);
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
        rvImages.setHasFixedSize(true);
        rvImages.setLayoutManager(layoutManager);
        rvImages.setAdapter(mediaAdapter);

        rvImages.setVisibility(View.GONE);

        rvImages.addOnScrollListener(new CenterScrollListener());

        setBackButton();
        setAddUpdateButton();
        setSpinner();
        getProjects();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddPhots:
                selectImage(this);
        }
    }

    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your images");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                     if (checkPermissionWRITE_EXTERNAL_STORAGE(AddUpdateActivity.this)) {
                         imageUri = getContentResolver().insert(
                                 MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                     } else {
                         return;
                     }
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public boolean checkPermissionWRITE_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(AddUpdateActivity.this, "GET PHOTO ACCESS DENIED",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
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
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        rvImages.setVisibility(View.VISIBLE);
                        Bitmap selectedImage = null;
                        try {
                            selectedImage = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(), imageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ParseFile convertedImage = conversionBitmapParseFile(selectedImage);
                        images.add(convertedImage);
                        photos.add(selectedImage);
                        mediaAdapter.notifyDataSetChanged();
                        //ImageView imageView = new ImageView(this);
                        //imageView.setImageBitmap(selectedImage);
                       // imageView.setAdjustViewBounds(true);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        rvImages.setVisibility(View.VISIBLE);
                        Uri selectedImage = data.getData();
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (bitmap != null) {
                            ParseFile convertedImage = conversionBitmapParseFile(bitmap);
                            images.add(convertedImage);
                            photos.add(bitmap);
                            mediaAdapter.notifyDataSetChanged();
                            //ImageView imageView = new ImageView(this);
                            //imageView.setImageBitmap(bitmap);
                            //imageView.setAdjustViewBounds(true);
                        }
                    }
                    break;
            }
        }
    }


    private void postUpdate(String update, ParseUser currentUser, Project project, ArrayList<ParseFile> images) {

        Update newUpdate = new Update();
        newUpdate.setCaption(update);
        newUpdate.setUser(currentUser);
        newUpdate.put("type", "Update");
        newUpdate.put("project", project);
        newUpdate.put("comments", new ArrayList<Comment>());
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
                newUpdate.setMedia(image);
            }

            newUpdate.saveInBackground(new SaveCallback() {
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
        LoadingBar.setTitle("Posting to Seed");
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
