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

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ViewHolder> {
    private Context context;
    private List<Request> requests;


    public ProgressAdapter(Context context, List<Request> requests) {
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public ProgressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_progress, parent, false);
        return new ProgressAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressAdapter.ViewHolder holder, int position) {
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
        private ProgressBar pbReq;

        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvReq = itemView.findViewById(R.id.tvReqProgress);
            pbReq = itemView.findViewById(R.id.pbReq);

        }



        //add in data for specific user's post
        public void bind(final Request request) {
            int percent = Math.round(request.getReceived()/request.getPrice() * 100);
            tvReq.setText(request.getRequest() + ": " + request.getReceived() + "/" + request.getPrice() + " (" + percent + "%)");
            pbReq.setProgress(percent);

        }




    }




}