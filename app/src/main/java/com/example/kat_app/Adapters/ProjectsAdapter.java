package com.example.kat_app.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kat_app.Project;
import com.example.kat_app.R;
import com.parse.ParseUser;

import java.util.List;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ViewHolder> {
    private Context context;
    private List<Project> projects;
    private final String TAG = "UpdatesAdapter";

    public ProjectsAdapter(Context context, List<Project> projects) {
        this.context = context;
        this.projects = projects;
    }

    @NonNull
    @Override
    public ProjectsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_project, parent, false);
        return new ProjectsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectsAdapter.ViewHolder holder, int position) {
        Project project = projects.get(position);
        holder.bind(project);
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG,"item count: " + updates.size());
        return projects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView tvAuthor;
        private TextView tvInvestors;
        private TextView tvFollowers;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvFollowers = itemView.findViewById(R.id.tvFollowers);
            tvInvestors = itemView.findViewById(R.id.tvInvestors);
        }


        //add in data for specific user's post
        public void bind(final Project project) {
            ParseUser user = project.getUser();
            tvName.setText(project.getName());
            tvAuthor.setText("Username");
            tvInvestors.setText("Investors: " + project.getInvestors().length());
            tvFollowers.setText("Followers: " + project.getFollowers().length());
        }
    }

//    // Clean all elements of the recycler
//    public void clear() {
//        updates.clear();
//        notifyDataSetChanged();
//    }
//
//    // Add a list of items -- change to type used
//    public void addAll(List<Update> list) {
//        updates.addAll(list);
//        notifyDataSetChanged();
//    }

}
