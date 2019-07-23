package com.example.kat_app.Activities;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.kat_app.Fragments.ProfileFragment;
import com.example.kat_app.R;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageAccountActivity extends AppCompatActivity {

    @BindView(R.id.ivBack)
    ImageButton ivBack;
    @BindView(R.id.creditHolder)
    ConstraintLayout creditsHolder;
    @BindView(R.id.bLogout)
    Button bLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        ButterKnife.bind(this);
        MainActivity.setStatusBar(getWindow());
        setBackButton();
        setLogoutButton();

        creditsHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageAccountActivity.this, ManageCreditActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void setLogoutButton() {
        // Make the logout image clickable and open up dialog on click
        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ManageAccountActivity.this);
                builder.setTitle("Log Out");
                builder.setIcon(R.drawable.ic_alert);
                builder.setMessage("Are you sure you want to log out?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.logOut();
                        final Intent intent = new Intent(ManageAccountActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();

                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                Button negButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                negButton.setTextColor(getResources().getColor(R.color.kat_orange_1));
                Button posButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                posButton.setTextColor(getResources().getColor(R.color.kat_orange_1));
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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
