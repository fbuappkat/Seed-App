package com.example.kat_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kat_app.Project;
import com.example.kat_app.R;
import com.example.kat_app.Request;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Hashtable;

public class CreateProjectActivity extends AppCompatActivity {

    private Button btnAdd;
    private Button btnPublish;
    private EditText etName;
    private EditText etDescription;
    public ConstraintLayout constraintLayout;

    Hashtable<String, Integer> allRequests;
    ArrayList<String> requests;
    ArrayList<Float> prices;
    ArrayList<String> requestWithPrice;
    ArrayAdapter<String> requestsAdapter;
    ListView lvRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);


        //link fileprovider
        btnAdd = findViewById(R.id.btnAdd);
        btnPublish = findViewById(R.id.btnPublish);
        constraintLayout = (ConstraintLayout) findViewById(R.id.root);
        lvRequest = findViewById(R.id.lvRequests);
        etDescription = findViewById(R.id.etDescription);
        etName = findViewById(R.id.etName);


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
                Project proj = createProject(name, description, ParseUser.getCurrentUser());
                for(int i = 0; i < requests.size(); i++){
                    createRequest(requests.get(i), prices.get(i), proj);
                }
                Intent create2main = new Intent(CreateProjectActivity.this, MainActivity.class);
                startActivity(create2main);
                finish();
            }
        });
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
                    Log.e("RequestCreate", "Failed creating project");
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
    private Project createProject(String name, String description, ParseUser user){
        final Project newProject = new Project();
        newProject.setDescription(description);
        newProject.setName(name);
        newProject.setUser(user);
        JSONArray emptyFollowers = new JSONArray();
        JSONArray emptyInvestors = new JSONArray();
        newProject.put("followers", emptyFollowers);
        newProject.put("investors", emptyInvestors);
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




}
