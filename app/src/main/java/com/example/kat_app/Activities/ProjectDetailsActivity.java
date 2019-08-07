package com.example.kat_app.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
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
import com.example.kat_app.Adapters.LegendAdapter;
import com.example.kat_app.Adapters.UpdatesAdapter;
import com.example.kat_app.Models.Project;
import com.example.kat_app.Models.Request;
import com.example.kat_app.Models.Update;
import com.example.kat_app.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProjectDetailsActivity extends AppCompatActivity {

    @BindView(R.id.pcBreakdown)
    PieChart pcBreakdown;
    @BindView(R.id.tvDetailsName)
    TextView tvName;
    @BindView(R.id.tvAuthor)
    TextView tvAuthor;
    @BindView(R.id.tvDescription)
    TextView tvDescription;
    @BindView(R.id.ivAdd)
    ImageView btnPostFunds;
    @BindView(R.id.tvNumInvestors)
    TextView tvInvestors;
    @BindView(R.id.tvNumFollowers)
    TextView tvFollowers;
    @BindView(R.id.tvNumFunds)
    TextView tvFunds;
    @BindView(R.id.btnFollow)
    Button btnFollow;
    @BindView(R.id.btnInvest)
    Button btnInvest;
    @BindView(R.id.btnCollab)
    Button btnCollab;
    @BindView(R.id.ivBack)
    ImageButton ivBack;
    @BindView(R.id.tvHandleDetails)
    TextView tvHandle;
    @BindView(R.id.tvPercentEquity)
    TextView tvPercentEquity;
    @BindView(R.id.rvFeed)
    RecyclerView rvFeed;
    protected LegendAdapter legendAdapter;
    @BindView(R.id.rvLegend)
    RecyclerView rvLegend;
    @BindView(R.id.svScroll)
    ScrollView svScroll;
    private JSONArray media;
    private Project project;

    private ArrayList<Request> requests;
    private Project proj;
    FragmentManager fragmentManager;
    public static final String TAG = "ProjectDetailsActivity";
    private float totalFunds;
    private float totalNeeded;
    protected UpdatesAdapter adapter;
    protected List<Update> updates = new ArrayList<>();
    private List<PieEntry> values;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        proj = Parcels.unwrap(getIntent().getParcelableExtra("project"));

        ButterKnife.bind(this);
        queryRequests();
        setBackButton();

        //set text views
        tvName.setText(proj.getName());
        tvDescription.setText(proj.getDescription());
        tvInvestors.setText(Integer.toString(proj.getInvestors().length()));
        tvFollowers.setText(Integer.toString(proj.getFollowers().length()));

        if (proj.getFollowers().toString().contains(ParseUser.getCurrentUser().getObjectId())) {
            btnFollow.setBackground(getDrawable(R.drawable.button_grey));
        }

        if (!proj.getCollabs()) {
            btnCollab.setBackground(getDrawable(R.drawable.button_grey));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(ProjectDetailsActivity.this);

        // set the layout manager on the recycler view
        rvFeed.setLayoutManager(layoutManager);
        rvFeed.setHasFixedSize(true);
        // create the adapter
        adapter = new UpdatesAdapter(ProjectDetailsActivity.this, updates);
        // set the adapter on the recycler view
        rvFeed.setAdapter(adapter);

        queryUpdates();

        queryUser();

        BounceTouchListener bounceTouchListener = BounceTouchListener.create(svScroll, R.id.projectHolder,
                new BounceTouchListener.OnTranslateListener() {
                    @Override
                    public void onTranslate(float translation) {
                    }
                }
        );

        svScroll.setOnTouchListener(bounceTouchListener);

        //Follow button
        if (!proj.getFollowers().toString().contains(ParseUser.getCurrentUser().getObjectId())) {
            btnFollow.setText("Follow");
        } else {
            btnFollow.setText("Unfollow");
        }

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!proj.getFollowers().toString().contains(ParseUser.getCurrentUser().getObjectId())) {
                    btnFollow.setBackgroundColor(Color.GRAY);
                    proj.add("followers", ParseUser.getCurrentUser());
                    tvFollowers.setText(Integer.toString(proj.getFollowers().length()));
                    proj.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Toast.makeText(ProjectDetailsActivity.this, "Project followed!", Toast.LENGTH_SHORT).show();
                    btnFollow.setText("Unfollow");
                    btnFollow.setBackground(getDrawable(R.drawable.button_grey));
                } else {
                    btnFollow.setBackgroundColor(Color.rgb(249, 138, 97));
                    btnFollow.setText("Follow");
                    btnFollow.setBackground(getDrawable(R.drawable.button));

                    ArrayList<ParseUser> remove = new ArrayList<>();
                    remove.add(ParseUser.getCurrentUser());
                    proj.removeAll("followers", remove);
                    tvFollowers.setText(Integer.toString(proj.getFollowers().length()));
                    proj.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

        //invest button
        btnInvest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalFunds >= totalNeeded) {
                    Toast.makeText(ProjectDetailsActivity.this, "Project is already fully funded!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent invest = new Intent(ProjectDetailsActivity.this, InvestActivity.class);
                    invest.putExtra("project", Parcels.wrap(proj));
                    startActivity(invest);
                    ProjectDetailsActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        //collab button
        btnCollab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"collabs: "+ proj.getCollabs());
                if (!proj.getCollabs()) {
                    Toast.makeText(ProjectDetailsActivity.this, "The owner of this project isn't looking for collaborators right now.", Toast.LENGTH_SHORT).show();
                //TODO add message
                } else {
                    Intent message = new Intent(ProjectDetailsActivity.this, MessageActivity.class);
                    message.putExtra(OtherUserProfileActivity.class.getSimpleName(), Parcels.wrap(proj.getUser()));
                    startActivity(message);
                    ProjectDetailsActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        btnPostFunds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (proj.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                    // create intent for the new activity
                    Intent detailsToFunds = new Intent(ProjectDetailsActivity.this, AddFundsActivity.class);
                    //pass user as an object
                    detailsToFunds.putExtra("User", Parcels.wrap(proj.getUser()));
                    detailsToFunds.putExtra("project",Parcels.wrap(proj));
                    // show the activity
                    startActivity(detailsToFunds);
                } else {
                    Toast.makeText(ProjectDetailsActivity.this, "You can't post funds for a project you didn't create!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create intent for the new activity
                Intent detailsToProfile = new Intent(ProjectDetailsActivity.this, OtherUserProfileActivity.class);
                //pass user as an object
                detailsToProfile.putExtra("User", Parcels.wrap(proj.getUser()));
                // show the activity
                startActivity(detailsToProfile);
            }
        });

        tvAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create intent for the new activity
                Intent detailsToProfile = new Intent(ProjectDetailsActivity.this, OtherUserProfileActivity.class);
                //pass user as an object
                detailsToProfile.putExtra("User", Parcels.wrap(proj.getUser()));
                // show the activity
                startActivity(detailsToProfile);
            }
        });
    }

    public void makeLegend() {
        // create the adapter
        legendAdapter = new LegendAdapter(ProjectDetailsActivity.this, values);
        // add line between items
        rvLegend.addItemDecoration(new DividerItemDecoration(ProjectDetailsActivity.this,
                DividerItemDecoration.VERTICAL));
        // set the adapter on the recycler view
        rvLegend.setAdapter(legendAdapter);
        // set the layout manager on the recycler view
        rvLegend.setLayoutManager(new LinearLayoutManager(ProjectDetailsActivity.this));
    }

    //create piechart for request breakdown
    public void makePieChart() {
        pcBreakdown.setUsePercentValues(false);
        Description pcDesc = new Description();
        pcDesc.setText("Requested funds breakdown");
        pcBreakdown.setDescription(pcDesc);
        pcBreakdown.setHoleRadius(25);
        pcBreakdown.setTransparentCircleRadius(25);
        pcBreakdown.getLegend().setEnabled(false);
        values = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            values.add(new PieEntry(requests.get(i).getPrice(), requests.get(i).getRequest()));
        }

        PieDataSet pieDataSet = new PieDataSet(values, "<- Requests");
        pieDataSet.setSliceSpace(0f);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(20);
        pcBreakdown.setData(pieData);
        pcBreakdown.getLegend().setEnabled(false);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pcBreakdown.animateXY(1400, 1400);
    }

    //get all requests linked to project
    protected void queryRequests() {
        ParseQuery<Request> projectQuery = new ParseQuery<Request>(Request.class);

        projectQuery.whereEqualTo("project", ParseObject.createWithoutData("Project", proj.getObjectId()));
        projectQuery.addDescendingOrder("createdAt");
        projectQuery.findInBackground(new FindCallback<Request>() {
            @Override
            public void done(List<Request> posts, ParseException e) {
                if (e != null) {
                    Log.e("Query requests", "Error with query");
                    e.printStackTrace();
                    return;
                }
                requests = new ArrayList<>();
                requests.addAll(posts);
                if (requests != null) {
                    totalFunds = getTotalFunds(requests);
                    totalNeeded = getTotal(requests);
                    if (totalFunds >= totalNeeded) {
                        btnInvest.setBackground(getDrawable(R.drawable.button_grey));
                    }
                    tvFunds.setText(Float.toString(getTotalFunds(requests)) + "0/" + Float.toString(getTotal(requests)) + "0");
                    String equity = "0.00%";
                    if (proj.getEquity() != null) {
                        float newEquity = proj.getEquity() - getTotalFunds(requests) / getTotal(requests) * proj.getEquity();
                        if (newEquity > 0) {
                            equity = round(newEquity) + "%";
                        }
                    }
                    tvPercentEquity.setText(equity);
                }
                makePieChart();
                makeLegend();
            }
        });
    }

    //get user associated with project
    protected void queryUser() {
        ParseQuery<ParseUser> projectQuery = new ParseQuery<ParseUser>(ParseUser.class);

        projectQuery.whereEqualTo("objectId", proj.getUser().getObjectId());
        projectQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> posts, ParseException e) {
                if (e != null) {
                    Log.e("Query requests", "Error with query");
                    e.printStackTrace();
                    return;
                }
                if (posts.size() != 0) {
                    ParseUser user = posts.get(0);
                    tvAuthor.setText((CharSequence) user.get("name"));
                    tvHandle.setText("@" + user.getUsername());
                }
            }

        });
    }

    //get total funds requested from project
    private float getTotal(ArrayList<Request> reqs) {
        float tot = 0;
        for (int i = 0; i < reqs.size(); i++) {
            tot += reqs.get(i).getPrice();
        }
        return tot;
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

    //get funds received
    private float getTotalFunds(ArrayList<Request> reqs) {
        float tot = 0;
        for (int i = 0; i < reqs.size(); i++) {
            tot += reqs.get(i).getReceived();
        }
        return tot;
    }

    private float round(float value) {
        return (float) Math.round(value * 100) / 100;
    }

    //get posts via network request
    protected void queryUpdates() {
        final Update.Query updateQuery = new Update.Query();
        updateQuery.whereEqualTo("project", proj);
        updateQuery.getTop().include("user");

        // If app is just opened, get newest 20 posts
        // Else query for older posts

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
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
