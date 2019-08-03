package com.example.kat_app.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.baoyz.widget.PullRefreshLayout;
import com.codepath.instagram.EndlessRecyclerViewScrollListener;
import com.example.kat_app.Activities.AddUpdateActivity;
import com.example.kat_app.Adapters.UpdatesAdapter;
import com.example.kat_app.Models.Update;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
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
    protected PullRefreshLayout swipeContainer;
    // Store a member variable for the listener
    private com.codepath.instagram.EndlessRecyclerViewScrollListener scrollListener;
    private ImageView btnGoToAddUpdate;
    private ProgressBar pbLoad;

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

        pbLoad = view.findViewById(R.id.pbLoad);
        rvFeed.setVisibility(View.INVISIBLE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        // create the data source
        updates = new ArrayList<>();
        // create the adapter
        adapter = new UpdatesAdapter(getActivity(), updates);
        // set the adapter on the recycler view
        rvFeed.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvFeed.setLayoutManager(layoutManager);

        setupSwipeRefreshing(view);
        enableEndlessScrolling(layoutManager);

        rvFeed.addOnScrollListener(scrollListener);
        //Todo - figure out how to make loading bar keep going until data is actually binded
        queryUpdates(new Date(0));

    }

    protected void enableEndlessScrolling(LinearLayoutManager layoutManager) {
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryUpdates(getMaxDate());
            }
        };
    }

    protected void setupSwipeRefreshing(View view) {
        swipeContainer = view.findViewById(R.id.swipeContainer);

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
        updates.clear();
        adapter.clear();
        queryUpdates(new Date(0));
        swipeContainer.setRefreshing(false);
    }

    //get posts via network request
    protected void queryUpdates(final Date maxDate) {
        final Update.Query updateQuery = new Update.Query();
        updateQuery.getTop();

        // If app is just opened, get newest 20 posts
        // Else query for older posts
        if (maxDate.equals(new Date(0))) {
            adapter.clear();
            updateQuery.getTop();
        } else {
            updateQuery.getNext(maxDate).getTop();
        }

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
                pbLoad.setVisibility(View.INVISIBLE);
                rvFeed.setVisibility(View.VISIBLE);
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

    // Get maximum Date to find next post to load.
    protected Date getMaxDate() {
        int size = updates.size();
        if (size == 0) {
            return new Date(0);
        } else {
            Update oldest = updates.get(updates.size() - 1);
            return oldest.getCreatedAt();
        }
    }
}
