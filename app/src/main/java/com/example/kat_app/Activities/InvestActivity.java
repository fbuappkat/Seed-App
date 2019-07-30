package com.example.kat_app.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.kat_app.Adapters.InvestAdapter;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class InvestActivity extends AppCompatActivity {
    @BindView(R.id.rvRequests)
    RecyclerView rvRequests;
    @BindView(R.id.ivBack)
    ImageView ivBack;

    private ArrayList<Request> requests;
    protected InvestAdapter investAdapter;
    private Project project;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest);


        ButterKnife.bind(this);
        setBackButton();

        //set values
        project = Parcels.unwrap(getIntent().getParcelableExtra("project"));


        queryRequests();
    }

    public void setupAdapter() {
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
                setupAdapter();
            }
        });
    }


    //calculate total funds needed to complete project
    /*private void calculateTotalFunds() {
        for (int i = 0; i < requests.size(); i++) {
            totalFundsNeeded += (requests.get(i).getPrice() - requests.get(i).getReceived());
        }
    }*/

    private void setBackButton() {
        // Set on-click listener for for image view to launch edit account activity
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    //get total funds requested from project
    private float getTotal(ArrayList<Request> reqs) {
        float tot = 0;
        for (int i = 0; i < reqs.size(); i++) {
            tot += reqs.get(i).getPrice();
        }
        return tot;
    }

    //get funds received
    private float getTotalFunds(ArrayList<Request> reqs) {
        float tot = 0;
        for (int i = 0; i < reqs.size(); i++) {
            tot += reqs.get(i).getReceived();
        }
        return tot;
    }

}
