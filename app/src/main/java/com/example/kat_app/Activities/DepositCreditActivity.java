package com.example.kat_app.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.example.kat_app.Models.Balance;
import com.example.kat_app.Models.Transaction;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DepositCreditActivity extends AppCompatActivity {

    private static final String TAG = "DepositCredit";

    @BindView(R.id.bDepositCredit)
    Button bDepositCredit;
    @BindView(R.id.ivBack)
    ImageButton ivBack;
    @BindView(R.id.etCredits)
    EditText etCredits;
    @BindView(R.id.tvNewBalanceCount)
    TextView tvNewBalanceCount;

    private HashMap<String, String> paramsHash;
    private String token;
    private String amount;
    private float currBalance;
    private float addedCredits;

    private String API_GET_TOKEN = "https://kat-app-247218.appspot.com/";
    private String API_CHECKOUT = "https://kat-app-247218.appspot.com/";

    private static final int REQUEST_CODE = 1234;

    private Transaction depositTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_credit);

        ButterKnife.bind(this);
        MainActivity.setStatusBar(getWindow());

        new DepositCreditActivity.getToken().execute();

        setBackButton();
        setDepositButton();
        queryBalance(ParseUser.getCurrentUser());

    }

    private void submitPayment() {
        String payValue = etCredits.getText().toString().replaceAll("[$,]", "");

        addedCredits = Float.parseFloat(payValue);

        if (!payValue.isEmpty()) {
            DropInRequest dropInRequest = new DropInRequest().clientToken(token);
            startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
        } else
            Toast.makeText(this, "Enter a valid amount for payment", Toast.LENGTH_SHORT).show();
    }

    private void sendPayments(final float newBalance, final ParseUser currUser) {
        RequestQueue queue = Volley.newRequestQueue(DepositCreditActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_CHECKOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.toString().contains("Successful")) {
                            queryBalance(currUser, newBalance);
                            Toast.makeText(DepositCreditActivity.this, "Payment Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DepositCreditActivity.this, "Payment Failed", Toast.LENGTH_SHORT).show();
                        }
                        Log.d("Response", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Err", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (paramsHash == null)
                    return null;
                Map<String, String> params = new HashMap<>();
                for (String key : paramsHash.keySet()) {
                    params.put(key, paramsHash.get(key));
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(mRetryPolicy);
        queue.add(stringRequest);
    }


    private class getToken extends AsyncTask {
        ProgressDialog mDailog;

        @Override
        protected Object doInBackground(Object[] objects) {
            HttpClient client = new HttpClient();
            client.get(API_GET_TOKEN, new HttpResponseCallback() {
                @Override
                public void success(final String responseBody) {
                    mDailog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            token = responseBody.substring(0, responseBody.indexOf("=") + 1);
                        }
                    });
                }

                @Override
                public void failure(Exception exception) {
                    mDailog.dismiss();
                    Log.d("Err", exception.toString());
                }
            });
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDailog = new ProgressDialog(DepositCreditActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
            mDailog.setCancelable(false);
            mDailog.setMessage("Loading Wallet, Please Wait");
            mDailog.show();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                String strNounce = nonce.getNonce();
                if (!etCredits.getText().toString().isEmpty()) {
                    amount = etCredits.getText().toString().replaceAll("[$,]", "");
                    ;
                    paramsHash = new HashMap<>();
                    paramsHash.put("amount", amount);
                    paramsHash.put("nonce", strNounce);

                    Float newBalance = round(currBalance + addedCredits);
                    ParseUser currUser = ParseUser.getCurrentUser();

                    sendPayments(newBalance, currUser);
                } else {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "User canceled", Toast.LENGTH_SHORT).show();
            } else {
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("Err", error.toString());
            }
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

                depositTransaction = new Transaction();
                depositTransaction.put("sender", ParseUser.getCurrentUser());
                depositTransaction.put("amount", addedCredits);
                depositTransaction.put("current_balance", amount);
                depositTransaction.setType("deposit");
                depositTransaction.saveInBackground(new SaveCallback() {
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

                NumberFormat formatter = NumberFormat.getCurrencyInstance();

                String cleanString = s.toString().replaceAll("[$,.]", "");

                Float parsed = 0F;
                if (!cleanString.isEmpty()) {
                    parsed = Float.parseFloat(cleanString);
                }
                if (parsed == 0F) {
                    tvNewBalanceCount.setText(formatter.format(round(currBalance)));
                    tvNewBalanceCount.setTextColor(getResources().getColor(R.color.kat_black));
                } else {
                    tvNewBalanceCount.setText(formatter.format(round(currBalance + (parsed / 100))));
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

    public void setDepositButton() {
        // submit payment on click
        bDepositCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPayment();
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
