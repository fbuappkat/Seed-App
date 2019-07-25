package com.example.kat_app.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kat_app.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private static Context context;
    private List<ParseUser> users;


    public UserAdapter(Context context, List<ParseUser> users) {
        this.context = context;
        this.users = users;
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        ParseUser user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG,"item count: " + updates.size());
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView tvUsername;
        private TextView tvFollowers;
        private TextView tvProjects;
        private ImageView ivImage;

        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvName = itemView.findViewById(R.id.tvName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvFollowers = itemView.findViewById(R.id.tvFollowers);
            tvProjects = itemView.findViewById(R.id.tvProjects);
            ivImage = itemView.findViewById(R.id.ivImage);

        }


        //add in data for specific user's post
        public void bind(final ParseUser user) {
           tvName.setText(user.get("name").toString());
           tvUsername.setText("@" + user.getUsername());
           tvFollowers.setText("Followers: " + "X");
           tvProjects.setText("Projects: " + "X");
            ParseFile profileImage = user.getParseFile("profile_image");
            if (profileImage != null) {
                Glide.with(context)
                        .load(profileImage.getUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivImage);
            } else {
                Glide.with(context)
                        .load(R.drawable.default_profile_image)
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivImage);
            }

        }


    }


}