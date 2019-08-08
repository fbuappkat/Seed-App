package com.example.kat_app.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.kat_app.Activities.MainActivity;
import com.example.kat_app.Activities.NewMessageActivity;
import com.example.kat_app.Adapters.ChatAdapter;
import com.example.kat_app.Models.Chat;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
    private ImageButton ivNewMessage;

    private ArrayList<Chat> chats;
    private ArrayList<ParseUser> otherUsers;
    private ChatAdapter adapter;

    public static ChatFragment newInstance(int page, String title) {
        ChatFragment fragmentChat = new ChatFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentChat.setArguments(args);
        return fragmentChat;
    }

    // onCreateView to inflate the view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        startWithCurrentUser();

        ivNewMessage = MainActivity.ivNewMessage;

        ivNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewMessageActivity.class);
                startActivity(intent);
            }
        });
    }


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
        // set the layout manager on the recycler view
        rvMessage.setLayoutManager(new LinearLayoutManager(getContext()));
        // set the adapter on the recycler view
        rvMessage.setAdapter(adapter);
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
}

