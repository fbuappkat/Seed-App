package com.example.kat_app.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kat_app.Project;
import com.example.kat_app.R;
import com.example.kat_app.Request;
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

public class ProjectDetailsActivity extends AppCompatActivity {

    private PieChart pcBreakdown;
    private TextView tvName;
    private TextView tvAuthor;
    private TextView tvDescription;
    private TextView tvInvestors;
    private TextView tvFollowers;
    private TextView tvFunds;
    private Button btnFollow;
    private Button btnInvest;
    private ArrayList<Request> requests;
    private Project proj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        proj = Parcels.unwrap(getIntent().getParcelableExtra("project"));

        //init text views
        tvName = findViewById(R.id.tvName);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvDescription = findViewById(R.id.tvDescription);
        tvInvestors = findViewById(R.id.tvInvestors);
        tvFollowers = findViewById(R.id.tvFollowers);
        tvFunds = findViewById(R.id.tvFunds);
        btnFollow = findViewById(R.id.btnFollow);
        btnInvest = findViewById(R.id.btnInvest);
        queryRequests();


        //set text views
        tvName.setText(proj.getName());
        //Todo figure out how to get user object
        tvAuthor.setText("By: " + "Name" + " (@" + "Username" + ")");
        tvDescription.setText(proj.getDescription());
        tvInvestors.setText("Investors: " + proj.getInvestors().length());
        tvFollowers.setText("Followers: " + proj.getFollowers().length());


        //Follow button

        if (!proj.getFollowers().toString().contains(ParseUser.getCurrentUser().getObjectId())){
            btnFollow.setText("Follow");
        } else {
            btnFollow.setText("Unfollow");
        }

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!proj.getFollowers().toString().contains(ParseUser.getCurrentUser().getObjectId())){
                    proj.add("followers", ParseUser.getCurrentUser());
                    tvFollowers.setText("Followers: " + (proj.getFollowers().length()));
                    proj.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null){
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
                            if (e != null){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    public void makePieChart(){
        pcBreakdown = findViewById(R.id.pcBreakdown);
        pcBreakdown.setUsePercentValues(false);
        Description pcDesc = new Description();
        pcDesc.setText("Requested funds breakdown");
        pcBreakdown.setDescription(pcDesc);
        pcBreakdown.setHoleRadius(25);
        pcBreakdown.setTransparentCircleRadius(25);
        List<PieEntry> values = new ArrayList<>();
        for(int i = 0; i < requests.size(); i++){
            values.add(new PieEntry(requests.get(i).getPrice(), requests.get(i).getRequest()));
        }
        PieDataSet pieDataSet = new PieDataSet(values, "<- Requests");
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(20);
        pcBreakdown.setData(pieData);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pcBreakdown.animateXY(1400, 1400);
    }

    protected void queryRequests() {
        ParseQuery<Request> projectQuery = new ParseQuery<Request>(Request.class);

        projectQuery.whereEqualTo("project", ParseObject.createWithoutData("Project",proj.getObjectId()));
        projectQuery.addDescendingOrder("createdAt");
        projectQuery.findInBackground(new FindCallback<Request>() {
            @Override
            public void done(List<Request> posts, ParseException e) {
                if (e != null) {
                    Log.e("Query requests","Error with query");
                    e.printStackTrace();
                    return;
                }
                requests = new ArrayList<>();
                requests.addAll(posts);
                tvFunds.setText("Funds: " + "0" + "/" + getTotal(requests));
                makePieChart();
            }
        });
    }

    private float getTotal(ArrayList<Request> reqs){
        float tot = 0;
        for (int i = 0; i < reqs.size(); i++){
            tot += reqs.get(i).getPrice();
        }
        return tot;
    }

}
