package com.example.kat_app.Activities;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.codepath.instagram.EndlessRecyclerViewScrollListener;
import com.example.kat_app.Adapters.CreditTransactionAdapter;
import com.example.kat_app.Adapters.UserProjectAdapter;
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
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.rvDeposits)
    RecyclerView rvDeposits;
    @BindView(R.id.rvWithdrawals)
    RecyclerView rvWithdrawals;
    @BindView(R.id.rvInvestments)
    RecyclerView rvInvestments;
    @BindView(R.id.rvEarnings)
    RecyclerView rvEarnings;

    private CreditTransactionAdapter depositAdapter;
    private ArrayList<Transaction> deposits;

    private CreditTransactionAdapter withdrawalAdapter;
    private ArrayList<Transaction> withdrawals;

    private com.codepath.instagram.EndlessRecyclerViewScrollListener scrollListenerDeposits;
    private com.codepath.instagram.EndlessRecyclerViewScrollListener scrollListenerWithdrawals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        ButterKnife.bind(this);
        MainActivity.setStatusBar(getWindow());

        setCreditTransactionAdapters();

        setTabLayout();
        setBackButton();
    }

    private void setCreditTransactionAdapters() {
        // initialize the lists
        deposits = new ArrayList<>();
        withdrawals = new ArrayList<>();

        // create the adapters
        depositAdapter = new CreditTransactionAdapter(this, deposits);
        withdrawalAdapter = new CreditTransactionAdapter(this, withdrawals);

        // set the layout manager on the recycler views
        LinearLayoutManager layoutManagerDeposits = new LinearLayoutManager(this);
        LinearLayoutManager layoutManagerWithdrawals = new LinearLayoutManager(this);
        rvDeposits.setLayoutManager(layoutManagerDeposits);
        rvWithdrawals.setLayoutManager(layoutManagerWithdrawals);

        // set the adapters
        rvDeposits.setAdapter(depositAdapter);
        rvWithdrawals.setAdapter(withdrawalAdapter);

        // enable endless scrolling
        enableEndlessScrolling(layoutManagerDeposits, "deposit");
        enableEndlessScrolling(layoutManagerWithdrawals, "withdrawal");

        // add scroll listeners to recycler views
        rvDeposits.addOnScrollListener(scrollListenerDeposits);
        rvWithdrawals.addOnScrollListener(scrollListenerWithdrawals);

        // load the transactions
        loadTopTransactions(new Date(0), depositAdapter, deposits, "deposit");
        loadTopTransactions(new Date(0), withdrawalAdapter, withdrawals, "withdrawal");
    }

    protected void enableEndlessScrolling(LinearLayoutManager layoutManager, final String type) {
        if (type.equals("deposit")) {
            scrollListenerDeposits = new EndlessRecyclerViewScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    loadTopTransactions(getMaxDate(deposits), depositAdapter, deposits, type);
                }
            };
        } else if (type.equals("withdrawal")) {
            scrollListenerWithdrawals = new EndlessRecyclerViewScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    loadTopTransactions(getMaxDate(withdrawals), withdrawalAdapter, withdrawals, type);
                }
            };
        }
    }

    private void setTabLayout() {
        tabLayout.getTabAt(0).getIcon().setTint(getResources().getColor(R.color.kat_orange_1));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    tab.getIcon().setTint(getResources().getColor(R.color.kat_orange_1));
                    tabLayout.getTabAt(1).getIcon().setTint(getResources().getColor(R.color.kat_black));
                    tabLayout.getTabAt(2).getIcon().setTint(getResources().getColor(R.color.kat_black));
                    tabLayout.getTabAt(3).getIcon().setTint(getResources().getColor(R.color.kat_black));
                    rvDeposits.setVisibility(View.VISIBLE);
                    rvWithdrawals.setVisibility(View.GONE);
                    rvInvestments.setVisibility(View.GONE);
                    rvEarnings.setVisibility(View.GONE);
                }

                if (tab.getPosition() == 1) {
                    tab.getIcon().setTint(getResources().getColor(R.color.kat_orange_1));
                    tabLayout.getTabAt(0).getIcon().setTint(getResources().getColor(R.color.kat_black));
                    tabLayout.getTabAt(2).getIcon().setTint(getResources().getColor(R.color.kat_black));
                    tabLayout.getTabAt(3).getIcon().setTint(getResources().getColor(R.color.kat_black));
                    rvDeposits.setVisibility(View.GONE);
                    rvWithdrawals.setVisibility(View.VISIBLE);
                    rvInvestments.setVisibility(View.GONE);
                    rvEarnings.setVisibility(View.GONE);
                }

                if (tab.getPosition() == 2) {
                    tab.getIcon().setTint(getResources().getColor(R.color.kat_orange_1));
                    tabLayout.getTabAt(0).getIcon().setTint(getResources().getColor(R.color.kat_black));
                    tabLayout.getTabAt(1).getIcon().setTint(getResources().getColor(R.color.kat_black));
                    tabLayout.getTabAt(3).getIcon().setTint(getResources().getColor(R.color.kat_black));
                    rvDeposits.setVisibility(View.GONE);
                    rvWithdrawals.setVisibility(View.GONE);
                    rvInvestments.setVisibility(View.VISIBLE);
                    rvEarnings.setVisibility(View.GONE);
                }

                if (tab.getPosition() == 3) {
                    tab.getIcon().setTint(getResources().getColor(R.color.kat_orange_1));
                    tabLayout.getTabAt(0).getIcon().setTint(getResources().getColor(R.color.kat_black));
                    tabLayout.getTabAt(1).getIcon().setTint(getResources().getColor(R.color.kat_black));
                    tabLayout.getTabAt(2).getIcon().setTint(getResources().getColor(R.color.kat_black));
                    rvDeposits.setVisibility(View.GONE);
                    rvWithdrawals.setVisibility(View.GONE);
                    rvInvestments.setVisibility(View.GONE);
                    rvEarnings.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    protected void loadTopTransactions(final Date maxDate, final CreditTransactionAdapter adapter,
                                       final ArrayList<Transaction> creditTransactions, String type) {

        ParseUser currUser = ParseUser.getCurrentUser();

        final Transaction.Query transactionsQuery = new Transaction.Query();
        transactionsQuery.getTop().withCurrUser(currUser).whereEqualTo("type", type);

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

                    creditTransactions.addAll(transactions);
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
    protected Date getMaxDate(ArrayList<Transaction> transactions) {
        int size = transactions.size();
        if (size == 0) {
            return new Date(0);
        } else {
            Transaction oldest = transactions.get(transactions.size() - 1);
            return oldest.getCreatedAt();
        }
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

}
