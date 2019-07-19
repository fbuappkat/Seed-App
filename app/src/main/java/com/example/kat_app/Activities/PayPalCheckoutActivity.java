package com.example.kat_app.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.kat_app.R;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class PayPalCheckoutActivity extends AppCompatActivity {

    private static final String TAG = "paymentExample";

    public static final String PAYPAL_KEY = "AY7cWIVK0aUZ3EGQITbXLktCknZNtTtJd9mXlDpOUXSHLts9zjZoZcJ6rYLwAVQZbih3SbrL6ivw4jg3";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static PayPalConfiguration config;
    PayPalPayment addCredit;
    Button order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_credit);

        order = findViewById(R.id.order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakePayment();
            }
        });

        configPayPal();
    }

    private void configPayPal() {

        config = new PayPalConfiguration()
                .environment(CONFIG_ENVIRONMENT)
                .clientId(PAYPAL_KEY)
                .merchantName("PayPal login")
                .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"))
                .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"));
    }

    private void MakePayment() {

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        addCredit = new PayPalPayment(new BigDecimal(String.valueOf("250.37")), "USD", "Payment", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent payment = new Intent(this, PaymentActivity.class);
        payment.putExtra(PaymentActivity.EXTRA_PAYMENT, addCredit);
        payment.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startActivityForResult(payment, REQUEST_CODE_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (confirm == null) {

                    try {
                        System.out.println(confirm.toJSONObject().toString(4));
                        System.out.println(confirm.getPayment().toJSONObject()
                                .toString(4));
                        Toast.makeText(this, "Payment Successful!", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {

                        Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "payment canceled", Toast.LENGTH_LONG).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(this, "error occurred", Toast.LENGTH_LONG).show();

            }
        } else if (resultCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth = data
                        .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {

                        Log.i("FutuePaymentExample", auth.toJSONObject().toString(4));
                        String authorization_code = auth.getAuthorizationCode();
                        Log.d("FuturePaymentExample", authorization_code);
                        Log.e("paypal", "future payment code recived from PayPal: " + authorization_code);
                    } catch (JSONException e) {
                        Toast.makeText(this, "failure occurred", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}

