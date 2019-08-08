package com.example.kat_app.Activities;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.kat_app.Fragments.ChatFragment;
import com.example.kat_app.Fragments.FeedFragment;
import com.example.kat_app.Fragments.HomeFragment;
import com.example.kat_app.Fragments.ProfileFragment;
import com.example.kat_app.Models.Chat;
import com.example.kat_app.R;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 4;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return HomeFragment.newInstance(0, "Page # 1");
                case 1:
                    return FeedFragment.newInstance(1, "Page # 2");
                case 2:
                    return ChatFragment.newInstance(2, "Page # 3");
                case 3:
                    return ProfileFragment.newInstance(3, "Page # 4");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    @BindView(R.id.bottomNav)
    BubbleNavigationLinearView bottomNav;
    @BindView(R.id.centerView)
    ViewPager viewPager;
    @BindView(R.id.feed)
    ConstraintLayout feed;
    @BindView(R.id.home)
    ConstraintLayout home;
    @BindView(R.id.chat)
    ConstraintLayout chat;
    @BindView(R.id.profile)
    ConstraintLayout profile;
    public static ImageButton btnAddUpdate;
    public static ImageButton ivMap;
    public static ImageButton ivNewMessage;
    public static ImageButton ivEdit;
    public static ImageButton ivSettings;
    public static ProgressBar pbLoad;

    private PagerAdapter pagerAdapter;

    public static Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setStatusBar(getWindow());
        ButterKnife.bind(this);

        feed.setVisibility(View.GONE);
        home.setVisibility(View.VISIBLE);
        chat.setVisibility(View.GONE);
        profile.setVisibility(View.GONE);

        activity = this;

        btnAddUpdate = findViewById(R.id.ivAddUpdate);
        pbLoad = findViewById(R.id.pbLoad);
        ivMap = findViewById(R.id.ivEarth);
        ivNewMessage = findViewById(R.id.ivNewMessage);
        ivEdit = findViewById(R.id.ivEdit);
        ivSettings = findViewById(R.id.ivSettings);

        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                bottomNav.setCurrentActiveItem(i);

                if (viewPager.getCurrentItem() == 0) {
                    feed.setVisibility(View.GONE);
                    home.setVisibility(View.VISIBLE);
                    chat.setVisibility(View.GONE);
                    profile.setVisibility(View.GONE);
                }

                if (viewPager.getCurrentItem() == 1) {
                    feed.setVisibility(View.VISIBLE);
                    home.setVisibility(View.GONE);
                    chat.setVisibility(View.GONE);
                    profile.setVisibility(View.GONE);
                }

                if (viewPager.getCurrentItem() == 2) {
                    feed.setVisibility(View.GONE);
                    home.setVisibility(View.GONE);
                    chat.setVisibility(View.VISIBLE);
                    profile.setVisibility(View.GONE);
                }

                if (viewPager.getCurrentItem() == 3) {
                    feed.setVisibility(View.GONE);
                    home.setVisibility(View.GONE);
                    chat.setVisibility(View.GONE);
                    profile.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        bottomNav.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                viewPager.setCurrentItem(position, true);
            }
        });

        bottomNav.setTypeface(ResourcesCompat.getFont(this, R.font.proximanova_semibold));
        viewPager.setCurrentItem(0);
    }


    public static void setStatusBar(Window window) {
        // Make sure that status bar text is still visible
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}
