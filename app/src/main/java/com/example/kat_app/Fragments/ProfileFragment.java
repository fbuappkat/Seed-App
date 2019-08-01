package com.example.kat_app.Fragments;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kat_app.Activities.EditProfileActivity;
import com.example.kat_app.Activities.ManageAccountActivity;
import com.example.kat_app.Adapters.InvestedProjectsAdapter;
import com.example.kat_app.Adapters.UserProjectAdapter;
import com.example.kat_app.Models.Balance;
import com.example.kat_app.Models.Equity;
import com.example.kat_app.Models.Project;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";

    private ImageView ivProfileImage;
    private TabLayout tabLayout;
    private TextView tvName;
    private TextView tvUsername;
    private TextView tvBalanceCount;
    private TextView tvLocation;
    private TextView tvProjectsCount;
    private TextView tvInvestmentsCount;
    private TextView tvBio;
    private ImageButton ivSettings;
    private ImageButton ivEdit;
    private Balance balance;
    private UserProjectAdapter userProjectAdapter;
    private InvestedProjectsAdapter investedProjectsAdapter;
    private List<Project> userProjects;
    private List<Equity> investedProjects;
    private RecyclerView rvProjects;
    private RecyclerView rvInvested;

    private static final String KEY_NAME = "name";
    private static final String KEY_PROFILE_IMAGE = "profile_image";
    private static final String KEY_BIO = "bio";
    private static final String KEY_LOCATION = "location";
    private static final int EDIT_PROFILE_CODE = 10001;
    private static final int MANAGE_ACCOUNT_CODE = 10002;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userProjects = new ArrayList<>();
        investedProjects = new ArrayList<>();

        try {
            setProfileInfo(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setEditAccountButton(view);
        setSettingsButton(view);
    }

    private void setProfileInfo(View view) throws IOException {
        // Find references for the views
        tvName = view.findViewById(R.id.tvName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvProjectsCount = view.findViewById(R.id.tvProjectsCount);
        tvInvestmentsCount = view.findViewById(R.id.tvInvestmentsCount);
        ivProfileImage = view.findViewById(R.id.ivProfileImageUpdate);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvBalanceCount = view.findViewById(R.id.tvBalanceCount);
        tvBio = view.findViewById(R.id.tvBio);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        rvProjects = view.findViewById(R.id.rvProjects);
        rvInvested = view.findViewById(R.id.rvInvested);
        tabLayout = view.findViewById(R.id.portfolioTabLayout);

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


        ParseUser currUser = ParseUser.getCurrentUser();

        tvName.setText(currUser.getString(KEY_NAME));
        tvUsername.setText("@" + currUser.getUsername());
        queryBalance(currUser);
        queryProjects();
        queryInvested();
        queryInvestments();
        tvBio.setText(currUser.getString(KEY_BIO));
        tvLocation.setText(setLocation(currUser.getParseGeoPoint(KEY_LOCATION)));

        ParseFile profileImage = currUser.getParseFile(KEY_PROFILE_IMAGE);
        if (profileImage != null) {
            Glide.with(getContext())
                    .load(profileImage.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        } else {
            Glide.with(getContext())
                    .load(R.drawable.default_profile_image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }
    }

    //get balance from user pointer and set balanceHolder
    protected void queryBalance(ParseUser currUser) {
        final ParseQuery<Balance> balanceQuery = new ParseQuery<>(Balance.class);

        balanceQuery.whereEqualTo("user", currUser);
        balanceQuery.findInBackground(new FindCallback<Balance>() {
            @Override
            public void done(List<Balance> accounts, ParseException e) {
                if (e != null) {
                    Log.e("Query requests", "Error with query");
                    e.printStackTrace();
                    return;
                }

                balance = accounts.get(0);

                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                tvBalanceCount.setText(formatter.format(round(balance.getAmount())));
            }
        });
    }

    private void setEditAccountButton(View view) {
        // Find reference for the view
        ivEdit = view.findViewById(R.id.ivEdit);

        // Set on-click listener for for image view to launch edit account activity
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivityForResult(intent, EDIT_PROFILE_CODE);
                getActivity().overridePendingTransition(R.anim.slide_up, R.anim.do_nothing);
            }
        });
    }

    private void setSettingsButton(View view) {
        // Find reference for the view
        ivSettings = view.findViewById(R.id.ivSettings);

        // Set up the settings button to open manage account fragment
        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ManageAccountActivity.class);
                startActivityForResult(intent, MANAGE_ACCOUNT_CODE);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private String setLocation(ParseGeoPoint currLocation) throws IOException {
        if (currLocation == null) {
            return "No Location";
        }
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        addresses = geocoder.getFromLocation(currLocation.getLatitude(), currLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();

        return state + ", " + country;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    private float round(float value) {
        return (float) Math.round(value * 100) / 100;
    }

    protected void queryProjects() {
        ParseQuery<Project> projectQuery = new ParseQuery<Project>(Project.class);
        projectQuery.whereEqualTo("author", ParseUser.getCurrentUser());

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
                userProjectAdapter = new UserProjectAdapter(getActivity(), userProjects);
                // set the adapter on the recycler view
                rvProjects.setAdapter(userProjectAdapter);
                // set the layout manager on the recycler view
                rvProjects.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });
    }

    protected void queryInvested() {
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
                    if (project.getInvestors().toString().contains(ParseUser.getCurrentUser().getObjectId())) {
                        count++;
                    }
                }
                tvInvestmentsCount.setText(Integer.toString(count));
            }
        });
    }

    protected void queryInvestments() {
        ParseQuery<Equity> equityQuery = new ParseQuery<>(Equity.class);

        equityQuery.whereEqualTo("investor", ParseUser.getCurrentUser());
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
                investedProjectsAdapter = new InvestedProjectsAdapter(getActivity(), investedProjects);
                // set the adapter on the recycler view
                rvInvested.setAdapter(investedProjectsAdapter);
                // set the layout manager on the recycler view
                // add line between items
                rvInvested.addItemDecoration(new DividerItemDecoration(getContext(),
                        DividerItemDecoration.VERTICAL));
                rvInvested.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });
    }
}

