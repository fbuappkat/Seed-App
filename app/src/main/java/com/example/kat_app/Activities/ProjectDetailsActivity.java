package com.example.kat_app.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kat_app.Project;
import com.example.kat_app.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.parse.ParseUser;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);


        final Project testProj = new Project();
        testProj.setName("Pizza Place");
        testProj.setDescription("I want to make a pizza place in my hometown, I will use exotic ingredients and hire local talent.");
        testProj.setUser(ParseUser.getCurrentUser());
        testProj.setFollowers();

        //init text views
        tvName = findViewById(R.id.tvName);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvDescription = findViewById(R.id.tvDescription);
        tvInvestors = findViewById(R.id.tvInvestors);
        tvFollowers = findViewById(R.id.tvFollowers);
        tvFunds = findViewById(R.id.tvFunds);
        btnFollow = findViewById(R.id.btnFollow);
        btnInvest = findViewById(R.id.btnInvest);

        //set text views
        tvName.setText(testProj.getName());
        tvAuthor.setText("By: " + testProj.getUser().get("name") + " (@" + testProj.getUser().getUsername() + ")");
        tvDescription.setText(testProj.getDescription());
        tvInvestors.setText("Investors: " + "0");
        tvFollowers.setText("Followers: " + "0");
        tvFunds.setText("Funds: " + "0" + "/" + "80.0");


        //init piechart and add data
        pcBreakdown = findViewById(R.id.pcBreakdown);
        pcBreakdown.setUsePercentValues(false);
        Description pcDesc = new Description();
        pcDesc.setText("Requested funds breakdown");
        pcBreakdown.setDescription(pcDesc);
        pcBreakdown.setHoleRadius(25);
        pcBreakdown.setTransparentCircleRadius(25);
        List<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(30,"Ingredients"));
        values.add(new PieEntry(50,"Employees"));
        PieDataSet pieDataSet = new PieDataSet(values, "<- Requests");
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(20);
        pcBreakdown.setData(pieData);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pcBreakdown.animateXY(1400, 1400);

        //Follow button
        //final String followers = testProj.getFollowers().toString();
        if (testProj.getFollowers().contains(ParseUser.getCurrentUser())){
            btnFollow.setText("Unfollow");
        } else {
            btnFollow.setText("Follow");
        }
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!testProj.getFollowers().contains(ParseUser.getCurrentUser())){
                    testProj.addFollower(ParseUser.getCurrentUser());
                    Toast.makeText(ProjectDetailsActivity.this, "Project followed!", Toast.LENGTH_SHORT).show();
                    btnFollow.setText("Unfollow");
                } else {
                    testProj.removeFollower(ParseUser.getCurrentUser());
                    btnFollow.setText("Follow");
                }
            }
        });
    }

}
