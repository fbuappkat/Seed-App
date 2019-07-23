package com.example.kat_app.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kat_app.Adapters.ProgressAdapter;
import com.example.kat_app.Models.Project;
import com.example.kat_app.R;
import com.example.kat_app.Models.Request;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;


public class MoreDetailsActivity extends AppCompatActivity {

    private ProgressBar pbTotal;
    private TextView tvTotal;
    private RecyclerView rvReqs;
    private ArrayList<Request> requests;
    private Project project;
    protected ProgressAdapter progressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_details);



        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = (int) Math.round(dm.widthPixels * 0.9);
        int height = (int) Math.round(dm.heightPixels * 0.7);

        getWindow().setLayout(width, height);

        //link to xml
        tvTotal = findViewById(R.id.tvTotal);
        pbTotal = findViewById(R.id.pbTotal);
        rvReqs = findViewById(R.id.rvReqs);

        //set values
        project = Parcels.unwrap(getIntent().getParcelableExtra("project"));

        queryRequests();


    }

    public void setupAdapter() {
        // create the adapter
        progressAdapter = new ProgressAdapter(MoreDetailsActivity.this, requests);
        // add line between items
        rvReqs.addItemDecoration(new DividerItemDecoration(MoreDetailsActivity.this,
                DividerItemDecoration.VERTICAL));
        // set the adapter on the recycler view
        rvReqs.setAdapter(progressAdapter);
        // set the layout manager on the recycler view
        rvReqs.setLayoutManager(new LinearLayoutManager(MoreDetailsActivity.this));
    }


    protected void queryRequests() {
        ParseQuery<Request> projectQuery = new ParseQuery<Request>(Request.class);

        projectQuery.whereEqualTo("project", ParseObject.createWithoutData("Project", project.getObjectId()));
        projectQuery.addDescendingOrder("createdAt");
        projectQuery.findInBackground(new FindCallback<Request>() {
            @Override
            public void done(List<Request> posts, ParseException e) {
                if (e != null) {
                    Log.e("Query requests", "Error with query");
                    e.printStackTrace();
                    return;
                }
                requests = new ArrayList<>();
                requests.addAll(posts);
                setTotal(getTotal(requests), getTotalFunds(requests));
                setupAdapter();
            }
        });
    }

    private float getTotal(ArrayList<Request> reqs) {
        float tot = 0;
        for (int i = 0; i < reqs.size(); i++) {
            tot += reqs.get(i).getPrice();
        }
        return tot;
    }

    private float getTotalFunds(ArrayList<Request> reqs) {
        float tot = 0;
        for (int i = 0; i < reqs.size(); i++) {
            tot += reqs.get(i).getReceived();
        }
        return tot;
    }

    public void setTotal(float total, float funds) {
        int percent = Math.round(funds / total * 100);
        tvTotal.setText("Total: " + funds + "/" + total + " (" + percent + "%)");
        pbTotal.setProgress(percent);
    }


}
