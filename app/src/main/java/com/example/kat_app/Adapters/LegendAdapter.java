package com.example.kat_app.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kat_app.Models.Request;
import com.example.kat_app.R;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.List;

public class LegendAdapter extends RecyclerView.Adapter<LegendAdapter.ViewHolder> {
    private static Context context;
    private List<PieEntry> requests;
    View view;


    public LegendAdapter(Context context, List<PieEntry> requests) {
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public LegendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_legend, viewGroup, false);
        return new LegendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PieEntry request = requests.get(position);
        holder.tvItem.setText(request.getLabel());
        holder.tvCost.setText(Float.toString(request.getValue()));
        holder.btn.setBackgroundColor(ColorTemplate.JOYFUL_COLORS[position]);

    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvItem;
        private TextView tvCost;
        private ImageView ivColor;
        private Button btn;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvItem = itemView.findViewById(R.id.tvLegendItem);
            tvCost = itemView.findViewById(R.id.tvLegendCost);
            btn = itemView.findViewById(R.id.btn);
        }

        //add in data for specific user's post
        public void bind(final Request request) {
            tvItem.setText(request.getRequest());
            tvCost.setText(Float.toString(request.getPrice()));


        }

    }
}
