package com.example.kat_app.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kat_app.Models.Balance;
import com.example.kat_app.Models.Project;
import com.example.kat_app.Models.Request;
import com.example.kat_app.Models.Transaction;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
    private ParseUser investUser = ParseUser.getCurrentUser();
    private ParseUser receiveUser;
    private Balance recieverBalance;
    private Balance investorBalance;
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
        queryUserBalance(ParseUser.getCurrentUser());

        //get project and set project name
        queryProject(request);

        //confirm and invest
        btnConfirm.setBackgroundColor(Color.parseColor("#77EE77"));
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invest();
            }
        });

        //cancel
        btnCancel.setBackgroundColor(Color.parseColor("#E43B3B"));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //invest funds
    public void invest(){
        //get current balance and request funds received so far
        float curBalanceInvestor = Float.parseFloat(investorBalance.getAmount().toString());
        float curBalanceReceiver = Float.parseFloat(recieverBalance.getAmount().toString());
        float curRequestFunds = request.getReceived();
        //check if investor has enough funds
        if (curBalanceInvestor < toInvest){
            Toast.makeText(this, "You do not have enough in your balance to invest this amount!", Toast.LENGTH_LONG).show();
        } else {
            //change balance and put into request
            investorBalance.put("amount", curBalanceInvestor - toInvest);
            recieverBalance.put("amount", curBalanceReceiver + toInvest);
            request.put("received", curRequestFunds + toInvest);
            //save values
            request.saveInBackground();
            investorBalance.saveInBackground();
            recieverBalance.saveInBackground();
            //create transaction for history
            createTransaction(toInvest, investUser, project, request);
            Toast.makeText(ConfirmInvestActivity.this, "Investment succesful!", Toast.LENGTH_LONG).show();
            //add to investor array
            if (!project.getInvestors().toString().contains(ParseUser.getCurrentUser().getObjectId())){
                project.add("investors", ParseUser.getCurrentUser());
            }
            project.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null){
                        e.printStackTrace();
                    }
                }
            });
            //go back to feed
            Intent finished = new Intent(ConfirmInvestActivity.this, MainActivity.class);
            startActivity(finished);
            finish();
        }
    }

    //create transaction object and save to parse
    public void createTransaction(float amount, ParseUser sender, Project project, Request request){
        final Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setSender(sender);
        transaction.setProject(project);
        transaction.setRequest(request);
        transaction.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Log.d("TransactionCreate", "create Transaction Success!");
                }else{
                    e.printStackTrace();
                    Log.e("TransactionCreate", "Failed creating Transaction");
                }
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
                queryReceiverBalance(receiveUser);
            }

        });
    }

    protected void queryReceiverBalance(ParseUser user) {
        ParseQuery<Balance> projectQuery = new ParseQuery<>(Balance.class);

        projectQuery.whereEqualTo("objectId", user.getParseObject("money").getObjectId());
        projectQuery.findInBackground(new FindCallback<Balance>() {
            @Override
            public void done(List<Balance> posts, ParseException e) {
                if (e != null) {
                    Log.e("Query receiver balance","Error with query");
                    e.printStackTrace();
                    return;
                }
                recieverBalance = posts.get(0);
            }

        });
    }

    protected void queryUserBalance(ParseUser user) {
        ParseQuery<Balance> projectQuery = new ParseQuery<>(Balance.class);

        projectQuery.whereEqualTo("objectId", user.getParseObject("money").getObjectId());
        projectQuery.findInBackground(new FindCallback<Balance>() {
            @Override
            public void done(List<Balance> posts, ParseException e) {
                if (e != null) {
                    Log.e("Query user balance","Error with query");
                    e.printStackTrace();
                    return;
                }
                investorBalance = posts.get(0);
                tvBalance.setText("Your current balance: $" + investorBalance.getNumber("amount"));
            }

        });
    }

}