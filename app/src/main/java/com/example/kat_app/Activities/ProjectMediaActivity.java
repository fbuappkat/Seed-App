package com.example.kat_app.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.example.kat_app.Adapters.MediaAdapter;
import com.example.kat_app.Models.Project;
import com.example.kat_app.R;

import org.json.JSONArray;
import org.parceler.Parcels;

public class ProjectMediaActivity extends AppCompatActivity {

    protected MediaAdapter mediaAdapter;
    private RecyclerView rvMedia;
    private ImageView ivBack;
    private JSONArray media;
    private Project project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_media);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = (int) Math.round(dm.widthPixels * 0.9);
        int height = (int) Math.round(dm.heightPixels * 0.9);

        getWindow().setLayout(width, height);

        rvMedia = findViewById(R.id.rvMedia);
        project = Parcels.unwrap(getIntent().getParcelableExtra("project"));
        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        media = project.getMedia();

        setupAdapter();

    }


    public void setupAdapter() {
        // create the adapter
        mediaAdapter = new MediaAdapter(ProjectMediaActivity.this, media);
        // add line between items
        rvMedia.addItemDecoration(new DividerItemDecoration(ProjectMediaActivity.this,
                DividerItemDecoration.VERTICAL));
        // set the adapter on the recycler view
        rvMedia.setAdapter(mediaAdapter);
        // set the layout manager on the recycler view
        rvMedia.setLayoutManager(new LinearLayoutManager(ProjectMediaActivity.this));
    }



}
