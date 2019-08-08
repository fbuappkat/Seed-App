package com.example.kat_app.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kat_app.Adapters.ChatAdapter;
import com.example.kat_app.Adapters.MessageAdapter;
import com.example.kat_app.Adapters.UserAdapter;
import com.example.kat_app.Models.Chat;
import com.example.kat_app.Models.Message;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageActivity extends AppCompatActivity {

    static final String TAG = ".MessageActivity";

    @BindView(R.id.rvMessage)
    RecyclerView rvMessage;
    @BindView(R.id.tvChat)
    TextView tvChat;
    @BindView(R.id.etMessage)
    EditText etMessage;
    @BindView(R.id.btSend)
    Button btSend;
    @BindView(R.id.ivBack)
    ImageButton ivBack;
    @BindView(R.id.ivMore)
    ImageButton ivMore;

    private ArrayList<Message> mMessages;
    private MessageAdapter mAdapter;
    private ChatAdapter chatAdapter;
    private ParseUser otherUser;
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;

    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        MainActivity.setStatusBar(getWindow());
        ButterKnife.bind(this);

        setBackButton();

        otherUser = Parcels.unwrap(getIntent().getParcelableExtra(OtherUserProfileActivity.class.getSimpleName()));

        if (otherUser == null) {
            otherUser = Parcels.unwrap(getIntent().getParcelableExtra(ChatAdapter.class.getSimpleName()));
        }

        if (otherUser == null) {
            otherUser = Parcels.unwrap(getIntent().getParcelableExtra(UserAdapter.class.getSimpleName()));
        }

        try {
            tvChat.setText(otherUser.fetchIfNeeded().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        startWithCurrentUser();
        myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
    }

    static final int POLL_INTERVAL = 1000; // milliseconds
    Handler myHandler = new Handler();  // android.os.Handler
    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            myHandler.postDelayed(this, POLL_INTERVAL);
        }
    };


    // Get the userId from the cached currentUser object
    void startWithCurrentUser() {
        setupMessagePosting();
    }

    // Create an anonymous user using ParseAnonymousUtils and set sUserId
    void login() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Anonymous login failed: ", e);
                } else {
                    startWithCurrentUser();
                }
            }
        });
    }

    // Setup button event handler which posts the entered message to Parse
    void setupMessagePosting() {
        mMessages = new ArrayList<>();
        mFirstLoad = true;

        final String userId = ParseUser.getCurrentUser().getObjectId();
        mAdapter = new MessageAdapter(MessageActivity.this, mMessages, userId, otherUser);
        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MessageActivity.this);
        linearLayoutManager.setReverseLayout(true);
        rvMessage.setLayoutManager(linearLayoutManager);
        rvMessage.setAdapter(mAdapter);


        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
                //ParseObject message = ParseObject.create("Message");
                //message.put(Message.USER_ID_KEY, userId);
                //message.put(Message.BODY_KEY, data);
                // Using new `Message` Parse-backed model now
                Message message = new Message();
                message.setBody(data);
                String userId = ParseUser.getCurrentUser().getObjectId();
                message.setUserId(userId);
                message.setMessageSender(ParseUser.getCurrentUser());
                message.setMessageReceiver(otherUser);
                message.setReadBy(userId);
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(MessageActivity.this, "Successfully created message on Parse",
                                Toast.LENGTH_SHORT).show();
                        refreshMessages();
                    }
                });

                etMessage.setText(null);

                if (mMessages.size() == 0) {
                    Chat newChat = new Chat();
                    ArrayList<String> users = new ArrayList<>();
                    users.add(otherUser.getObjectId());
                    users.add(ParseUser.getCurrentUser().getObjectId());
                    newChat.setUsers(users);
                    newChat.setFirstMessageCount();
                    newChat.setLastMessageBody(etMessage.getText().toString());
                    newChat.setLastMessagePointer(message);

                    newChat.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Toast.makeText(MessageActivity.this, "Successfully created chat on Parse",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });


                    queryChats(message);
                } else {
                    queryChats(message);
                }
            }
        });
    }


    void refreshMessages() {
        // build first AND condition
        ParseQuery<Message> queryPart1 = ParseQuery.getQuery(Message.class);
        queryPart1.whereEqualTo("sender", ParseUser.getCurrentUser());
        queryPart1.whereEqualTo("receiver", otherUser);

        // build second AND condition
        ParseQuery<Message> queryPart2 = ParseQuery.getQuery(Message.class);
        queryPart2.whereEqualTo("sender", otherUser);
        queryPart2.whereEqualTo("receiver", ParseUser.getCurrentUser());

        // list all queries condition for next step
        List<ParseQuery<Message>> queries = new ArrayList<ParseQuery<Message>>();
        queries.add(queryPart1);
        queries.add(queryPart2);

        // Compose the OR clause
        ParseQuery<Message> innerQuery = ParseQuery.or(queries);
        innerQuery.orderByDescending("createdAt");

        innerQuery.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);

        // Run selection asynchronously
        innerQuery.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        rvMessage.scrollToPosition(0);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }

        });
    }



    //get chats via network request
    protected void queryChats(final Message message) {
        ParseQuery<Chat> chatQuery = new ParseQuery<>(Chat.class);

        ArrayList<String> users = new ArrayList<>();
        users.add(ParseUser.getCurrentUser().getObjectId());
        users.add(otherUser.getObjectId());

        chatQuery.whereContainsAll("users", users);

        chatQuery.findInBackground(new FindCallback<Chat>() {
            @Override
            public void done(List<Chat> userChats, ParseException e) {
                if (e == null) {
                    if (userChats.size() != 0) {
                        Chat chat = userChats.get(0);
                        chat.setLastMessageBody(message.getBody());
                        chat.setLastMessageTime(message.getTime());
                        chat.saveInBackground();
                    } else {
                        Chat newChat = new Chat();
                        ArrayList<String> users = new ArrayList<>();
                        users.add(otherUser.getObjectId());
                        users.add(ParseUser.getCurrentUser().getObjectId());
                        newChat.setUsers(users);
                        newChat.setFirstMessageCount();
                        newChat.setLastMessageTime(message.getTime());
                        newChat.setLastMessageBody(message.getBody());
                        newChat.setLastMessagePointer(message);

                        newChat.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Toast.makeText(MessageActivity.this, "Successfully created chat on Parse",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    e.printStackTrace();
                }
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}

