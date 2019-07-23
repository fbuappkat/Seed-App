package com.example.kat_app.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.kat_app.Adapters.TransactionsAdapter;
import com.example.kat_app.Models.Transaction;
import com.example.kat_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionHistoryActivity extends AppCompatActivity {

    public static final String TAG = "TransactionHistoryActivity";


    @BindView(R.id.ivBack)
    ImageButton ivBack;
    @BindView(R.id.rvTransactions)
    RecyclerView rvTransactions;

    private TransactionsAdapter adapter;
    private List<Transaction> mTransactions;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        ButterKnife.bind(this);

        MainActivity.setStatusBar(getWindow());

        mTransactions = new ArrayList<>();

        adapter = new TransactionsAdapter(this, mTransactions);

        rvTransactions.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(this);
        rvTransactions.setLayoutManager(layoutManager);


        loadTopPosts(new Date(0));

        setBackButton();
    }

    private void setBackButton() {
        // Set on-click listener for for image view to launch edit account activity
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    protected void loadTopPosts(final Date maxDate) {

        ParseUser currUser = ParseUser.getCurrentUser();

        final Transaction.Query transactionsQuery = new Transaction.Query();
        transactionsQuery.getTop().withCurrUser(currUser);

        // If app is just opened, get newest 20 posts
        // Else query for older posts
        if (maxDate.equals(new Date(0))) {
            adapter.clear();
            transactionsQuery.getTop().withCurrUser(currUser);
        } else {
            transactionsQuery.getNext(maxDate).getTop().withCurrUser(currUser);
        }

        transactionsQuery.findInBackground(new FindCallback<Transaction>() {
            @Override
            public void done(List<Transaction> transactions, ParseException e) {
                if (e == null) {

                    // if opening app, clear out old items
                    if (maxDate.equals(new Date(0))) {
                        adapter.clear();
                    }

                    mTransactions.addAll(transactions);
                    adapter.notifyDataSetChanged();

                    // For logging purposes
                    for (int i = 0; i < transactions.size(); i++) {
                        Log.d(TAG, "Transaction[" + i + "] = "
                                + transactions.get(i).getAmount()
                                + "\nusername = " + transactions.get(i).getSender());
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    // Get maximum Date to find next post to load.
    protected Date getMaxDate() {
        int size = mTransactions.size();
        if (size == 0) {
            return new Date(0);
        } else {
            Transaction oldest = mTransactions.get(mTransactions.size() - 1);
            return oldest.getCreatedAt();
        }
    }
}
