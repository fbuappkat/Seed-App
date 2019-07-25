package com.example.kat_app.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.example.kat_app.R;

public class ProjectMediaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_media);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = (int) Math.round(dm.widthPixels * 0.9);
        int height = (int) Math.round(dm.heightPixels * 0.7);

        getWindow().setLayout(width, height);

    }
}
