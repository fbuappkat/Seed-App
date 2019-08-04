package com.example.kat_app.Activities;

import android.os.Handler;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kat_app.Adapters.CommentsAdapter;
import com.example.kat_app.Adapters.MessageAdapter;
import com.example.kat_app.Models.Message;
import com.example.kat_app.Models.Update;
import com.example.kat_app.Models.Comment;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/* UpdateDetailsActivity displays all the comments associated with an update. */
public class UpdateDetailsActivity extends AppCompatActivity {

    @BindView(R.id.tvUser)
    TextView tvUser;
    @BindView(R.id.tvRelativeTime)
    TextView tvTime;
    @BindView(R.id.tvCaption)
    TextView tvCaption;
    @BindView(R.id.rvComments)
    RecyclerView rvComments;
    @BindView(R.id.swipeContainer)
    PullRefreshLayout swipeContainer;
    @BindView(R.id.btnAddComment)
    Button btnComment;
    @BindView(R.id.etComment)
    EditText etComment;
    @BindView(R.id.ivDetailsToFeed)
    ImageButton ivBack;
    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;

    private Update update;
    private ParseUser user;
    protected CommentsAdapter adapter;
    protected List<Comment> commentList;

    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;

    private final String KEY_PROFILE_IMAGE = "profile_image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_details);

        //unwrap the update passed in via intent, using its simple name as a key
        update = Parcels.unwrap(getIntent().getParcelableExtra("update"));

        //unwrap the user passed in via intent, using its simple name as a key
        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));


        ButterKnife.bind(this);

        setupSwipeRefreshing();

        setUpCommenting();

        tvUser.setText(user.getUsername());
        tvCaption.setText(update.getCaption());
        tvTime.setText(getRelativeTimeAgo(String.valueOf(update.getCreatedAt())));

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
        setCommentButton();
        setBackButton();
    }

    private void setUpCommenting() {
        // create the data source
        commentList = new ArrayList<>();
        mFirstLoad = true;
        queryComments();

        // create the adapter
        adapter = new CommentsAdapter(this, commentList);
        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setAdapter(adapter);
    }

    protected void setupSwipeRefreshing() {
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchHomeAsync(0);
            }
        });
        swipeContainer.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);
        swipeContainer.setColor(getResources().getColor(R.color.kat_grey_6));
    }

    protected void fetchHomeAsync(int page) {
        commentList.clear();
        adapter.clear();
        queryComments();
        swipeContainer.setRefreshing(false);
    }

    private void queryComments() {
        Comment.Query commentQuery = new Comment.Query();
        commentQuery.withUpdate().withUser();

        commentQuery.whereEqualTo("update", update);


        commentQuery.orderByAscending("createdAt");

        // Run selection asynchronously
        commentQuery.findInBackground(new FindCallback<Comment>() {
            public void done(List<Comment> comments, com.parse.ParseException e) {
                if (e == null) {
                    commentList.clear();
                    commentList.addAll(comments);
                    adapter.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        rvComments.scrollToPosition(0);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e("comment", "Error Loading Comments" + e);
                }
            }

        });
    }

    private void setCommentButton() {
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String comment = etComment.getText().toString();
                final Comment newComment = new Comment();
                newComment.setUser(ParseUser.getCurrentUser());
                newComment.setComment(comment);
                newComment.setUpdate(update);

                newComment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        if (e != null) {
                            e.printStackTrace();
                            System.out.println("ERROR");
                            return;
                        }

                        adapter.notifyDataSetChanged();
                        update.addComment(newComment);
                        etComment.setText("");
                        update.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(com.parse.ParseException e) {
                                if (e != null) {
                                    e.printStackTrace();
                                    return;
                                }
                            }
                        });
                    }
                });

            }
        });


    }

    // return how long ago relative to current time tweet was sent
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate.toUpperCase();
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
}
