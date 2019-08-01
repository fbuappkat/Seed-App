package com.example.kat_app.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kat_app.Adapters.LegendAdapter;
import com.example.kat_app.Models.Project;
import com.example.kat_app.Models.Request;
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
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvAuthor)
    TextView tvAuthor;
    @BindView(R.id.tvDescription)
    TextView tvDescription;
    @BindView(R.id.tvNumInvestors)
    TextView tvInvestors;
    @BindView(R.id.tvNumFollowers)
    TextView tvFollowers;
    @BindView(R.id.btnMedia)
    Button btnMedia;
    @BindView(R.id.tvNumFunds)
    TextView tvFunds;
    @BindView(R.id.btnFollow)
    Button btnFollow;
    @BindView(R.id.btnInvest)
    Button btnInvest;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.tvHandleDetails)
    TextView tvHandle;
    @BindView(R.id.tvPercentEquity)
    TextView tvPercentEquity;
    protected LegendAdapter legendAdapter;
    private RecyclerView rvLegend;
    private JSONArray media;
    private Project project;

    private ArrayList<Request> requests;
    private Project proj;
    FragmentManager fragmentManager;
    public static final String TAG = "ProjectDetailsActivity";


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
            btnFollow.setBackgroundColor(Color.GRAY);
        }

        queryUser();

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
                } else {
                    btnFollow.setBackgroundColor(Color.rgb(249, 138, 97));
                    btnFollow.setText("Follow");

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
                Intent invest = new Intent(ProjectDetailsActivity.this, InvestActivity.class);
                invest.putExtra("project", Parcels.wrap(proj));
                startActivity(invest);
            }
        });

        //invest button
        btnMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent media = new Intent(ProjectDetailsActivity.this, ProjectMediaActivity.class);
                media.putExtra("project", Parcels.wrap(proj));
                startActivity(media);
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

    //create piechart for request breakdown
    public void makePieChart() {
        pcBreakdown.setUsePercentValues(false);
        Description pcDesc = new Description();
        pcDesc.setText("Requested funds breakdown");
        pcBreakdown.setDescription(pcDesc);
        pcBreakdown.setHoleRadius(25);
        pcBreakdown.setTransparentCircleRadius(25);

        List<PieEntry> values = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            values.add(new PieEntry(requests.get(i).getPrice(), requests.get(i).getRequest()));
        }

        PieDataSet pieDataSet = new PieDataSet(values, "<- Requests");
        pieDataSet.setSliceSpace(0f);
        PieData pieData = new PieData(pieDataSet);
        pcBreakdown.getLegend().setEnabled(false);
        pieData.setValueTextSize(20);
        pcBreakdown.setData(pieData);
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
                    tvFunds.setText(Float.toString(getTotalFunds(requests)) + "0/" + Float.toString(getTotal(requests)) + "0");
                    String equity = "0.00";
                    if (proj.getEquity() != null) {
                        equity = Float.toString(proj.getEquity() - getTotalFunds(requests) / getTotal(requests) * proj.getEquity()) + "%";
                    }
                    tvPercentEquity.setText(equity);
                }
                makePieChart();
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

}
