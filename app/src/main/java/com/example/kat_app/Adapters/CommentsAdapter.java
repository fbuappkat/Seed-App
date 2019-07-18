package com.example.kat_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kat_app.R;

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

    private List<String> comments;
    Context context;

    // pass in the Tweets array in the constructor
    public CommentsAdapter(List<String> list) {
        comments = list;
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvComment;

        CommentsAdapter commentsAdapter;

        public ViewHolder(View itemView) {
            super(itemView);

            // perform findViewById lookups
            tvComment = (TextView) itemView.findViewById(R.id.tvCommentField);
        }
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_comment, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);

        //add itemView's OnClickListener
        //itemView.setOnClickListener(this);
        return viewHolder;
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get the data according to position
        final String comment = comments.get(position);
        // populate the views according to this data
        holder.tvComment.setText(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
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

    // Clean all elements of the recycler
    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<String> list) {
        comments.addAll(list);
        notifyDataSetChanged();
    }


}

