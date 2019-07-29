package com.example.kat_app.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kat_app.Activities.CreateProjectActivity;
import com.example.kat_app.Activities.ProjectDetailsActivity;
import com.example.kat_app.Adapters.ProjectsAdapter;
import com.example.kat_app.Adapters.UserAdapter;
import com.example.kat_app.Models.Project;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ProjectsAdapter.OnClickListener {

    RecyclerView rvProjects;
    private ImageView ivAdd;
    protected List<Project> projects;
    protected ProjectsAdapter adapter;
    protected List<ParseUser> users;
    protected UserAdapter userAdapter;
    private ProgressBar pbLoad;
    private FloatingActionButton fabCreate;
    private Spinner spinnerFilter;
    private Spinner spinnerSearch;
    private TextView tvFilter;
    private boolean onProjects = true;
    RecyclerView rvUsers;
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


        pbLoad = view.findViewById(R.id.pbLoad);
        fabCreate = view.findViewById(R.id.fabCreate);
        spinnerFilter = view.findViewById(R.id.spinnerFilter);
        spinnerSearch = view.findViewById(R.id.spinnerSearch);
        rvUsers = view.findViewById(R.id.rvUsers);
        tvFilter = view.findViewById(R.id.tvFilter);



        //setup spinners
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.filter));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerFilter.setAdapter(spinnerAdapter);

        ArrayAdapter<String> spinnerAdapter2 = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.search));
        spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerSearch.setAdapter(spinnerAdapter2);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] strarr = getResources().getStringArray(R.array.filter);
                switch(position){
                    case 0:
                        queryProjects();
                        break;
                    case 1:
                        queryProjects();
                    default:
                        queryProjectsByCategory(strarr[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSearch.setSelection(0,false);
        spinnerSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        spinnerFilter.setVisibility(View.VISIBLE);
                        tvFilter.setVisibility(View.VISIBLE);
                        rvProjects.setVisibility(View.VISIBLE);
                        rvUsers.setVisibility(View.INVISIBLE);
                        onProjects = true;
                        break;
                    case 1:
                        spinnerFilter.setVisibility(View.INVISIBLE);
                        tvFilter.setVisibility(View.INVISIBLE);
                        rvProjects.setVisibility(View.INVISIBLE);
                        rvUsers.setVisibility(View.VISIBLE);
                        onProjects = false;
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinnerFilter.setVisibility(View.VISIBLE);
                tvFilter.setVisibility(View.VISIBLE);
                onProjects = true;
            }
        });

        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateProjectActivity.class);
                startActivity(intent);
            }
        });
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


        rvProjects = view.findViewById(R.id.rvProjects);
        rvProjects.setVisibility(View.INVISIBLE);
        rvUsers.setVisibility(View.INVISIBLE);


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

        // create the data source
        users = new ArrayList<>();
        // create the adapter
        userAdapter = new UserAdapter(getContext(), users, this);
        // add line between items
        rvUsers.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        // set the adapter on the recycler view
        rvUsers.setAdapter(userAdapter);
        // set the layout manager on the recycler view
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));

        queryProjects();
        queryUsers();

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                if (onProjects) {
                    projects.clear();
                    adapter.clear();
                    pbLoad.setVisibility(View.VISIBLE);
                    rvProjects.setVisibility(View.INVISIBLE);
                    queryProjects();
                } else {
                    queryUsers();
                }
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark,
                android.R.color.holo_orange_light);

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
                projects.clear();
                adapter.clear();
                projects.addAll(posts);
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
                pbLoad.setVisibility(View.INVISIBLE);
                rvProjects.setVisibility(View.VISIBLE);
            }
        });
    }

    protected void queryUsers() {
        ParseQuery<ParseUser> projectQuery = new ParseQuery<ParseUser>(ParseUser.class);
        projectQuery.addDescendingOrder("createdAt");

        projectQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> user, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error with query");
                    e.printStackTrace();
                    return;
                }
                users.clear();
                userAdapter.clear();
                users.addAll(user);
                userAdapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    protected void queryProjectsByCategory(String category) {
        ParseQuery<Project> projectQuery = new ParseQuery<Project>(Project.class);
        //updateQuery.include(Update.KEY_USER);
        projectQuery.whereEqualTo("category", category);

        projectQuery.findInBackground(new FindCallback<Project>() {
            @Override
            public void done(List<Project> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error with query");
                    e.printStackTrace();
                    return;
                }
                projects.clear();
                adapter.clear();
                projects.addAll(posts);
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
                pbLoad.setVisibility(View.INVISIBLE);
                rvProjects.setVisibility(View.VISIBLE);
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
