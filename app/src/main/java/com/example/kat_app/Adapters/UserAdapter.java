package com.example.kat_app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import com.example.kat_app.Models.Project;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

import static com.parse.Parse.getApplicationContext;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private static Context context;
    private List<ParseUser> users;
    private ProjectsAdapter.OnClickListener monClickListener;
    private int request;


    public UserAdapter(Context context, List<ParseUser> users, ProjectsAdapter.OnClickListener onClickListener) {
        this.context = context;
        this.users = users;
        this.monClickListener = onClickListener;
    }

    public UserAdapter(Context context, List<ParseUser> users, int request) {
        this.context = context;
        this.users = users;
        this.request = request;
    }


    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        ParseUser user = users.get(position);
        holder.bind(user, users, position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnClickListener onClickListener;
        private TextView tvName;
        private TextView tvUsername;
        private TextView tvInvestments;
        private TextView tvProjects;
        private ImageView ivImage;
        private ImageView ivProfile;
        private ConstraintLayout userHolder;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvName = itemView.findViewById(R.id.tvName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            userHolder = itemView.findViewById(R.id.userHolder);
        }


        //add in data for specific user's post
        public void bind(final ParseUser user, final List<ParseUser> userList, final int position) {
            tvName.setText(user.get("name").toString());
            tvUsername.setText("@" + user.getUsername());
            ParseFile profileImage = user.getParseFile("profile_image");
            if (profileImage != null) {
                Glide.with(getApplicationContext())
                        .load(profileImage.getUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivImage);
            } else {
                Glide.with(getApplicationContext())
                        .load(R.drawable.default_profile_image)
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivImage);
            }

            if (request == 1) {
                userHolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent message = new Intent(context, MessageActivity.class);
                        message.putExtra(UserAdapter.class.getSimpleName(), Parcels.wrap(user));
                        context.startActivity(message);
                        ((Activity) context).finish();
                    }
                });
            } else {
                userHolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent otherProfile = new Intent(context, OtherUserProfileActivity.class);
                        otherProfile.putExtra("User", Parcels.wrap(userList.get(position)));
                        context.startActivity(otherProfile);
                    }
                });
            }


        }

        @Override
        public void onClick(View v) {
            onClickListener.onClick(getAdapterPosition());
        }


    }

    public interface OnClickListener {
        void onClick(int position);
    }


}