package com.example.kat_app.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.kat_app.Activities.UserOwnedProjectActivity;
import com.example.kat_app.Models.Project;
import com.example.kat_app.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class UserProjectAdapter extends RecyclerView.Adapter<UserProjectAdapter.ViewHolder> {

    private final String TAG = "UserProjectAdapter";
    private Context context;
    private List<Project> userProjects;

    public UserProjectAdapter(Context context, List<Project> userProjects) {
        this.context = context;
        this.userProjects = userProjects;
    }

    @NonNull
    @Override
    public UserProjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_projects, parent, false);
        return new UserProjectAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProjectAdapter.ViewHolder holder, int position) {
        Project project = userProjects.get(position);
        holder.bind(project);
    }

    @Override
    public int getItemCount() {
        return userProjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvName;
        private TextView tvInvestors;
        private TextView tvFollowers;
        private ImageView ivThumbnail;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvName = itemView.findViewById(R.id.tvName);
            tvFollowers = itemView.findViewById(R.id.tvFollowers);
            tvInvestors = itemView.findViewById(R.id.tvInvestors);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
        }


        //add in data for specific user's post
        public void bind(final Project project) {
            tvName.setText(project.getName());
            tvInvestors.setText("Investors: " + project.getInvestors().length());
            tvFollowers.setText("Followers: " + project.getFollowers().length());
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent project2description = new Intent(context, UserOwnedProjectActivity.class);
                    project2description.putExtra("project", Parcels.wrap(project));
                    context.startActivity(project2description);
                }
            });
        }

        @Override
        public void onClick(View v) {
        }


    }

}
