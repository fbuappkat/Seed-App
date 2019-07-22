package com.example.kat_app.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.kat_app.Activities.ProjectDetailsActivity;
import com.example.kat_app.Activities.CreateProjectActivity;
import com.example.kat_app.Adapters.ProjectsAdapter;
import com.example.kat_app.Models.Project;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ProjectsAdapter.OnClickListener {

    RecyclerView rvProjects;
    private ImageView ivAdd;
    protected List<Project> projects;
    protected ProjectsAdapter adapter;
    SearchView editsearch;
    protected SwipeRefreshLayout swipeContainer;

    public static final String TAG = "HomeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivAdd = view.findViewById(R.id.ivAdd);
        // Locate the EditText in listview_main.xml
        editsearch = (SearchView) view.findViewById(R.id.search);
        editsearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String text = s;
                adapter.filter(text);
                return false;
            }
        });

        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateProjectActivity.class);
                startActivity(intent);
            }
        });

        rvProjects = view.findViewById(R.id.rvProjects);

        // create the data source
        projects = new ArrayList<>();
        // create the adapter
        adapter = new ProjectsAdapter(getContext(), projects, this);
        // add line between items
        rvProjects.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        // set the adapter on the recycler view
        rvProjects.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvProjects.setLayoutManager(new LinearLayoutManager(getContext()));
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                projects.clear();
                adapter.clear();
                queryProjects();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark,
                android.R.color.holo_orange_light);
        queryProjects();
    }

    protected void queryProjects() {
        ParseQuery<Project> projectQuery = new ParseQuery<Project>(Project.class);
        //updateQuery.include(Update.KEY_USER);
        projectQuery.addDescendingOrder("createdAt");

        projectQuery.findInBackground(new FindCallback<Project>() {
            @Override
            public void done(List<Project> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error with query");
                    e.printStackTrace();
                    return;
                }
                projects.addAll(posts);
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(int i) {
        //Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
        Intent project2description = new Intent(getActivity(), ProjectDetailsActivity.class);
        project2description.putExtra("project", Parcels.wrap(projects.get(i)));
        startActivity(project2description);
    }

}
