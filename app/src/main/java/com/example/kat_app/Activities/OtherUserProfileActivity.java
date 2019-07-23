package com.example.kat_app.Activities;

import android.content.Context;
import android.content.Intent;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class OtherUserProfileActivity extends AppCompatActivity {

    public static final String TAG = "OtherUserProfileActivity";

    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvUser)
    TextView tvUsername;
    @BindView(R.id.tvBalanceCount)
    TextView tvBalanceCount;
    @BindView(R.id.tvProjectsCount)
    TextView tvProjectsCount;
    @BindView(R.id.tvInvestmentsCount)
    TextView tvInvestmentsCount;
    @BindView(R.id.tvBio)
    TextView tvBio;
    @BindView(R.id.ivProfileToFeed)
    ImageView ivBack;
    @BindView(R.id.ivChat)
    ImageView ivChat;

    private static final String KEY_NAME = "name";
    private static final String KEY_PROFILE_IMAGE = "profile_image";
    private static final String KEY_BIO = "bio";
    private static final String KEY_BALANCE = "balance";
    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        ButterKnife.bind(this);
        Update update = (Update) Parcels.unwrap(getIntent().getParcelableExtra(Update.class.getSimpleName()));
        user = update.getUser();

        setProfileInfo(user);
        setBackButton();
        setChatButton();
    }

    private void setProfileInfo(ParseUser user) {
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
        } else {
            Glide.with(this)
                    .load(R.drawable.default_profile_image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }
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

    private void setChatButton() {
        // Set on-click listener for for image view to launch edit account activity
        ivChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create intent for the new activity
                /*Intent otherProfileToChat = new Intent(context, UpdateDetailsActivity.class);
                //serialize the update using parceler, use its short name as a key
                //otherProfileToChat.putExtra(Update.class.getSimpleName(), Parcels.wrap(user));
                // show the activity
                startActivity(otherProfileToChat);*/
            }
        });
    }
}
