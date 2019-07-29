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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kat_app.Models.Project;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OtherUserProfileActivity extends AppCompatActivity {

    public static final String TAG = "OtherUserProfileActivity";

    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvUsername)
    TextView tvUsername;
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
    @BindView(R.id.tvFollowerCount)
    TextView tvFollowerCount;
    @BindView(R.id.btnFollow)
    Button btnFollow;

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
        user = (ParseUser) Parcels.unwrap(getIntent().getParcelableExtra("User"));

        setProfileInfo(user);
        setBackButton();
        setChatButton();


        if (!user.getJSONArray("followers").toString().contains(ParseUser.getCurrentUser().getObjectId())) {
            btnFollow.setText("Follow");
            //todo fix - parse does not allow changing other user info.
        } else {
            btnFollow.setText("Unfollow");

        }

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!user.getJSONArray("followers").toString().contains(ParseUser.getCurrentUser().getObjectId())) {
                    user.add("followers", ParseUser.getCurrentUser());
                    tvFollowerCount.setText(Integer.toString(user.getJSONArray("followers").length()));
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Toast.makeText(OtherUserProfileActivity.this, "User followed!", Toast.LENGTH_SHORT).show();
                    btnFollow.setText("Unfollow");
                } else {
                    btnFollow.setText("Follow");

                }
            }
        });
    }

    private void setProfileInfo(ParseUser user) {
        try {
            tvName.setText(user.fetchIfNeeded().getString(KEY_NAME));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvUsername.setText("@" + user.getUsername());
        tvFollowerCount.setText(Integer.toString(user.getJSONArray("followers").length()));
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

        queryInvested(user);
        queryProjects(user);
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
                Intent message = new Intent(OtherUserProfileActivity.this, MessageActivity.class);
                message.putExtra(OtherUserProfileActivity.class.getSimpleName(), Parcels.wrap(user));
                startActivity(message);
            }
        });
    }

    protected void queryProjects(ParseUser user) {
        ParseQuery<Project> projectQuery = new ParseQuery<Project>(Project.class);
        projectQuery.whereEqualTo("author", user);

        projectQuery.findInBackground(new FindCallback<Project>() {
            @Override
            public void done(List<Project> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error with query");
                    e.printStackTrace();
                    return;
                }
                tvProjectsCount.setText(Integer.toString(posts.size()));
            }
        });
    }

    protected void queryInvested(ParseUser user) {
        final ParseUser otherUser = user;
        ParseQuery<Project> projectQuery = new ParseQuery<Project>(Project.class);
        projectQuery.findInBackground(new FindCallback<Project>() {
            @Override
            public void done(List<Project> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error with query");
                    e.printStackTrace();
                    return;
                }
                int count = 0;
                for (Project project : posts){
                    if(project.getInvestors().toString().contains(otherUser.getObjectId())){
                        count++;
                    }
                }
                tvInvestmentsCount.setText(Integer.toString(count));
            }
        });
    }
}
