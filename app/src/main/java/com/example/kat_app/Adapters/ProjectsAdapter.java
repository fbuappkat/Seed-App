package com.example.kat_app.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.kat_app.Models.Project;
import com.example.kat_app.Models.Request;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ViewHolder> {
    private static Context context;
    private static List<Project> projects;
    private static List<Project> matchingProjects;
    private final String TAG = "UpdatesAdapter";
    private OnClickListener monClickListener;


    public ProjectsAdapter(Context context, List<Project> projects, OnClickListener onClickListener) {
        this.context = context;
        this.projects = projects;
        matchingProjects = projects;
        this.monClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ProjectsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_project, parent, false);

        return new ProjectsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectsAdapter.ViewHolder holder, int position) {
        Project project = matchingProjects.get(position);
        holder.bind(project, monClickListener);
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnClickListener onClickListener;
        private TextView tvName;
        private TextView tvAuthor;
        private TextView tvInvestorsCount;
        private TextView tvFollowersCount;
        private TextView tvPercentCount;
        private ImageView ivThumbnail;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvName = itemView.findViewById(R.id.tvName);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvFollowersCount = itemView.findViewById(R.id.tvFollowersCount);
            tvInvestorsCount = itemView.findViewById(R.id.tvInvestorsCount);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvPercentCount = itemView.findViewById(R.id.tvPercentCount);
        }

        protected void queryUser(final Project project) {
            ParseQuery<ParseUser> projectQuery = new ParseQuery<ParseUser>(ParseUser.class);

            projectQuery.whereEqualTo("objectId", project.getUser().getObjectId());
            projectQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> posts, ParseException e) {
                    if (e != null) {
                        Log.e("Query requests", "Error with query");
                        e.printStackTrace();
                        return;
                    }
                    if (posts.size() != 0) {
                        ParseUser user = posts.get(0);
                        tvAuthor.setText("@" + user.getUsername());
                    }
                }

            });
        }

        //add in data for specific user's post
        public void bind(final Project project, OnClickListener onClickListener) {
            tvName.setText(project.getName());
            tvInvestorsCount.setText(project.getInvestors().length() + "");
            tvFollowersCount.setText(project.getFollowers().length() + "");
            queryUser(project);
            queryRequests(project);
            ParseFile profileImage = project.getParseFile("thumbnail");
            MultiTransformation<Bitmap> multiTransformation = new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCornersTransformation(25, 0), new BlurTransformation(7));
            if (profileImage != null) {
                Glide.with(context)
                        .load(profileImage.getUrl())
                        .apply(bitmapTransform(multiTransformation))
                        .into(ivThumbnail);
            } else {
                Glide.with(context)
                        .load(R.drawable.default_project_image)
                        .apply(bitmapTransform(multiTransformation))
                        .into(ivThumbnail);
            }
            this.onClickListener = onClickListener;
            itemView.setOnClickListener(this);

        }

        protected void queryRequests(final Project project) {
            ParseQuery<Request> requestQuery = new ParseQuery<>(Request.class);
            requestQuery.include("project");

            requestQuery.whereEqualTo("project", project);
            requestQuery.findInBackground(new FindCallback<Request>() {
                @Override
                public void done(List<Request> requests, ParseException e) {
                    if (e != null) {
                        Log.e("Query requests", "Error with query");
                        e.printStackTrace();
                        return;
                    }

                    NumberFormat percentageFormat = NumberFormat.getPercentInstance();
                    percentageFormat.setMinimumFractionDigits(2);

                    float requestedFunds = 0;
                    float receievedFunds = 0;
                    float percent = 0;

                    for (Request projectRequest : requests) {
                        receievedFunds += projectRequest.getPrice();
                        requestedFunds += projectRequest.getReceived();
                    }

                    percent = (requestedFunds/receievedFunds);
                    tvPercentCount.setText(percentageFormat.format(percent));
                }

            });
        }


        @Override
        public void onClick(View v) {
            onClickListener.onClick(getAdapterPosition());
        }


    }

    public interface OnClickListener {
        void onClick(int position);
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        projects.clear();
        if (charText.length() == 0) {
            projects.addAll(matchingProjects);
        } else {
            for (Project project : projects) {
                if (project.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    projects.add(project);
                }
            }
        }
        notifyDataSetChanged();
    }


    // Clean all elements of the recycler
    public void clear() {
        projects.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Project> list) {
        projects.addAll(list);
        notifyDataSetChanged();
    }

    public Filter getFilter(final String text) {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = text;
                if (charString.isEmpty()) {
                    matchingProjects = projects;
                } else {
                    List<Project> filteredList = new ArrayList<>();
                    for (Project row : projects) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getDescription().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    matchingProjects = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = matchingProjects;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                matchingProjects = (ArrayList<Project>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    private static float round(float value) {
        return (float) Math.round(value * 100) / 100;
    }

}
