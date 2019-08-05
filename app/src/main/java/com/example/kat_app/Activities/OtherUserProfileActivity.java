package com.example.kat_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.BounceTouchListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kat_app.Adapters.InvestedProjectsAdapter;
import com.example.kat_app.Adapters.UserProjectAdapter;
import com.example.kat_app.Models.Equity;
import com.example.kat_app.Models.Followers;
import com.example.kat_app.Models.Project;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.parceler.Parcels;

import java.util.ArrayList;
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
    @BindView(R.id.rvProjects)
    RecyclerView rvProjects;
    @BindView(R.id.rvInvested)
    RecyclerView rvInvested;
    @BindView(R.id.ivBack)
    ImageButton ivBack;
    @BindView(R.id.ivChat)
    ImageButton ivChat;
    @BindView(R.id.tvFollowerCount)
    TextView tvFollowerCount;
    @BindView(R.id.btnFollow)
    Button btnFollow;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.portfolioTabLayout)
    TabLayout tabLayout;

    private UserProjectAdapter userProjectAdapter;
    private InvestedProjectsAdapter investedProjectsAdapter;
    private List<Project> userProjects;
    private List<Equity> investedProjects;

    private static final String KEY_NAME = "name";
    private static final String KEY_PROFILE_IMAGE = "profile_image";
    private static final String KEY_BIO = "bio";
    private static final String KEY_BALANCE = "balance";
    private boolean following;
    private JSONArray follows;
    private Followers followers;
    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        ButterKnife.bind(this);
        user = (ParseUser) Parcels.unwrap(getIntent().getParcelableExtra("User"));

        setProfileInfo(user);
        setTabLayout();
        setBackButton();
        setChatButton();
        queryFollowers(user);

        BounceTouchListener bounceTouchListener = BounceTouchListener.create(scrollView, R.id.profile,
                new BounceTouchListener.OnTranslateListener() {
                    @Override
                    public void onTranslate(float translation) {
                    }
                }
        );
        scrollView.setOnTouchListener(bounceTouchListener);
    }

    private void setProfileInfo(ParseUser user) {
        userProjects = new ArrayList<>();
        investedProjects = new ArrayList<>();
        try {
            tvName.setText(user.fetchIfNeeded().getString(KEY_NAME));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvUsername.setText("@" + user.getUsername());
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
        queryInvestments(user);
    }

    private void setTabLayout() {
        tabLayout.getTabAt(0).getIcon().setTint(getResources().getColor(R.color.kat_orange_1));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    tab.getIcon().setTint(getResources().getColor(R.color.kat_orange_1));
                    tabLayout.getTabAt(1).getIcon().setTint(getResources().getColor(R.color.kat_black));
                    rvProjects.setVisibility(View.VISIBLE);
                    rvInvested.setVisibility(View.GONE);
                }

                if (tab.getPosition() == 1) {
                    tab.getIcon().setTint(getResources().getColor(R.color.kat_orange_1));
                    tabLayout.getTabAt(0).getIcon().setTint(getResources().getColor(R.color.kat_black));
                    rvInvested.setVisibility(View.VISIBLE);
                    rvProjects.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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
                    Log.e(TAG, "Error with query");
                    e.printStackTrace();
                    return;
                }
                userProjects.addAll(posts);
                tvProjectsCount.setText(Integer.toString(posts.size()));

                // create the adapter
                userProjectAdapter = new UserProjectAdapter(OtherUserProfileActivity.this, userProjects);
                // set the adapter on the recycler view
                // set the layout manager on the recycler view
                rvProjects.setLayoutManager(new LinearLayoutManager(OtherUserProfileActivity.this));
                rvProjects.setAdapter(userProjectAdapter);

            }
        });
    }

    protected void queryFollowers(ParseUser user) {
        ParseQuery<Followers> projectQuery = new ParseQuery<Followers>(Followers.class);
        projectQuery.whereEqualTo("user", user);

        projectQuery.findInBackground(new FindCallback<Followers>() {
            @Override
            public void done(List<Followers> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with query");
                    e.printStackTrace();
                    return;
                }
                try {
                    final int followCount = posts.get(0).getFollowers().length();
                    tvFollowerCount.setText(Integer.toString(followCount));
                    follows = posts.get(0).getFollowers();
                    followers = posts.get(0);
                    if (!follows.toString().contains(ParseUser.getCurrentUser().getObjectId())) {
                        btnFollow.setText("Follow");
                    } else {
                        btnFollow.setText("Unfollow");

                    }

                    following = follows.toString().contains(ParseUser.getCurrentUser().getObjectId());
                    btnFollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!following) {
                                followers.add("followers", ParseUser.getCurrentUser());
                                followers.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e != null) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                Toast.makeText(OtherUserProfileActivity.this, "User followed!", Toast.LENGTH_SHORT).show();
                                tvFollowerCount.setText(Integer.toString(followers.getFollowers().length()));
                                btnFollow.setText("Unfollow");
                                following = true;
                            } else {
                                ArrayList<ParseUser> remove = new ArrayList<>();
                                remove.add(ParseUser.getCurrentUser());
                                followers.removeAll("followers", remove);
                                followers.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e!= null){
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                btnFollow.setText("Follow");
                                tvFollowerCount.setText(Integer.toString(followers.getFollowers().length()));

                                following = false;

                            }
                        }
                    });
                } catch (IndexOutOfBoundsException e2) {
                    e2.printStackTrace();
                }
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
                    Log.e(TAG, "Error with query");
                    e.printStackTrace();
                    return;
                }
                int count = 0;
                for (Project project : posts) {
                    if (project.getInvestors().toString().contains(otherUser.getObjectId())) {
                        count++;
                    }
                }
                tvInvestmentsCount.setText(Integer.toString(count));
            }
        });
    }

    protected void queryInvestments(ParseUser user) {
        ParseQuery<Equity> equityQuery = new ParseQuery<>(Equity.class);

        equityQuery.whereEqualTo("investor", user);
        equityQuery.findInBackground(new FindCallback<Equity>() {
            @Override
            public void done(List<Equity> investments, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with query");
                    e.printStackTrace();
                    return;
                }
                investedProjects.addAll(investments);

                // create the adapter
                investedProjectsAdapter = new InvestedProjectsAdapter(OtherUserProfileActivity.this, investedProjects);
                // set the adapter on the recycler view
                rvInvested.setLayoutManager(new LinearLayoutManager(OtherUserProfileActivity.this));
                rvInvested.setAdapter(investedProjectsAdapter);
                // set the layout manager on the recycler view
            }
        });
    }
}
