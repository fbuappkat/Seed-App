package com.example.kat_app.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.baoyz.widget.PullRefreshLayout;
import com.example.kat_app.Activities.CreateProjectActivity;
import com.example.kat_app.Activities.MainActivity;
import com.example.kat_app.Activities.MapActivity;
import com.example.kat_app.Activities.ProjectDetailsActivity;
import com.example.kat_app.Activities.SearchUserActivity;
import com.example.kat_app.Adapters.ProjectsAdapter;
import com.example.kat_app.Models.Project;
import com.example.kat_app.R;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.tuann.floatingactionbuttonexpandable.FloatingActionButtonExpandable;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class HomeFragment extends Fragment implements ProjectsAdapter.OnClickListener {

    RecyclerView rvProjects;
    protected List<Project> projects;
    protected ProjectsAdapter adapter;
    protected List<ParseUser> users;
    private ProgressBar pbLoad;
    private FloatingActionButtonExpandable fabCreate;
    private ImageButton ivMap;
    private ImageButton ivSearchUsers;
    protected PullRefreshLayout swipeContainer;
    private BubbleNavigationConstraintView topNav;


    public static final String TAG = "HomeFragment";

    public static HomeFragment newInstance(int page, String title) {
        HomeFragment fragmentHome = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentHome.setArguments(args);
        return fragmentHome;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivSearchUsers = MainActivity.ivSearchUsers;
        ivSearchUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent project2usersearch = new Intent(getActivity(), SearchUserActivity.class);
                startActivity(project2usersearch);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        topNav = view.findViewById(R.id.topNav);
        pbLoad = MainActivity.pbLoad;
        pbLoad.setIndeterminate(true);
        fabCreate = view.findViewById(R.id.fabCreate);
        fabCreate.setIconActionButton(R.drawable.ic_add_project);
        fabCreate.setTextColor(ContextCompat.getColor(getContext(), R.color.kat_white));
        fabCreate.setBackgroundButtonColor(ContextCompat.getColor(getContext(), R.color.kat_orange_1));
        fabCreate.setContent("Add A New Project");
        fabCreate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        fabCreate.setTypeface(ResourcesCompat.getFont(getContext(), R.font.proximanova_regular));
        ivMap = MainActivity.ivMap;

        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent map = new Intent(getActivity(), MapActivity.class);
                startActivity(map);
                getActivity().overridePendingTransition(R.anim.slide_up, R.anim.do_nothing);
            }
        });

        topNav.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                switch (position) {
                    case 0:
                        queryProjects();
                        break;
                    case 1:
                        queryProjectsByCategory("Technology");
                        break;
                    case 2:
                        queryProjectsByCategory("Health");
                        break;
                    case 3:
                        queryProjectsByCategory("Education");
                        break;
                    case 4:
                        queryProjectsByCategory("Food/Drink");
                        break;
                    case 5:
                        queryProjectsByCategory("Arts");
                        break;
                }
            }
        });


        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateProjectActivity.class);
                startActivity(intent);
            }
        });

        rvProjects = view.findViewById(R.id.rvProjects);
        rvProjects.setVisibility(View.INVISIBLE);

        rvProjects.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    fabCreate.collapse(true);
                } else {
                    fabCreate.expand(true);
                }
            }
        });

        // create the data source
        projects = new ArrayList<>();
        // create the adapter
        adapter = new ProjectsAdapter(getContext(), projects, this);
        // add line between items
        // set the adapter on the recycler view
        rvProjects.setAdapter(new ScaleInAnimationAdapter(adapter));
        // set the layout manager on the recycler view
        rvProjects.setLayoutManager(new LinearLayoutManager(getContext()));

        setupSwipeRefreshing(view);

        queryProjects();
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
                pbLoad.setVisibility(View.VISIBLE);
                int position = topNav.getCurrentActiveItemPosition();
                switch (position) {
                    case 0:
                        queryProjects();
                        rvProjects.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        queryProjectsByCategory("Technology");
                        rvProjects.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        queryProjectsByCategory("Health");
                        rvProjects.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        queryProjectsByCategory("Education");
                        rvProjects.setVisibility(View.INVISIBLE);
                        break;
                    case 4:
                        queryProjectsByCategory("Food/Drink");
                        rvProjects.setVisibility(View.INVISIBLE);
                        break;
                    case 5:
                        queryProjectsByCategory("Arts");
                        rvProjects.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });
        swipeContainer.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);
        swipeContainer.setColor(getResources().getColor(R.color.kat_grey_7));
    }

    protected void queryProjects() {
        ParseQuery<Project> projectQuery = new ParseQuery<Project>(Project.class);
        projectQuery.addDescendingOrder("createdAt");
        projectQuery.findInBackground(new FindCallback<Project>() {
            @Override
            public void done(List<Project> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with query");
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

    protected void queryProjectsByCategory(String category) {
        ParseQuery<Project> projectQuery = new ParseQuery<Project>(Project.class);
        //updateQuery.include(Update.KEY_USER);
        projectQuery.whereEqualTo("category", category);

        projectQuery.findInBackground(new FindCallback<Project>() {
            @Override
            public void done(List<Project> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with query");
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
        Intent project2description = new Intent(getActivity(), ProjectDetailsActivity.class);
        project2description.putExtra("project", Parcels.wrap(projects.get(i)));
        startActivity(project2description);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


}
