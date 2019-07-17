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
import android.view.WindowManager;

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

        setStatusBarColor(R.color.kat_orange_1);

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
                                showToolbar();
                                setStatusBarColor(R.color.kat_orange_1);
                                break;
                            case R.id.navHome:
                                fragment = new HomeFragment();
                                setStatusBarColor(R.color.kat_orange_1);
                                hideToolbar();
                                break;
                            case R.id.navUser:
                                setStatusBarColor(R.color.kat_white);
                                fragment = new ProfileFragment();
                                hideToolbar();
                                break;
                            default:
                                fragment = new FeedFragment();
                                showToolbar();
                                setStatusBarColor(R.color.kat_orange_1);
                                break;
                        }
                        fragmentManager.beginTransaction().replace(R.id.centerView, fragment).commit();
                        return true;
                    }
                });
    }

    private void setStatusBarColor(int statusBarColor) {
        Window window = this.getWindow();

        // Make sure that status bar text is still visible
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(this.getResources().getColor(statusBarColor));
    }

    private void hideToolbar() {
        toolbar.setVisibility(View.GONE);
    }

    private void showToolbar() {
        toolbar.setVisibility(View.VISIBLE);
    }
}
