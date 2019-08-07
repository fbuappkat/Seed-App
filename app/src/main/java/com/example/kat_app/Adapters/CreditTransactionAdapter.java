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

public class CreditTransactionAdapter extends RecyclerView.Adapter<CreditTransactionAdapter.ViewHolder> {

    private final String TAG = "TransactionsAdapter";

    private Context context;
    private List<Transaction> transactions;


    public CreditTransactionAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public CreditTransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_credit_transaction, parent, false);
        return new CreditTransactionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CreditTransactionAdapter.ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        if (transaction.getType().equals("withdrawal")) {
            holder.tvBalance.setText(transaction.getBalance().toString());
            holder.tvAmount.setText("-" + formatter.format(transaction.getAmount()));
            holder.tvDate.setText(transaction.getRelativeTimeAgo());
        } else if (transaction.getType().equals("deposit")) {
            holder.tvBalance.setText(transaction.getBalance().toString());
            holder.tvAmount.setText("+" + formatter.format(transaction.getAmount()));
            holder.tvDate.setText(transaction.getRelativeTimeAgo());
        }
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG,"item count: " + updates.size());
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvAmount;
        public TextView tvBalance;
        public TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvBalance = itemView.findViewById(R.id.tvBalance);
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