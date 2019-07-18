package com.example.kat_app.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kat_app.Models.Update;
import com.example.kat_app.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class OtherUserProfileActivity extends AppCompatActivity {

    public static final String TAG = "OtherUserProfileActivity";
    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvUsername;
    private TextView tvBalanceCount;
    private TextView tvProjectsCount;
    private TextView tvInvestmentsCount;
    private TextView tvBio;
    private ImageView ivSettings;
    private ImageView ivEdit;
    private static final String KEY_NAME = "name";
    private static final String KEY_PROFILE_IMAGE = "profile_image";
    private static final String KEY_BIO = "bio";
    private static final String KEY_BALANCE = "balance";
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        Update update = (Update) Parcels.unwrap(getIntent().getParcelableExtra(Update.class.getSimpleName()));
        ParseUser user = update.getUser();

        setProfileInfo(user);
        setBackButton();
    }

    private void setProfileInfo(ParseUser user) {
        // Find references for the views
        tvName = findViewById(R.id.tvName);
        tvUsername = findViewById(R.id.tvUsername);
        tvProjectsCount = findViewById(R.id.tvProjectsCount);
        tvInvestmentsCount = findViewById(R.id.tvInvestmentsCount);
        ivProfileImage = findViewById(R.id.ivProfileImageUpdate);
        tvBalanceCount = findViewById(R.id.tvBalanceCount);
        tvBio = findViewById(R.id.tvBio);
        ivProfileImage = findViewById(R.id.ivProfileImage);

        try {
            tvName.setText(user.fetchIfNeeded().getString(KEY_NAME));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvUsername.setText("@" + user.getUsername());
        tvBalanceCount.setText("$" + user.getInt(KEY_BALANCE));
        tvBio.setText(user.getString(KEY_BIO));

        ParseFile profileImage = user.getParseFile(KEY_PROFILE_IMAGE);
        if (profileImage != null) {
            Glide.with(this)
                    .load(profileImage.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }
        else {
            Glide.with(this)
                    .load(R.drawable.default_profile_image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }
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
