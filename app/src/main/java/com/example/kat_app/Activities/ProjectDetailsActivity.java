package com.example.kat_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kat_app.Models.Project;
import com.example.kat_app.R;
import com.example.kat_app.Models.Request;
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
    @BindView(R.id.tvInvestors)
    TextView tvInvestors;
    @BindView(R.id.tvFollowers)
    TextView tvFollowers;
    @BindView(R.id.tvFunds)
    TextView tvFunds;
    @BindView(R.id.btnFollow)
    Button btnFollow;
    @BindView(R.id.btnInvest)
    Button btnInvest;
    @BindView(R.id.btnMoreDetails)
    Button btnMore;

    private ArrayList<Request> requests;
    private Project proj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        proj = Parcels.unwrap(getIntent().getParcelableExtra("project"));

        ButterKnife.bind(this);
        queryRequests();


        //set text views
        tvName.setText(proj.getName());
        tvDescription.setText(proj.getDescription());
        tvInvestors.setText("Investors: " + proj.getInvestors().length());
        tvFollowers.setText("Followers: " + proj.getFollowers().length());

        queryUser();


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
                    proj.add("followers", ParseUser.getCurrentUser());
                    tvFollowers.setText("Followers: " + (proj.getFollowers().length()));
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
                    btnFollow.setText("Follow");

                    ArrayList<ParseUser> remove = new ArrayList<>();
                    remove.add(ParseUser.getCurrentUser());
                    proj.removeAll("followers", remove);
                    tvFollowers.setText("Followers: " + (proj.getFollowers().length()));
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

        //moredetails button
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moreDetails = new Intent(ProjectDetailsActivity.this, MoreDetailsActivity.class);
                moreDetails.putExtra("project", Parcels.wrap(proj));
                startActivity(moreDetails);
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
        PieData pieData = new PieData(pieDataSet);
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
                    tvFunds.setText("Funds: " + getTotalFunds(requests) + "/" + getTotal(requests));
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
                    tvAuthor.setText("By: " + user.get("name") + " (@" + user.getUsername() + ")");
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

    //get funds received
    private float getTotalFunds(ArrayList<Request> reqs) {
        float tot = 0;
        for (int i = 0; i < reqs.size(); i++) {
            tot += reqs.get(i).getReceived();
        }
        return tot;
    }

}
