package com.example.kat_app.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.example.kat_app.Fragments.FeedFragment;
import com.example.kat_app.Fragments.HomeFragment;
import com.example.kat_app.Fragments.ProfileFragment;
import com.example.kat_app.R;


public class MainActivity extends AppCompatActivity {

    private ConstraintLayout toolbar;
    private BottomNavigationView bottomNav;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setStatusBar(getWindow());

        // Find references for the views
        toolbar =   findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottomNav);

        // Get the fragment manager
        fragmentManager = getSupportFragmentManager();

        // Set up the navigation bar to switch between fragments
        bottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        Fragment fragment;
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        switch (menuItem.getItemId()) {
                            case R.id.navFeed:
                                fragment = new FeedFragment();
                                hideToolbar();
                                break;
                            case R.id.navHome:
                                fragment = new HomeFragment();
                                hideToolbar();
                                break;
                            case R.id.navUser:
                                fragment = new ProfileFragment();
                                hideToolbar();
                                break;

                                default:
                                fragment = new FeedFragment();
                                showToolbar();
                                break;
                        }
                        fragmentManager.beginTransaction().replace(R.id.centerView, fragment).commit();
                        return true;
                    }
                });
    }

    public static void setStatusBar(Window window) {
        // Make sure that status bar text is still visible
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void hideToolbar() {
        toolbar.setVisibility(View.GONE);
    }

    private void showToolbar() {
        toolbar.setVisibility(View.VISIBLE);
    }
}
