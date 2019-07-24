package com.example.kat_app.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kat_app.Models.Message;
import com.example.kat_app.R;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<Message> mMessageList;
    private String mUserId;
    private ParseUser otherUser;

    public ChatAdapter(Context context, List<Message> messageList, String userId, ParseUser otherUser) {
        mContext = context;
        mMessageList = messageList;
        this.mUserId = userId;
        this.otherUser = otherUser;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);

        if (message.getMessageSender().getObjectId().equals(mUserId)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message =  mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.tvSenderBody);
            timeText = (TextView) itemView.findViewById(R.id.tvSenderTime);
        }

        void bind(Message message) {
            messageText.setText(message.getBody());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getTime());
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.tvReceivedBody);
            timeText = (TextView) itemView.findViewById(R.id.tvReceivedTime);
            nameText = (TextView) itemView.findViewById(R.id.tvReceivedName);
            profileImage = (ImageView) itemView.findViewById(R.id.ivReceivedProfileImage);
        }

        void bind(Message message) {
            messageText.setText(message.getBody());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getTime());
            try {
                nameText.setText(message.getMessageSender().fetchIfNeeded().getUsername());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Insert the profile image from the URL into the ImageView.
            Glide.with(mContext).load(otherUser.getParseFile("profile_image").getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage);
        }
    }
}