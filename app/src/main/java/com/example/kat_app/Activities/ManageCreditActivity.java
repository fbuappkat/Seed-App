package com.example.kat_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kat_app.Models.Balance;
import com.example.kat_app.Models.Transaction;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.NumberFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageCreditActivity extends AppCompatActivity {

    @BindView(R.id.withdrawHolder)
    ConstraintLayout withdrawHolder;
    @BindView(R.id.depositHolder)
    ConstraintLayout depositHolder;
    @BindView(R.id.transactionHolder)
    ConstraintLayout transactionHolder;
    @BindView(R.id.ivBack)
    ImageButton ivBack;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvCurrBalanceCount)
    TextView tvCurrBalanceCount;

    private Balance balance;

    private static final int MANAGE_CREDIT_OPTION = 3;
    private static final String KEY_NAME = "name";


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_credit);

        ButterKnife.bind(this);
        MainActivity.setStatusBar(getWindow());
        setCreditOptions();
        setBackButton();
        setProfileInfo();
    }

    private void setCreditOptions() {
        // Open deposit credit option on click
        depositHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageCreditActivity.this, DepositCreditActivity.class);
                startActivityForResult(intent, MANAGE_CREDIT_OPTION);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // Open withdraw credit option on click
        withdrawHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageCreditActivity.this, WithdrawCreditActivity.class);
                startActivityForResult(intent, MANAGE_CREDIT_OPTION);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // Open transaction history option on click
        transactionHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageCreditActivity.this, TransactionHistoryActivity.class);
                startActivityForResult(intent, MANAGE_CREDIT_OPTION);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }


    private void setProfileInfo() {
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
                    Log.e("Query requests", "Error with query");
                    e.printStackTrace();
                    return;
                }

                balance = accounts.get(0);
                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                tvCurrBalanceCount.setText(formatter.format(round(balance.getAmount())));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MANAGE_CREDIT_OPTION && resultCode == RESULT_OK) {
            queryBalance(ParseUser.getCurrentUser());
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private float round(float value) {
        return (float) Math.round(value * 100) / 100;
    }

}
