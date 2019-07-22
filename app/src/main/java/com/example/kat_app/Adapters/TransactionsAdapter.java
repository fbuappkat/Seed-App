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

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    private final String TAG = "TransactionsAdapter";

    private Context context;
    private List<Transaction> transactions;


    public TransactionsAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TransactionsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsAdapter.ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        if (transaction.getType() == null) {
            holder.withdrawTransaction.setVisibility(View.GONE);
            holder.depositTransaction.setVisibility(View.GONE);
            holder.investTransaction.setVisibility(View.VISIBLE);

            holder.tvInvestCount.setText(formatter.format(transaction.getAmount()));
            holder.tvTimeAgo.setText(transaction.getRelativeTimeAgo());
        } else if (transaction.getType().compareTo("withdrawl") == 0) {
            holder.depositTransaction.setVisibility(View.GONE);
            holder.investTransaction.setVisibility(View.GONE);
           holder.withdrawTransaction.setVisibility(View.VISIBLE);

           holder.tvWithdrawCount.setText(formatter.format(transaction.getAmount()));
           holder.tvTimeAgo.setText(transaction.getRelativeTimeAgo());
        } else if (transaction.getType().compareTo("deposit") == 0) {
            holder.investTransaction.setVisibility(View.GONE);
            holder.withdrawTransaction.setVisibility(View.GONE);
            holder.depositTransaction.setVisibility(View.VISIBLE);

            holder.tvDepositCount.setText(formatter.format(transaction.getAmount()));
            holder.tvTimeAgo2.setText(transaction.getRelativeTimeAgo());
        }
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG,"item count: " + updates.size());
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ConstraintLayout withdrawTransaction;
        public ConstraintLayout depositTransaction;
        public ConstraintLayout investTransaction;
        public TextView tvWithdrawCount;
        public TextView tvDepositCount;
        public TextView tvInvestCount;
        public TextView tvTimeAgo;
        public TextView tvTimeAgo2;
        public TextView tvTimeAgo3;

        public ViewHolder(View itemView) {
            super(itemView);
            withdrawTransaction = itemView.findViewById(R.id.withdrawTransaction);
            depositTransaction = itemView.findViewById(R.id.depositTransaction);
            investTransaction = itemView.findViewById(R.id.investTransaction);
            tvWithdrawCount = itemView.findViewById(R.id.tvWithdrawCount);
            tvDepositCount = itemView.findViewById(R.id.tvDepositCount);
            tvInvestCount = itemView.findViewById(R.id.tvInvestCount);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            tvTimeAgo2 = itemView.findViewById(R.id.tvTimeAgo2);
            tvTimeAgo3 = itemView.findViewById(R.id.tvTimeAgo3);
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
