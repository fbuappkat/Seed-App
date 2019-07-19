package com.example.kat_app.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.kat_app.R;
import com.parse.ParseUser;

public class ManageAccountActivity extends AppCompatActivity {

    private ImageView ivBack;
    private ConstraintLayout creditsHolder;
    private Button bLogout;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        creditsHolder = findViewById(R.id.creditHolder);


        creditsHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageAccountActivity.this, ManageCreditActivity.class);
                startActivity(intent);
            }
        });

        setStatusBarColor(R.color.kat_white);
        setBackButton();
        setLogoutButton();
    }

    private void setStatusBarColor(int statusBarColor) {
        Window window = this.getWindow();

        // Make sure that status bar text is still visible
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(this.getResources().getColor(statusBarColor));
    }

    private void setLogoutButton() {
        // Find reference for the view
        bLogout = findViewById(R.id.bLogout);

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
        // Find reference for the view
        ivBack = findViewById(R.id.ivProfileToFeed);

        // Set on-click listener for for image view to launch edit account activity
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
