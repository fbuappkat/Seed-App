package com.example.kat_app.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.kat_app.Adapters.UserAdapter;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewMessageActivity extends AppCompatActivity {

    public static final String TAG = "NewMessage";

    @BindView(R.id.bCancel)
    Button bCancel;
    @BindView(R.id.rvUsers)
    RecyclerView rvUsers;

    private List<ParseUser> users;
    private UserAdapter userAdapter;
    private ParseUser currUser = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        ButterKnife.bind(this);
        MainActivity.setStatusBar(getWindow());
        setCancelButton();
        loadUsers();
    }

    private void loadUsers() {
        // create the data source
        users = new ArrayList<>();
        // create the adapter
        userAdapter = new UserAdapter(this, users, 1);
        // add line between items
        rvUsers.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        // set the adapter on the recycler view
        rvUsers.setAdapter(userAdapter);
        // set the layout manager on the recycler view
        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        queryUsers();
    }

    protected void queryUsers() {
        ParseQuery<ParseUser> userQuery = new ParseQuery<ParseUser>(ParseUser.class);
        userQuery.addDescendingOrder("createdAt");

        userQuery.whereNotEqualTo("name", currUser.getString("name"));

        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> user, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error with query");
                    e.printStackTrace();
                    return;
                }
                users.clear();
                userAdapter.clear();
                users.addAll(user);
                userAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setCancelButton() {
        // Set on-click listener for for image view to launch edit account activity
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.do_nothing, R.anim.slide_down);
    }
}
