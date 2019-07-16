package com.example.kat_app.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kat_app.Models.Update;
import com.example.kat_app.R;

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
        holder.bind(update);
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG,"item count: " + updates.size());
        return updates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUser;
        //private ImageView ivImage;
        private TextView tvCaption;
        private TextView tvRelativeTime;
        /*private TextView tvHandle2;
        private TextView tvNumLikes;
        private ImageButton btnLike;*/

        public ViewHolder(View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvUser);
            //ivImage = itemView.findViewById(R.id.ivDetailsImage);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            //tvHandle2 = itemView.findViewById(R.id.tvDetailsHandle2);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            /*tvNumLikes = itemView.findViewById(R.id.tvDetailsNumLikes);
            btnLike = itemView.findViewById(R.id.btnLike);
            //add itemView's OnClickListener
            itemView.setOnClickListener(this);*/
        }

        /*@Override
        public void onClick(View v) {
            // get item position
            int position = getAdapterPosition();
            // make sure the position exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position, this won't work if the class is static
                Update post = posts.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, PostDetails.class);
                //serialize the movie using parceler, use its short name as a key
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                // show the activity
                context.startActivity(intent);
            }
        }*/

        //add in data for specific user's post
        public void bind(final Update update) {
            tvUser.setText("test");
            tvCaption.setText(update.getCaption());
            tvRelativeTime.setText(getRelativeTimeAgo(String.valueOf(update.getCreatedAt())));
            //tvNumLikes.setText(Integer.toString(post.getNumLikes()));
           /* JSONArray v = post.userLikes();
            if (v != null) {
                tvNumLikes.setText(Integer.toString(v.length()));
            } else {
                tvNumLikes.setText("0");
            }
            if(post.isLiked()) {
                btnLike.setImageResource(R.drawable.ufi_heart_active);
            } else {
                btnLike.setImageResource(R.drawable.ufi_heart);
            }
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }
            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!post.isLiked()) {
                        btnLike.setImageResource(R.drawable.ufi_heart_active);
                        int position = getAdapterPosition();
                        Post post = posts.get(position);
                        int curLikes = post.getNumLikes();
                        //add current user to list of users who liked this post
                        post.likePost(ParseUser.getCurrentUser());

                        post.saveInBackground(new SaveCallback() {
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
                        Post post = posts.get(position);
                        int curLikes = post.getNumLikes();
                        //add current user to list of users who liked this post
                        post.unlikePost(ParseUser.getCurrentUser());

                        post.saveInBackground(new SaveCallback() {
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
            });*/
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
        return relativeDate;
    }
}
