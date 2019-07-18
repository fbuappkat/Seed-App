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
import android.widget.ImageView;

import com.example.kat_app.Activities.AddUpdateActivity;
import com.example.kat_app.Models.Update;
import com.example.kat_app.R;
import com.example.kat_app.Adapters.UpdatesAdapter;
import android.support.v7.widget.DividerItemDecoration;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/* FBU 2019
   TimelineFragment displays a recycler view with updates on a user's personal projects
   and the projects they've invested in. Users can scroll to refresh posts.
 */
public class FeedFragment extends Fragment {

    protected  RecyclerView rvFeed;
    public static final String TAG = "FeedFragment";
    protected UpdatesAdapter adapter;
    protected List<Update> updates;
    protected SwipeRefreshLayout swipeContainer;
    private int limit;
    // Store a member variable for the listener
    private com.codepath.instagram.EndlessRecyclerViewScrollListener scrollListener;
    private ImageView btnGoToAddUpdate;

    // onCreateView to inflate the view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        rvFeed = view.findViewById(R.id.rvFeed);
        setAddButton(view);

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
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark,
                android.R.color.holo_orange_light);
        queryUpdates();
    }

    //get posts via network request
    protected void queryUpdates() {
        ParseQuery<Update> updateQuery = new ParseQuery<Update>(Update.class);
        //updateQuery.include(Update.KEY_USER);
        updateQuery.addDescendingOrder(Update.KEY_CREATED_AT);

        updateQuery.findInBackground(new FindCallback<Update>() {
            @Override
            public void done(List<Update> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error with string query");
                    e.printStackTrace();
                    return;
                }
                updates.addAll(posts);
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void setAddButton(View view) {
        // Find reference for the view
        btnGoToAddUpdate = view.findViewById(R.id.ivAddUpdate);

        // Make the update image clickable
        btnGoToAddUpdate.setClickable(true);
        btnGoToAddUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent TimelineToUpdate = new Intent(getContext(), AddUpdateActivity.class);
                startActivity(TimelineToUpdate);
            }
        });
    }
}
