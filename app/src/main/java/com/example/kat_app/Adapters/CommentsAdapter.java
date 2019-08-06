package com.example.kat_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import com.example.kat_app.Activities.OtherUserProfileActivity;
import com.example.kat_app.Activities.UpdateDetailsActivity;
import com.example.kat_app.Fragments.ProfileFragment;
import com.example.kat_app.Models.Comment;
import com.example.kat_app.Models.Update;
import com.example.kat_app.R;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/* FBU Android 2019
 * TweetAdapter controls the RecyclerView of tweets displayed in the feed,
 * updating them with user's name, screenname, time tweeted, and tweet body text.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private List<Comment> comments;
    private Context context;

    private final ParseUser currUser = ParseUser.getCurrentUser();
    private static final String KEY_PROFILE_IMAGE = "profile_image";

    // pass in the Tweets array in the constructor
    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentsAdapter.ViewHolder(view);
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        // populate the views according to this data
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvUser;
        private TextView tvComment;
        private TextView tvRelativeTime;
        private TextView tvNumLikes;
        private LikeButton btnLike;
        private ImageView ivProfileImage;

        public ViewHolder(View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            tvNumLikes = itemView.findViewById(R.id.tvNumLikes);
            btnLike = itemView.findViewById(R.id.btnLike);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
        }

        @Override
        public void onClick(View v) {
        }

        //add in data for specific user's post
        public void bind(final Comment comment) {

            tvUser.setText(comment.getUser().getUsername());
            tvComment.setText(comment.getComment());
            tvRelativeTime.setText(getRelativeTimeAgo(String.valueOf(comment.getCreatedAt())));
            if (comment.getNumLikes() == 0) {
                tvNumLikes.setVisibility(View.GONE);
            } else if (comment.getNumLikes() == 1) {
                tvNumLikes.setVisibility(View.VISIBLE);
                tvNumLikes.setText(comment.getNumLikes() + " like");
            } else {
                tvNumLikes.setVisibility(View.VISIBLE);
                tvNumLikes.setText(comment.getNumLikes() + " likes");
            }

            ParseFile profileImage = comment.getUser().getParseFile(KEY_PROFILE_IMAGE);

            if (profileImage != null) {
                Glide.with(context)
                        .load(profileImage.getUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivProfileImage);
            } else {
                Glide.with(context)
                        .load(R.drawable.default_profile_image)
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivProfileImage);
            }

            if (comment.isLiked()) {
                btnLike.setLiked(true);
            } else {
                btnLike.setLiked(false);
            }

            btnLike.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    btnLike.setLiked(true);
                    int position = getAdapterPosition();
                    Comment comment = comments.get(position);
                    int curLikes = comment.getNumLikes();
                    //add current user to list of users who liked this post
                    comment.likePost(currUser);

                    comment.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            if (e != null) {
                                e.printStackTrace();
                                return;
                            }
                        }
                    });
                    if (comment.getNumLikes() == 0) {
                        tvNumLikes.setVisibility(View.GONE);
                    } else if (comment.getNumLikes() == 1) {
                        tvNumLikes.setVisibility(View.VISIBLE);
                        tvNumLikes.setText(comment.getNumLikes() + " like");
                    } else {
                        tvNumLikes.setVisibility(View.VISIBLE);
                        tvNumLikes.setText(comment.getNumLikes() + " likes");
                    }
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    btnLike.setLiked(false);
                    int position = getAdapterPosition();
                    Comment comment = comments.get(position);
                    int curLikes = comment.getNumLikes();
                    //add current user to list of users who liked this post
                    comment.unlikePost(currUser);

                    comment.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            if (e != null) {
                                e.printStackTrace();
                                return;
                            }
                        }
                    });
                    if (comment.getNumLikes() == 0) {
                        tvNumLikes.setVisibility(View.GONE);
                    } else if (comment.getNumLikes() == 1) {
                        tvNumLikes.setVisibility(View.VISIBLE);
                        tvNumLikes.setText(comment.getNumLikes() + " like");
                    } else {
                        tvNumLikes.setVisibility(View.VISIBLE);
                        tvNumLikes.setText(comment.getNumLikes() + " likes");
                    }
                }
            });

            if (tvUser.getText().toString().equals(currUser.getUsername())) {
                tvUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            } else {
                tvUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // get item position
                        int position = getAdapterPosition();
                        // get the update at the position, this won't work if the class is static
                        Comment comment = comments.get(position);
                        // create intent for the new activity
                        Intent feedToProfile = new Intent(context, OtherUserProfileActivity.class);
                        //pass user as an object
                        feedToProfile.putExtra("User", Parcels.wrap(comment.getUser()));
                        // show the activity
                        context.startActivity(feedToProfile);
                    }
                });
            }
        }
    }



    // return how long ago relative to current time tweet was sent
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

        return relativeDate.toUpperCase();
    }



    // Clean all elements of the recycler
    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            comments.add(comments.get(i));
        }
        notifyDataSetChanged();
    }


}

