package com.example.kat_app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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

import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.azoft.carousellayoutmanager.CenterScrollListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kat_app.Activities.OtherUserProfileActivity;
import com.example.kat_app.Activities.ProjectDetailsActivity;
import com.example.kat_app.Activities.UpdateDetailsActivity;
import com.example.kat_app.Fragments.ProfileFragment;
import com.example.kat_app.Models.Project;
import com.example.kat_app.Models.Update;
import com.example.kat_app.R;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
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
    private final String TAG = "UpdatesAdapter";
    private final ParseUser currUser = ParseUser.getCurrentUser();
    private static final String KEY_PROFILE_IMAGE = "profile_image";
    private boolean userInFollowList;
    private final static String KEY_FOLLOWERS = "followers";

    protected MediaAdapter mediaAdapter;
    private JSONArray media;

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
        final Update update = updates.get(position);
        final ViewHolder hold = holder;

        //Todo make less janky and slow
        ParseQuery<Project> projectQuery = new ParseQuery<>("Project");
        projectQuery.whereEqualTo("objectId", update.getProject().getObjectId());
        if (projectQuery != null) {
            projectQuery.findInBackground(new FindCallback<Project>() {
                @Override
                public void done(List<Project> projs, com.parse.ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error with query");
                        e.printStackTrace();
                        return;
                    }
                    if (projs.size() != 0) {
                        Project project = projs.get(0);
                        /*try {
                            JSONArray followers = project.getFollowers();
                            for (int i = 0; i < followers.length(); i++) {
                                JSONObject jsonobject = followers.getJSONObject(i);
                                String userID = jsonobject.getString("objectId");
                                String currUserID = currUser.getObjectId();
                                if (Boolean.toString(userID.equals(currUserID)).equals("true")) {
                                    userInFollowList = true;
                                    break;
                                }
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }*/

                        hold.bind(update, project.getName());
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return updates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected MediaAdapter mediaAdapter;
        private TextView tvUser;
        private TextView tvUser2;
        private TextView tvCaption;
        private TextView tvRelativeTime;
        private TextView tvNumLikes;
        private LikeButton btnLike;
        private ImageView ivProfileImage;
        private TextView tvNumComments;
        private TextView tvProject;
        private EditText etComment;
        private Button btnAddComment;
        private ImageButton btnGoToComments;
        private RecyclerView rvPhotos;
        private TextView tvType;

        public ViewHolder(View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvEditAccount);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            tvNumLikes = itemView.findViewById(R.id.tvNumLikes);
            tvNumComments = itemView.findViewById(R.id.tvNumComments);
            btnLike = itemView.findViewById(R.id.btnLike);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImageUpdate);
            tvProject = itemView.findViewById(R.id.tvProject);
            btnGoToComments = itemView.findViewById(R.id.btnGoToComments);
            rvPhotos = itemView.findViewById(R.id.rvPhotos);
            tvType = itemView.findViewById(R.id.tvType);
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
                intent.putExtra("update", Parcels.wrap(update));
                intent.putExtra("user", Parcels.wrap(update.getUser()));
                intent.putExtra("comments", Parcels.wrap(update.getComments()));
                // show the activity
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }

        public void queryProject(Update update){
            ParseQuery<Project> projectQuery = new ParseQuery<Project>(Project.class);
            //updateQuery.include(Update.KEY_USER);
            projectQuery.whereEqualTo("objectId", update.getProject().getObjectId());

            projectQuery.findInBackground(new FindCallback<Project>() {
                @Override
                public void done(final List<Project> posts, com.parse.ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error with query");
                        e.printStackTrace();
                        return;
                    }
                    tvProject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent update2details = new Intent(context, ProjectDetailsActivity.class);
                            update2details.putExtra("project", Parcels.wrap(posts.get(0)));
                            context.startActivity(update2details);
                        }
                    });

                }
            });
        }

        //add in data for specific user's post
        public void bind(final Update update, String name) {

            Log.d(TAG, Boolean.toString(userInFollowList));

            String username = update.getUser().getString("username");
            tvUser.setText(username);
            if (update.getCaption().equals("")) {
                tvCaption.setVisibility(View.GONE);
            } else {
                tvCaption.setVisibility(View.VISIBLE);
                tvCaption.setText(update.getCaption());
            }
            tvRelativeTime.setText(getRelativeTimeAgo(String.valueOf(update.getCreatedAt())));
            tvNumLikes.setText(Integer.toString(update.getNumLikes()));
            tvNumComments.setText(Integer.toString(update.getNumComments()));
            tvType.setText(update.getString("type"));
            tvProject.setText(name);

            queryProject(update);



            if (update.getMedia() == null || update.getMedia().length() == 0) {
                rvPhotos.setVisibility(View.GONE);
            } else {
                rvPhotos.setVisibility(View.VISIBLE);
                media = update.getMedia();
                setupAdapter(rvPhotos, media);
            }


            ParseFile profileImage = update.getUser().getParseFile(KEY_PROFILE_IMAGE);
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

            JSONArray v = update.userLikes();
            if (v != null) {
                tvNumLikes.setText(Integer.toString(v.length()));
            } else {
                tvNumLikes.setText("0");
            }
            if (update.isLiked()) {
                btnLike.setLiked(true);
            } else {
                btnLike.setLiked(false);
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
                        feedToDetails.putExtra("update", Parcels.wrap(update));
                        feedToDetails.putExtra("user", Parcels.wrap(updates.get(position).getUser()));
                        // show the activity
                        context.startActivity(feedToDetails);
                    }
                }
            });

            btnLike.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    btnLike.setLiked(true);
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
                    tvNumLikes.setText(Integer.toString(update.getNumLikes()));
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    btnLike.setLiked(false);
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
                    tvNumLikes.setText(Integer.toString(update.getNumLikes()));
                }
            });

            if (tvUser.getText().toString().equals(currUser.getUsername())) {
                tvUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        Fragment myFragment = new ProfileFragment();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.centerView, myFragment).addToBackStack(null).commit();
                    }
                });
            } else {
                tvUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // get item position
                        int position = getAdapterPosition();
                        // get the update at the position, this won't work if the class is static
                        Update update = updates.get(position);
                        // create intent for the new activity
                        Intent feedToProfile = new Intent(context, OtherUserProfileActivity.class);
                        //pass user as an object
                        feedToProfile.putExtra("User", Parcels.wrap(update.getUser()));
                        // show the activity
                        context.startActivity(feedToProfile);
                        ((Activity) context).overridePendingTransition(R.anim.slide_up, R.anim.do_nothing);
                    }
                });
            }
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
        return relativeDate.toUpperCase();
    }

    public void setupAdapter(RecyclerView rvPhotos, JSONArray media) {
        // create the adapter
        mediaAdapter = new MediaAdapter(context, media);
        // set the adapter on the recycler view
        rvPhotos.setAdapter(mediaAdapter);
        // set the layout manager on the recycler view
        final CarouselLayoutManager layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL);
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
        rvPhotos.setLayoutManager(layoutManager);
        rvPhotos.setHasFixedSize(true);
        rvPhotos.addOnScrollListener(new CenterScrollListener());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
