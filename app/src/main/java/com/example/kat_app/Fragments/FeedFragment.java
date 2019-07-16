package com.example.kat_app.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kat_app.Activities.LoginActivity;
import com.example.kat_app.Models.Update;
import com.example.kat_app.R;
import com.example.kat_app.Adapters.UpdatesAdapter;
import android.support.v7.widget.DividerItemDecoration;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/* FBU 2019
   TimelineFragment displays a recycler view with updates on a user's personal projects
   and the projects they've invested in. Users can scroll to refresh posts.
 */
public class FeedFragment extends Fragment {

    protected  RecyclerView rvFeed;
    private Button btnLogout;
    public static final String TAG = "FeedFragment";
    protected UpdatesAdapter adapter;
    protected List<Update> updates;
    protected SwipeRefreshLayout swipeContainer;
    private int limit;
    // Store a member variable for the listener
    private com.codepath.instagram.EndlessRecyclerViewScrollListener scrollListener;
    private Button btnAddUpdate;
    private EditText etUpdateCaption;

    // onCreateView to inflate the view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvFeed = view.findViewById(R.id.rvFeed);
        etUpdateCaption = view.findViewById(R.id.etUpdateCaption);
        btnAddUpdate = view.findViewById(R.id.btnAddUpdate);

        btnAddUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String caption = etUpdateCaption.getText().toString();
                ParseUser user = ParseUser.getCurrentUser();
                savePost(caption, user);
            }
        });

        btnLogout= view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
                Intent TimelineToLogin = new Intent(getContext(), LoginActivity.class);
                startActivity(TimelineToLogin);
            }
        });

        // create the data source
        updates = new ArrayList<>();
        // create the adapter
        adapter = new UpdatesAdapter(getContext(), updates);
        // add line between items
        rvFeed.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        // set the adapter on the recycler view
        rvFeed.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                updates.clear();
                adapter.clear();
                queryUpdates();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        queryUpdates();
    }

    // Save a new update to the server
    private void savePost(String caption, ParseUser parseUser) {
        Update update = new Update();
        update.setCaption(caption);
        //update.setUser(parseUser);
        update.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Error while saving");
                    e.printStackTrace();
                    return;
                }
                Log.d(TAG, "Success!");
                etUpdateCaption.setText("");
            }
        });
        queryUpdates();
    }

    //get posts via network request
    protected void queryUpdates() {
        ParseQuery<Update> updateQuery = new ParseQuery<Update>(Update.class);
        //updateQuery.include(Update.KEY_USER);
        //postQuery.setLimit(limit);
        //postQuery.addDescendingOrder(Update.KEY_CREATED_AT);

        updateQuery.findInBackground(new FindCallback<Update>() {
            @Override
            public void done(List<Update> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error with string query");
                    e.printStackTrace();
                    return;
                }
                updates.addAll(posts);
                Log.d(TAG,Integer.toString(updates.size()));
                for (int i = 0; i < updates.size(); i++) {
                    Update update = updates.get(i);
                    Log.d(TAG,"Update: " + update.getCaption() + update.getUser());
                }
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });
    }
}
