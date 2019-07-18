package com.example.kat_app.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.kat_app.Adapters.InvestAdapter;
import com.example.kat_app.Project;
import com.example.kat_app.R;
import com.example.kat_app.Request;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class InvestActivity extends AppCompatActivity {

    private Button btnCancel;
    private RecyclerView rvRequests;
    private ArrayList<Request> requests;
    protected InvestAdapter investAdapter;
    private Project project;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest);


        //link xml
        btnCancel = findViewById(R.id.btnCancel);
        rvRequests = findViewById(R.id.rvRequests);

        //set values
        project = Parcels.unwrap(getIntent().getParcelableExtra("project"));


        queryRequests();

        //cancel button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setupAdapter(){
        // create the adapter
        investAdapter = new InvestAdapter(InvestActivity.this, requests);
        // add line between items
        rvRequests.addItemDecoration(new DividerItemDecoration(InvestActivity.this,
                DividerItemDecoration.VERTICAL));
        // set the adapter on the recycler view
        rvRequests.setAdapter(investAdapter);
        // set the layout manager on the recycler view
        rvRequests.setLayoutManager(new LinearLayoutManager(InvestActivity.this));
    }



    protected void queryRequests() {
        ParseQuery<Request> projectQuery = new ParseQuery<Request>(Request.class);

        projectQuery.whereEqualTo("project", ParseObject.createWithoutData("Project",project.getObjectId()));
        projectQuery.addDescendingOrder("createdAt");
        projectQuery.findInBackground(new FindCallback<Request>() {
            @Override
            public void done(List<Request> posts, ParseException e) {
                if (e != null) {
                    Log.e("Query requests","Error with query");
                    e.printStackTrace();
                    return;
                }
                requests = new ArrayList<>();
                requests.addAll(posts);
                setupAdapter();
            }
        });
    }

}
