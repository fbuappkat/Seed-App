package com.example.kat_app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kat_app.Activities.MessageActivity;
import com.example.kat_app.Activities.OtherUserProfileActivity;
import com.example.kat_app.Models.Chat;
import com.example.kat_app.Models.Message;
import com.example.kat_app.Models.Project;
import com.example.kat_app.Models.Update;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final String TAG = "UpdatesAdapter";
    private Context context;
    private List<Chat> chats;
    private ParseUser currUser = ParseUser.getCurrentUser();

    private static final String KEY_PROFILE_IMAGE = "profile_image";

    public ChatAdapter() {
    }

    public ChatAdapter(Context context, List<Chat> chats) {
        this.context = context;
        this.chats = chats;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_private, parent, false);
        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder viewHolder, int position) {
        Chat chat =  chats.get(position);

        ParseUser otherUser = chat.getOtherUsers(currUser).get(0);

        String name = null;
        try {
            name = otherUser.fetchIfNeeded().getString("name");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String body = chat.getLastMessageBody();
        String time = chat.getLastMessageTime();
        viewHolder.bind(name, body, time, otherUser);
    }

    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvName;
        private TextView tvBody;
        private TextView tvTime;
        private ImageView ivProfileImage;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvName = itemView.findViewById(R.id.tvName);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
        }


        //add in data for specific user's post
        public void bind(String name, String body, String time, final ParseUser otherUser) {
            tvName.setText(name);
            tvBody.setText(body);
            tvTime.setText(time);

            Glide.with(context)
                        .load(otherUser.getParseFile("profile_image").getUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivProfileImage);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent message = new Intent(context, MessageActivity.class);
                    message.putExtra(ChatAdapter.class.getSimpleName(), Parcels.wrap(otherUser));
                    ((Activity) context).startActivityForResult(message, 1);
                }
            });
        }

        @Override
        public void onClick(View v) {
        }


    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        chats.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Chat> list) {
        chats.addAll(list);
        notifyDataSetChanged();
    }
}
