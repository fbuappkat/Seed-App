package com.example.kat_app.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.audiofx.DynamicsProcessing;
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
import com.example.kat_app.Models.Equity;
import com.example.kat_app.Models.Project;
import com.example.kat_app.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class InvestedProjectsAdapter extends RecyclerView.Adapter<InvestedProjectsAdapter.ViewHolder> {

    private final String TAG = "UserProjectAdapter";
    private Context context;
    private List<Equity> investedProjects;

    public InvestedProjectsAdapter(Context context, List<Equity> investedProjects) {
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
        Equity investment = investedProjects.get(position);
        holder.bind(investment);
    }

    @Override
    public int getItemCount() {
        return investedProjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvProjectName;
        private TextView tvEquityVal;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvProjectName = itemView.findViewById(R.id.tvProjectName);
            tvEquityVal = itemView.findViewById(R.id.tvEquityVal);
        }


        //add in data for specific user's post
        public void bind(Equity investment) {
            try {
                tvProjectName.setText(investment.getProject().fetchIfNeeded().getString("name"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvEquityVal.setText(investment.getEquity() + "%");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }


    }

}
