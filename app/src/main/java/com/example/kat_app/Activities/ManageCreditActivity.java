package com.example.kat_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kat_app.Models.Balance;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ManageCreditActivity extends AppCompatActivity {

    private ConstraintLayout depositHolder;
    private ImageView ivBack;
    private TextView tvName;
    private TextView tvCurrBalanceCount;
    private Balance balance;

    private static final String KEY_NAME = "name";
    private static final String KEY_BALANCE = "balance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_credit);

        setDepositCreditOption();
        setBackButton();
        setProfileInfo();
    }

    private void setDepositCreditOption() {
        // Find reference for the view
        depositHolder = findViewById(R.id.depositHolder);

        // Open deposit credit options on click
        depositHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageCreditActivity.this, PayPalCheckoutActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setProfileInfo() {
        // Find references for the views
        tvName = findViewById(R.id.tvName);
        tvCurrBalanceCount = findViewById(R.id.tvCurrBalanceCount);

        // Get the current user
        ParseUser currUser = ParseUser.getCurrentUser();

        tvName.setText(currUser.getString(KEY_NAME));
        queryBalance(currUser);
    }

    //get balance from user pointer and set balanceHolder
    protected void queryBalance(ParseUser currUser) {
        final ParseQuery<Balance> balanceQuery = new ParseQuery<>(Balance.class);

        balanceQuery.whereEqualTo("user", currUser);
        balanceQuery.findInBackground(new FindCallback<Balance>() {
            @Override
            public void done(List<Balance> accounts, ParseException e) {
                if (e != null) {
                    Log.e("Query requests","Error with query");
                    e.printStackTrace();
                    return;
                }

                balance = accounts.get(0);

                tvCurrBalanceCount.setText("$" + balance.getAmount());
            }
        });
    }

    private void setBackButton() {
        // Find reference for the view
        ivBack = findViewById(R.id.ivBack);

        // Set on-click listener for for image view to launch edit account activity
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
