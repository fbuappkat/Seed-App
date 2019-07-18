package com.example.kat_app.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.kat_app.Fragments.FeedFragment;
import com.example.kat_app.Models.Update;
import com.example.kat_app.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class AddUpdateActivity extends AppCompatActivity {

    private Button btnAddUpdate;
    private EditText etUpdate;
    private final String TAG = "Add Update Activity";
    private Spinner sProjectChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update);

        btnAddUpdate = findViewById(R.id.ivAddUpdate);
        etUpdate = findViewById(R.id.etUpdateCaption);
        sProjectChoice = findViewById(R.id.sProjectChoice);
        final ArrayAdapter<String> projectAdapter = new ArrayAdapter<String>(AddUpdateActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.projects));

        projectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sProjectChoice.setAdapter(projectAdapter);

        btnAddUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caption = etUpdate.getText().toString();
                ParseUser currUser = ParseUser.getCurrentUser();
                //TODO fix get adapter position
                //String project = projectAdapter.getPosition();
                //TODO make this get the real project
                String project = "test";
                postUpdate(caption, currUser, project);
            }
        });

        etUpdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    etUpdate.setText("");
                else
                    etUpdate.setText("What's new with your project?");
            }
        });
    }

    private void postUpdate(String update, ParseUser currentUser, String project) {
        Update newUpdate = new Update();
        newUpdate.setCaption(update);
        newUpdate.setUser(currentUser);
        newUpdate.setProject(project);
        newUpdate.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Error while saving");
                    e.printStackTrace();
                    return;
                }
                Log.d(TAG, "Success!");
                etUpdate.setText("");
            }
        });
    }
}
