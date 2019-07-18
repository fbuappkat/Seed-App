package com.example.kat_app.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kat_app.R;
import com.example.kat_app.Request;

import java.util.List;

public class InvestAdapter extends RecyclerView.Adapter<InvestAdapter.ViewHolder> {
    private Context context;
    private List<Request> requests;


    public InvestAdapter(Context context, List<Request> requests) {
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public InvestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_invest, parent, false);
        return new InvestAdapter.ViewHolder(view);
    }

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

        private TextView tvReq;
        private TextView tvProg;
        private ProgressBar pbReq;

        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvReq = itemView.findViewById(R.id.tvRequest);
            tvProg = itemView.findViewById(R.id.tvProgress);
            pbReq = itemView.findViewById(R.id.pbInvested);

        }



        //add in data for specific user's post
        public void bind(final Request request) {
            int percent = Math.round(request.getReceived()/request.getPrice() * 100);
            tvReq.setText(request.getRequest());
            tvProg.setText(request.getReceived() + "/" + request.getPrice() + " (" + percent + "%)");
            pbReq.setProgress(percent);

        }




    }




}