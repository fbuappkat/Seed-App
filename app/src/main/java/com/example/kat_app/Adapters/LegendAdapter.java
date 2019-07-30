package com.example.kat_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kat_app.Activities.ConfirmInvestActivity;
import com.example.kat_app.R;
import com.example.kat_app.Models.Request;

import org.parceler.Parcels;

import java.util.List;

public abstract class LegendAdapter extends RecyclerView.Adapter<InvestAdapter.ViewHolder> {
    private static Context context;
    private List<Request> requests;
    View view;


    public LegendAdapter(Context context, List<Request> requests) {
        this.context = context;
        this.requests = requests;
    }

    /*@NonNull
    @Override
    public LegendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_legend, parent, false);
        return new LegendAdapter.ViewHolder(view);
    }*/

    @Override
    public void onBindViewHolder(@NonNull InvestAdapter.ViewHolder holder, int position) {
        Request request = requests.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG,"item count: " + updates.size());
        return requests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvItem;
        private TextView tvCost;
        private ImageView ivColor;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvItem = itemView.findViewById(R.id.tvLegendItem);
            tvCost = itemView.findViewById(R.id.tvLegendCost);
        }

        //add in data for specific user's post
        public void bind(final Request request) {
            tvItem.setText(request.getRequest());
            tvCost.setText(Float.toString(request.getPrice()));
        }

    }
}
