package com.example.kat_app.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kat_app.Models.Balance;
import com.example.kat_app.Models.Transaction;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WithdrawCreditActivity extends AppCompatActivity {

    private static final String TAG = "WithdrawCredit";

    @BindView(R.id.bWithdrawCredit)
    Button bWithdrawCredit;
    @BindView(R.id.ivBack)
    ImageButton ivBack;
    @BindView(R.id.etCredits)
    EditText etCredits;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etConfirmEmail)
    EditText etConfirmEmail;
    @BindView(R.id.tvNewBalanceCount)
    TextView tvNewBalanceCount;

    private float currBalance;
    private float removedCredits;

    private Transaction withdrawalTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_credit);

        ButterKnife.bind(this);

        MainActivity.setStatusBar(getWindow());
        setBackButton();
        setWithdrawButton();
        queryBalance(ParseUser.getCurrentUser());
    }

    private void withdrawCredit() {

        String withdrawValue = etCredits.getText().toString().replaceAll("[$,]", "");

        String email = etEmail.getText().toString();
        String confirmEmail = etConfirmEmail.getText().toString();

        if (withdrawValue.isEmpty()) {
            Toast.makeText(this, "Enter a valid amount for payment", Toast.LENGTH_SHORT).show();
            return;
        } else if (email.isEmpty()) {
            Toast.makeText(this, "Enter your paypal account", Toast.LENGTH_SHORT).show();
            return;
        } else if (confirmEmail.isEmpty()) {
            Toast.makeText(this, "Confirm your paypal account", Toast.LENGTH_SHORT).show();
            return;
        } else if (email.compareTo(confirmEmail) != 0) {
            Toast.makeText(this, "Paypal accounts don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        removedCredits = Float.parseFloat(withdrawValue);

        Float newBalance = round(currBalance - removedCredits);
        if (newBalance < 0F) {
            Toast.makeText(this, "Withdrawl exceeds current balance", Toast.LENGTH_SHORT).show();
        } else {
            queryBalance(ParseUser.getCurrentUser(), newBalance);
        }
    }

    //get balance from user pointer
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

                Balance balance = accounts.get(0);
                currBalance = balance.getAmount();
                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                tvNewBalanceCount.setText(formatter.format(round(currBalance)));
                etCredits.addTextChangedListener(balanceWatcher);
            }
        });
    }

    //update the user's balance
    protected void queryBalance(ParseUser currUser, final float amount) {
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
                Balance balance = accounts.get(0);
                balance.put("amount", amount);
                balance.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d(TAG, "Balance updated!");
                            setResult(RESULT_OK);
                        } else {
                            Log.e(TAG, "Error while updating balance.");
                            e.printStackTrace();
                        }
                    }
                });

                withdrawalTransaction = new Transaction();
                withdrawalTransaction.put("sender", ParseUser.getCurrentUser());
                withdrawalTransaction.put("amount", removedCredits);
                withdrawalTransaction.put("current_balance", amount);
                withdrawalTransaction.setType("withdrawal");
                withdrawalTransaction.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d(TAG, "transaction saved!");
                            onBackPressed();
                        } else {
                            Log.e(TAG, "Error while saving transaction.");
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    // update the user's potential balance
    private final TextWatcher balanceWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        String current = "";

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current)) {
                etCredits.removeTextChangedListener(this);

                String cleanString = s.toString().replaceAll("[$,.]", "");

                Float parsed = 0F;
                if (!cleanString.isEmpty()) {
                    parsed = Float.parseFloat(cleanString);

                    if (round(parsed / 100) > currBalance) {
                        parsed = 0F;
                    }
                }
                if (parsed == 0F) {
                    tvNewBalanceCount.setText("$" + round(currBalance));
                    tvNewBalanceCount.setTextColor(getResources().getColor(R.color.kat_black));
                } else {
                    tvNewBalanceCount.setText("$" + round(currBalance - (parsed / 100)));
                    tvNewBalanceCount.setTextColor(getResources().getColor(R.color.kat_orange_1));
                }
                String formatted = NumberFormat.getCurrencyInstance().format((parsed / 100));

                current = formatted;
                etCredits.setText(formatted);
                etCredits.setSelection(formatted.length());

                etCredits.addTextChangedListener(this);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void setWithdrawButton() {
        // submit payment on click
        bWithdrawCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                withdrawCredit();
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private float round(float value) {
        return (float) Math.round(value * 100) / 100;
    }
}
