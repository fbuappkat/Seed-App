package com.example.kat_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kat_app.Models.Project;
import com.example.kat_app.Models.Transaction;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.text.NumberFormat;
import java.util.List;

public class EarningsTransactionAdapter extends RecyclerView.Adapter<EarningsTransactionAdapter.ViewHolder> {

    private final String TAG = "TransactionsAdapter";

    private Context context;
    private List<Transaction> transactions;

    public EarningsTransactionAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public EarningsTransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_earning_transaction, parent, false);
        return new EarningsTransactionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EarningsTransactionAdapter.ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        holder.tvProjectName.setText(transaction.getProject().getName());
        if (transaction.getEarningType().equals("investment")) {
            holder.tvType.setText("Investment");
        } else if (transaction.getEarningType().equals("project_earning")) {
            holder.tvType.setText("Project Earning");
        }
        holder.tvAmountCount.setText(formatter.format(transaction.getAmount()));
        holder.tvDate.setText(transaction.getRelativeTimeAgo());
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG,"item count: " + updates.size());
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvProjectName;
        public TextView tvAmountCount;
        private TextView tvType;
        public TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProjectName = itemView.findViewById(R.id.tvProjectName);
            tvAmountCount = itemView.findViewById(R.id.tvAmountCount);
            tvType = itemView.findViewById(R.id.tvType);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

        @Override
        public void onClick(View v) {
        }

    }


    // Clean all elements of the recycler
    public void clear() {
        transactions.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Transaction> list) {
        transactions.addAll(list);
        notifyDataSetChanged();
    }
}