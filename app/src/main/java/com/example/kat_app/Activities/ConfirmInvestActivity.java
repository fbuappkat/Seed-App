package com.example.kat_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kat_app.Models.Balance;
import com.example.kat_app.Models.Equity;
import com.example.kat_app.Models.Project;
import com.example.kat_app.Models.Request;
import com.example.kat_app.Models.Transaction;
import com.example.kat_app.Models.Update;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmInvestActivity extends AppCompatActivity {

    @BindView(R.id.tvConfirm)
    TextView tvConfirm;
    @BindView(R.id.tvPrice)
    TextView tvPrice;
    @BindView(R.id.tvInvestments)
    TextView tvBalance;
    @BindView(R.id.tvProject)
    TextView tvProject;
    @BindView(R.id.tvRequest)
    TextView tvRequest;
    @BindView(R.id.btnConfirm)
    Button btnConfirm;
    @BindView(R.id.ivBack)
    ImageView ivBack;

    private Request request;
    private Project project;

    private ParseUser investUser = ParseUser.getCurrentUser();
    private ParseUser receiveUser;

    private Balance receiverBalance;
    private Balance investorBalance;

    private float toInvest;
    private float totalProjectFunds;
    private float equity;

    String TAG = "Confirm Invest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_invest);

        ButterKnife.bind(this);

        setBackButton();

        //get values
        request = Parcels.unwrap(getIntent().getParcelableExtra("request"));
        project = Parcels.unwrap(getIntent().getParcelableExtra("project"));
        toInvest = getIntent().getFloatExtra("toInvest", 0);
        totalProjectFunds = getIntent().getFloatExtra("total", 0);

        //setValues
        tvPrice.setText(request.getReceived() + "/" + request.getPrice());
        tvRequest.setText(request.getRequest());
        equity = project.getEquity() / totalProjectFunds * toInvest;
        equity = round(equity);
        tvConfirm.setText("Are you sure you want to invest $" + toInvest + "0? This investment would give you " + equity + "% equity stake in this project.");
        //get user and set balance
        queryUserBalance(ParseUser.getCurrentUser());

        //get project and set project name
        queryProject(request);

        //confirm and invest
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invest();
            }
        });
    }

    //invest funds
    public void invest() {
        //get current balance and request funds received so far
        float curBalanceInvestor = Float.parseFloat(investorBalance.getAmount().toString());
        float curBalanceReceiver = Float.parseFloat(receiverBalance.getAmount().toString());
        float curRequestFunds = request.getReceived();
        //check if investor has enough funds
        if (curBalanceInvestor < toInvest) {
            Toast.makeText(this, "You do not have enough in your balance to invest this amount!", Toast.LENGTH_LONG).show();
        } else {
            //change balance and put into request
            investorBalance.put("amount", curBalanceInvestor - toInvest);
            receiverBalance.put("amount", curBalanceReceiver + toInvest);
            request.put("received", curRequestFunds + toInvest);
            //save values
            request.saveInBackground();
            investorBalance.saveInBackground();
            receiverBalance.saveInBackground();
            //create transaction for history
            Toast.makeText(ConfirmInvestActivity.this, "Investment succesful!", Toast.LENGTH_LONG).show();
            //add to investor array
            if (!project.getInvestors().toString().contains(ParseUser.getCurrentUser().getObjectId())) {
                project.add("investors", ParseUser.getCurrentUser());
            }
            project.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        e.printStackTrace();
                    }
                }
            });
            Equity newStake = new Equity();
            newStake.setEquity(equity);
            newStake.setInvestor(ParseUser.getCurrentUser());
            newStake.setProject(project);
            newStake.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        e.printStackTrace();
                    }
                }
            });
            createTransaction(toInvest, investUser, project, request, newStake);
            //go back to feed
            Intent finished = new Intent(ConfirmInvestActivity.this, MainActivity.class);
            startActivity(finished);
            finish();
        }
    }

    //create transaction object and save to parse
    public void createTransaction(float amount, ParseUser sender, Project project, Request request, Equity equity) {
        final Transaction investmentTransaction = new Transaction();
        investmentTransaction.setAmount(amount);
        investmentTransaction.setSender(sender);
        investmentTransaction.setProject(project);
        investmentTransaction.setRequest(request);
        investmentTransaction.setType("investment");
        investmentTransaction.setEquity(equity);
        investmentTransaction.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("TransactionCreate", "create Transaction Success!");
                } else {
                    e.printStackTrace();
                    Log.e("TransactionCreate", "Failed creating Transaction");
                }
            }
        });
        final Transaction earningTransaction = new Transaction();
        earningTransaction.setAmount(amount);
        earningTransaction.setReceiver(receiveUser);
        earningTransaction.setProject(project);
        earningTransaction.setRequest(request);
        earningTransaction.setType("earning");
        earningTransaction.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("TransactionCreate", "create Transaction Success!");
                } else {
                    e.printStackTrace();
                    Log.e("TransactionCreate", "Failed creating Transaction");
                }
            }
        });
        String update = sender.getString("name") + " invested $" + amount + "0 for '" + request.getRequest() + "'";
        postUpdate(update, sender, project);
    }

    private void postUpdate(String update, ParseUser currentUser, Project project) {

        Update newUpdate = new Update();
        ParseUser curUser = ParseUser.getCurrentUser();
        ParseACL acl = new ParseACL(curUser);
        acl.setPublicWriteAccess(true);
        acl.setPublicReadAccess(true);
        currentUser.setACL(acl);
        currentUser.saveInBackground();
        newUpdate.setCaption(update);
        newUpdate.setUser(currentUser);
        newUpdate.put("type", "Investment");
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
                    Log.e("Query requests", "Error with query");
                    e.printStackTrace();
                    return;
                }
                project = posts.get(0);
                tvProject.setText(project.getName());
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
            public void done(List<ParseUser> user, ParseException e) {
                if (e != null) {
                    Log.e("Query requests", "Error with query");
                    e.printStackTrace();
                    return;
                }
                receiveUser = user.get(0);
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
                    Log.e("Query receiver balance", "Error with query");
                    e.printStackTrace();
                    return;
                }
                receiverBalance = posts.get(0);
            }

        });
    }

    protected void queryUserBalance(ParseUser user) {
        ParseQuery<Balance> projectQuery = new ParseQuery<>(Balance.class);

        projectQuery.whereEqualTo("user", user);
        projectQuery.findInBackground(new FindCallback<Balance>() {
            @Override
            public void done(List<Balance> posts, ParseException e) {
                if (e != null) {
                    Log.e("Query user balance", "Error with query");
                    e.printStackTrace();
                    return;
                }
                investorBalance = posts.get(0);
                tvBalance.setText("$" + (investorBalance.getNumber("amount")));
            }

        });
    }

    private float round(float value) {
        return (float) Math.round(value * 100) / 100;
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
}