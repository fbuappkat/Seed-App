package com.example.kat_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kat_app.Project;
import com.example.kat_app.R;
import com.example.kat_app.Request;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

public class ConfirmInvestActivity extends AppCompatActivity {

    private TextView tvConfirm;
    private TextView tvPrice;
    private TextView tvBalance;
    private TextView tvProject;
    private TextView tvRequest;
    private Button btnCancel;
    private Button btnConfirm;
    private Request request;
    private Project project;
    private ParseUser investUser;
    private ParseUser receiveUser;
    private float toInvest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_invest);

        //link xml
        tvConfirm = findViewById(R.id.tvConfirm);
        tvPrice = findViewById(R.id.tvPrice);
        tvBalance = findViewById(R.id.tvBalance);
        tvProject = findViewById(R.id.tvProject);
        tvRequest = findViewById(R.id.tvRequest);
        btnCancel = findViewById(R.id.btnCancel);
        btnConfirm = findViewById(R.id.btnConfirm);

        //get values
        request = Parcels.unwrap(getIntent().getParcelableExtra("request"));
        toInvest = getIntent().getFloatExtra("toInvest", 0);


        //setValues
        tvPrice.setText("Invested so far: " + request.getReceived() + "/" + request.getPrice());
        tvRequest.setText("Request: " + request.getRequest());
        tvConfirm.setText("Are you sure you want to invest $" + toInvest + "0?");
        //get user and set balance
        queryCurrentUser();
        //get project and set project name
        queryProject(request);




        //confirm and invest
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invest();
            }
        });

        //cancel
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void invest(){
        float curBalanceInvestor = Float.parseFloat(investUser.get("balance").toString());
        float curBalanceOwner = Float.parseFloat(receiveUser.get("balance").toString());
        float curRequestFunds = request.getReceived();
        if (curBalanceInvestor < toInvest){
            Toast.makeText(this, "You do not have enough in your balance to invest this amount!", Toast.LENGTH_LONG).show();
        } else {
            investUser.put("balance", curBalanceInvestor - toInvest);
            receiveUser.put("balance", curBalanceOwner + toInvest);
            request.put("received", curRequestFunds + toInvest);
            request.saveInBackground();
            investUser.saveInBackground();
            receiveUser.saveInBackground();
            Intent finished = new Intent(ConfirmInvestActivity.this, MainActivity.class);
            startActivity(finished);
            finish();
        }
    }


    //get logged in user and display balance
    protected void queryCurrentUser() {
        ParseQuery<ParseUser> projectQuery = new ParseQuery<ParseUser>(ParseUser.class);

        projectQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        projectQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> posts, ParseException e) {
                if (e != null) {
                    Log.e("Query requests","Error with query");
                    e.printStackTrace();
                    return;
                }
                investUser = posts.get(0);
                tvBalance.setText("Your current balance: $" + investUser.getNumber("balance"));

            }

        });
    }

    //get project from request pointer and set name
    protected void queryProject(Request req) {
        final ParseQuery<Project> projectQuery = new ParseQuery<Project>(Project.class);

        projectQuery.whereEqualTo("objectId", req.getProject().getObjectId());
        projectQuery.findInBackground(new FindCallback<Project>() {
            @Override
            public void done(List<Project> posts, ParseException e) {
                if (e != null) {
                    Log.e("Query requests","Error with query");
                    e.printStackTrace();
                    return;
                }
                project = posts.get(0);
                tvProject.setText("Project: " + project.getName());
                queryProjectOwner(project);
            }

        });
    }

    //get owner of project for transaction
    protected void queryProjectOwner(Project proj) {
        ParseQuery<ParseUser> projectQuery = new ParseQuery<ParseUser>(ParseUser.class);

        projectQuery.whereEqualTo("objectId", proj.getUser().getObjectId());
        projectQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> posts, ParseException e) {
                if (e != null) {
                    Log.e("Query requests","Error with query");
                    e.printStackTrace();
                    return;
                }
                receiveUser = posts.get(0);
            }

        });
    }


}
