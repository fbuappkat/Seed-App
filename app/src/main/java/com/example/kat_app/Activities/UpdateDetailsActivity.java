package com.example.kat_app.Activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kat_app.Adapters.CommentsAdapter;
import com.example.kat_app.Models.Update;
import com.example.kat_app.R;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UpdateDetailsActivity extends AppCompatActivity {

    private TextView tvUser;
    private TextView tvTime;
    private TextView tvCaption;
    private Update update;
    protected RecyclerView rvComments;
    protected CommentsAdapter adapter;
    protected SwipeRefreshLayout swipeContainer;
    protected List<String> comments;
    private Button btnComment;
    private EditText etComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_details);

        //unwrap the update passed in via intent, using its simple name as a key
        update = (Update) Parcels.unwrap(getIntent().getParcelableExtra(Update.class.getSimpleName()));

        tvUser = findViewById(R.id.tvUser);
        tvTime = findViewById(R.id.tvRelativeTime);
        tvCaption = findViewById(R.id.tvCaption);
        rvComments = findViewById(R.id.rvComments);
        btnComment = findViewById(R.id.btnAddComment);
        etComment = findViewById(R.id.etComment);

        try {
            String username = update.getUser().fetchIfNeeded().getString("username");
            tvUser.setText(username);
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
        tvCaption.setText(update.getCaption());
        tvTime.setText(getRelativeTimeAgo(String.valueOf(update.getCreatedAt())));

        // create the data source
        comments = new ArrayList<>();
        // create the adapter
        adapter = new CommentsAdapter(comments);
        // add line between items
        rvComments.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        // set the adapter on the recycler view
        rvComments.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                comments.clear();
                adapter.clear();
                queryComments();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark,
                android.R.color.holo_orange_light);

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ParseUser user = ParseUser.getCurrentUser();
                final String comment = etComment.getText().toString();
                update.addComment(comment);
                etComment.setText("");
                queryComments();
            }
        });

        queryComments();
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
        return relativeDate;
    }

    private void queryComments() {
        if (update.getNumComments() > 0) {
            JSONArray jsonComments = update.getComments();
            for (int i = 0; i < update.getNumComments(); i++) {
                try {
                    comments.add(jsonComments.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            adapter.addAll(comments);
        }
    }
}
