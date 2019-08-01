package com.example.kat_app.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.kat_app.Models.Project;
import com.example.kat_app.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class InvestedProjectsAdapter extends RecyclerView.Adapter<InvestedProjectsAdapter.ViewHolder> {

    private final String TAG = "UserProjectAdapter";
    private Context context;
    private List<Project> investedProjects;

    public InvestedProjectsAdapter(Context context, List<Project> investedProjects) {
        this.context = context;
        this.investedProjects = investedProjects;
    }

    @NonNull
    @Override
    public InvestedProjectsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_investment, parent, false);
        return new InvestedProjectsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvestedProjectsAdapter.ViewHolder holder, int position) {
        Project project = investedProjects.get(position);
        holder.bind(project);
    }

    @Override
    public int getItemCount() {
        return investedProjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvProjectName;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvProjectName = itemView.findViewById(R.id.tvProjectName);
        }


        //add in data for specific user's post
        public void bind(Project project) {
            tvProjectName.setText(project.getName());
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }


    }

}
