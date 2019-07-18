package com.example.kat_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kat_app.Activities.UpdateDetailsActivity;
import com.example.kat_app.Models.Update;
import com.example.kat_app.Project;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/* FBU 2019
   UpdatesAdapter updates all items inside an update, including the user, title, caption,
   number of likes and comments, relative time posted, and image. It implements an
   on click listener to open the details activity if a user clicks on a specific post.
 */
public class UpdatesAdapter extends RecyclerView.Adapter<UpdatesAdapter.ViewHolder> {

    private Context context;
    private List<Update> updates;
    private final String TAG = "UpdatesAdapter";
    private final ParseUser currUser = ParseUser.getCurrentUser();
    private static final String KEY_PROFILE_IMAGE = "profile_image";
    private boolean userInFollowList;
    private final static String KEY_FOLLOWERS = "followers";

    public UpdatesAdapter(Context context, List<Update> updates) {
        this.context = context;
        this.updates = updates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_update, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Update update = updates.get(position);
        final ParseObject project = update.getProjectPointer();
        final ParseQuery<Project> projectQuery = new ParseQuery<Project>(Project.class);

        projectQuery.findInBackground(new FindCallback<Project>() {
            @Override
            public void done(List<Project> posts, com.parse.ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error with query");
                    e.printStackTrace();
                    return;
                }
                //TODO FIX ME
                try {
                    Log.d(TAG,"projectID: " + project.getClassName());
                    JSONArray followers = project.fetchIfNeeded().getJSONArray("followers");
                    for (int i = 0; i < followers.length(); i++) {
                        JSONObject jsonobject = followers.getJSONObject(i);
                        String userID = jsonobject.getString("objectId");
                        String currUserID = currUser.getObjectId();
                        Log.d(TAG, "userID: " + userID + " project user id: " + currUserID);
                        if (userID == currUserID) {
                            userInFollowList = true;
                            break;
                        }
                        Log.d(TAG, Boolean.toString(userInFollowList));
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (com.parse.ParseException e1) {
                    e1.printStackTrace();
                }

            }
        });

        //TODO fix this
        if (userInFollowList) {

        }
        holder.bind(update);
    }

    @Override
    public int getItemCount() {
        return updates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvUser;
        private TextView tvUser2;
        private TextView tvCaption;
        private TextView tvRelativeTime;
        private TextView tvNumLikes;
        private ImageButton btnLike;
        private ImageView ivProfileImage;
        private TextView tvNumComments;
        private TextView tvProject;
        private EditText etComment;
        private Button btnAddComment;
        private ImageButton btnGoToComments;
        private TextView tvUserFollows;

        public ViewHolder(View itemView) {
            super(itemView);
           // tvUser2 = itemView.findViewById(R.id.tvCommentsHeader);
            tvUser = itemView.findViewById(R.id.tvEditAccount);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            tvNumLikes = itemView.findViewById(R.id.tvNumLikes);
            tvNumComments = itemView.findViewById(R.id.tvNumComments);
            btnLike = itemView.findViewById(R.id.btnLike);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImageUpdate);
            tvProject = itemView.findViewById(R.id.tvProject);
            etComment = itemView.findViewById(R.id.etAddComment);
            btnAddComment = itemView.findViewById(R.id.btnPostComment);
            btnGoToComments = itemView.findViewById(R.id.btnGoToComments);
            tvUserFollows = itemView.findViewById(R.id.tvUserFollows);
            //add itemView's OnClickListener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // get item position
            int position = getAdapterPosition();
            // make sure the position exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the update at the position, this won't work if the class is static
                Update update = updates.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, UpdateDetailsActivity.class);
                //serialize the update using parceler, use its short name as a key
                intent.putExtra(Update.class.getSimpleName(), Parcels.wrap(update));
                // show the activity
                context.startActivity(intent);
            }
        }

        //add in data for specific user's post
        public void bind(final Update update) {
            try {
                String username = update.getUser().fetchIfNeeded().getString("username");
                tvUser.setText(username);
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
            tvCaption.setText(update.getCaption());
            tvRelativeTime.setText(getRelativeTimeAgo(String.valueOf(update.getCreatedAt())));
            tvNumLikes.setText(Integer.toString(update.getNumLikes()));
            tvNumComments.setText(Integer.toString(update.getNumComments()));
            tvProject.setText(update.getProjectPointer().getObjectId());
            tvUserFollows.setText(userInFollowList ? "true" : "false");

            ParseFile profileImage = update.getUser().getParseFile(KEY_PROFILE_IMAGE);
            if (profileImage != null) {
                Glide.with(context)
                        .load(profileImage.getUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivProfileImage);
            }
            else {
                Glide.with(context)
                        .load(R.drawable.default_profile_image)
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivProfileImage);
            }

            btnAddComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String comment = etComment.getText().toString();
                    update.addComment(comment);
                    etComment.setText("");
                    update.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            if (e != null) {
                                e.printStackTrace();
                                return;
                            }
                        }
                    });
                    notifyDataSetChanged();
                }
            });

            JSONArray v = update.userLikes();
            if (v != null) {
                tvNumLikes.setText(Integer.toString(v.length()));
            } else {
                tvNumLikes.setText("0");
            }
            if(update.isLiked()) {
                btnLike.setImageResource(R.drawable.ufi_heart_active);
            } else {
                btnLike.setImageResource(R.drawable.ufi_heart);
            }
            btnGoToComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // get item position
                    int position = getAdapterPosition();
                    // make sure the position exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // get the update at the position, this won't work if the class is static
                        Update update = updates.get(position);
                        // create intent for the new activity
                        Intent feedToDetails = new Intent(context, UpdateDetailsActivity.class);
                        //serialize the update using parceler, use its short name as a key
                        feedToDetails.putExtra(Update.class.getSimpleName(), Parcels.wrap(update));
                        // show the activity
                        context.startActivity(feedToDetails);
                    }
                }
            });

            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!update.isLiked()) {
                        btnLike.setImageResource(R.drawable.ufi_heart_active);
                        int position = getAdapterPosition();
                        Update update = updates.get(position);
                        int curLikes = update.getNumLikes();
                        //add current user to list of users who liked this post
                        update.likePost(currUser);

                        update.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(com.parse.ParseException e) {
                                if (e != null) {
                                    e.printStackTrace();
                                    return;
                                }
                            }
                        });
                        notifyDataSetChanged();
                    } else {
                        btnLike.setImageResource(R.drawable.ufi_heart);
                        int position = getAdapterPosition();
                        Update update = updates.get(position);
                        int curLikes = update.getNumLikes();
                        //add current user to list of users who liked this post
                        update.unlikePost(currUser);

                        update.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(com.parse.ParseException e) {
                                if (e != null) {
                                    e.printStackTrace();
                                    return;
                                }
                            }
                        });
                        notifyDataSetChanged();
                    }

                }
            });
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        updates.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Update> list) {
        updates.addAll(list);
        notifyDataSetChanged();
    }

    // return how long ago relative to current time update was posted
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }
}
