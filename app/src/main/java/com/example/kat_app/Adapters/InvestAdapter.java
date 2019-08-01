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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kat_app.Activities.ConfirmInvestActivity;
import com.example.kat_app.Models.Project;
import com.example.kat_app.R;
import com.example.kat_app.Models.Request;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class InvestAdapter extends RecyclerView.Adapter<InvestAdapter.ViewHolder> {
    private static Context context;
    private List<Request> requests;
    private static Project project;
    private static float projTotal;


    public InvestAdapter(Context context, List<Request> requests, Project project) {
        this.context = context;
        this.requests = requests;
        this.project = project;
        projTotal = getTotal(requests);
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
        private EditText etInvest;
        private Button btnInvest;
        private ProgressBar pbReq;

        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvReq = itemView.findViewById(R.id.tvRequest);
            tvProg = itemView.findViewById(R.id.tvProgress);
            etInvest = itemView.findViewById(R.id.etInvest);
            pbReq = itemView.findViewById(R.id.pbInvested);

            btnInvest = itemView.findViewById(R.id.btnInvest);

        }



        //add in data for specific user's post
        public void bind(final Request request) {
            int percent = Math.round(request.getReceived()/request.getPrice() * 100);
            tvReq.setText(request.getRequest());
            tvProg.setText(request.getReceived() + "/" + request.getPrice() + " (" + percent + "%)");
            pbReq.setProgress(percent);
            btnInvest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String toInvest = etInvest.getText().toString();
                    if (!toInvest.isEmpty()) {
                        Intent confirm = new Intent(context, ConfirmInvestActivity.class);
                        confirm.putExtra("request", Parcels.wrap(request));
                        float investment = Float.parseFloat(etInvest.getText().toString());
                        confirm.putExtra("toInvest", investment);
                        confirm.putExtra("project", Parcels.wrap(project));
                        confirm.putExtra("total", projTotal);
                        context.startActivity(confirm);
                    } else {
                        Toast.makeText(context, "Please enter an amount to invest", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    //get total funds requested from project
    private float getTotal(List<Request> reqs) {
        float tot = 0;
        for (int i = 0; i < reqs.size(); i++) {
            tot += reqs.get(i).getPrice();
        }
        return tot;
    }
}