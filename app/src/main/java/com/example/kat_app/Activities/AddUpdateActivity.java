package com.example.kat_app.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.kat_app.Fragments.FeedFragment;
import com.example.kat_app.Models.Update;
import com.example.kat_app.R;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class AddUpdateActivity extends AppCompatActivity {

    private Button btnAddUpdate;
    private EditText etUpdate;
    private final String TAG = "Add Update Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update);

        btnAddUpdate = findViewById(R.id.btnAddUpdate);
        etUpdate = findViewById(R.id.etUpdateCaption);

        btnAddUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postUpdate(etUpdate.getText().toString());
            }
        });
    }

    private void postUpdate(String update) {
        Update newUpdate = new Update();
        newUpdate.setCaption(update);
        //update.setUser(parseUser);
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
