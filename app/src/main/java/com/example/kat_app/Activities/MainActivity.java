package com.example.kat_app.Activities;

import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.kat_app.Fragments.FeedFragment;
import com.example.kat_app.Fragments.HomeFragment;
import com.example.kat_app.Fragments.ProfileFragment;
import com.example.kat_app.R;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setStatusBarColor();

        // Find reference for the view
        bottomNav = findViewById(R.id.bottomNav);

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
                                break;
                            case R.id.navHome:
                                fragment = new HomeFragment();
                                break;
                            case R.id.navUser:
                                fragment = new ProfileFragment();
                                break;
                            default:
                                fragment = new FeedFragment();
                                break;
                        }
                        fragmentManager.beginTransaction().replace(R.id.centerView, fragment).commit();
                        return true;
                    }
                });
    }

    private void setStatusBarColor() {
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(this.getResources().getColor(R.color.kat_off_white));
    }
}
