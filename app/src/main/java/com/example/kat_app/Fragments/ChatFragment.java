package com.example.kat_app.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kat_app.Activities.AddUpdateActivity;
import com.example.kat_app.Activities.MainActivity;
import com.example.kat_app.Activities.MessageActivity;
import com.example.kat_app.Adapters.ChatAdapter;
import com.example.kat_app.Adapters.MessageAdapter;
import com.example.kat_app.Adapters.UpdatesAdapter;
import com.example.kat_app.Models.Chat;
import com.example.kat_app.Models.Message;
import com.example.kat_app.Models.Update;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/* FBU 2019
   TimelineFragment displays a recycler view with updates on a user's personal projects
   and the projects they've invested in. Users can scroll to refresh posts.
 */
public class ChatFragment extends Fragment {

    @BindView(R.id.rvMessage)
    RecyclerView rvMessage;

    private ArrayList<Chat> chats;
    private ArrayList<ParseUser> otherUsers;
    private ChatAdapter adapter;

    // onCreateView to inflate the view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        rvMessage.setVisibility(View.GONE);
        startWithCurrentUser();
        rvMessage.setVisibility(View.VISIBLE);
        myHandler.postDelayed(mRefreshChatsRunnable, POLL_INTERVAL);
    }

    static final int POLL_INTERVAL = 1000; // milliseconds
    Handler myHandler = new Handler();  // android.os.Handler
    Runnable mRefreshChatsRunnable = new Runnable() {
        @Override
        public void run() {
            queryChats();
            myHandler.postDelayed(this, POLL_INTERVAL);
        }
    };

    // Get the userId from the cached currentUser object
    void startWithCurrentUser() {
        setupChatListing();
    }

    // Setup button event handler which posts the entered message to Parse
    void setupChatListing() {
        // create the data source
        chats = new ArrayList<>();
        otherUsers = new ArrayList<>();
        queryChats();
        // create the adapter
        adapter = new ChatAdapter(getActivity(), chats, ParseUser.getCurrentUser());
        // add line between items
        rvMessage.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        // set the adapter on the recycler view
        rvMessage.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvMessage.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    //get chats via network request
    protected void queryChats() {
        ParseQuery<Chat> chatQuery = new ParseQuery<>(Chat.class);

        ArrayList<String> currUser = new ArrayList<>();
        currUser.add(ParseUser.getCurrentUser().getObjectId());

        chatQuery.whereContainedIn("users", currUser);

        chatQuery.orderByDescending("updatedAt");

        chatQuery.findInBackground(new FindCallback<Chat>() {
            @Override
            public void done(List<Chat> userChats, ParseException e) {
                if (e == null) {
                    chats.clear();
                    chats.addAll(userChats);
                    adapter.notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}

