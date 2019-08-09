package com.example.kat_app.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.kat_app.Adapters.ProjectsAdapter;
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

public class SearchUserActivity extends AppCompatActivity {

    @BindView(R.id.ivBack)
    ImageButton ivBack;
    @BindView(R.id.rvUsers)
    RecyclerView rvUsers;

    protected UserAdapter userAdapter;
    protected List<ParseUser> users;

    public static final String TAG = "SearchUserActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        ButterKnife.bind(this);

        MainActivity.setStatusBar(getWindow());
        setBackButton();

        // create the data source
        users = new ArrayList<>();
        // create the adapter
        userAdapter = new UserAdapter(this, users, new ProjectsAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {

            }
        });
        // set the layout manager on the recycler view
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        // set the adapter on the recycler view
        rvUsers.setAdapter(userAdapter);
        queryUsers();
    }

    protected void queryUsers() {
        ParseQuery<ParseUser> projectQuery = new ParseQuery<ParseUser>(ParseUser.class);
        projectQuery.addDescendingOrder("createdAt");
        projectQuery.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());

        projectQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with query");
                    e.printStackTrace();
                    return;
                }
//                for (int  i = 0; i < user.size(); i++) {
//                    if (user.get(i).getParseObject("follower") == null) {
//                        final Followers follows = new Followers();
//                        JSONArray empty = new JSONArray();
//                        follows.setFollowers(empty);
//                        follows.setUser(user.get(i));
//                        follows.saveInBackground();
//                    }
//                }

                users.clear();
                userAdapter.clear();
                users.addAll(user);
                userAdapter.notifyDataSetChanged();
            }
        });
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
