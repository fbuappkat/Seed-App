package com.example.kat_app.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kat_app.Activities.EditAccountActivity;
import com.example.kat_app.Activities.ManageAccountActivity;
import com.example.kat_app.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";

    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvUsername;
    private TextView tvBalanceCount;
    private TextView tvProjectsCount;
    private TextView tvInvestmentsCount;
    private TextView tvBio;
    private ImageView ivSettings;
    private ImageView ivEdit;


    private static final String KEY_NAME = "name";
    private static final String KEY_PROFILE_IMAGE = "profile_image";
    private static final String KEY_BIO = "bio";
    private static final String KEY_BALANCE = "balance";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setProfileInfo(view);
        setEditAccountButton(view);
        setSettingsButton(view);
    }

    private void setProfileInfo(View view) {
        // Find references for the views
        tvName = view.findViewById(R.id.tvName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvProjectsCount = view.findViewById(R.id.tvProjectsCount);
        tvInvestmentsCount = view.findViewById(R.id.tvInvestmentsCount);
        ivProfileImage = view.findViewById(R.id.ivProfileImageUpdate);
        tvBalanceCount = view.findViewById(R.id.tvBalanceCount);
        tvBio = view.findViewById(R.id.tvBio);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);

        ParseUser currUser = ParseUser.getCurrentUser();

        tvName.setText(currUser.getString(KEY_NAME));
        tvUsername.setText("@" + currUser.getUsername());
        tvBalanceCount.setText("$" + currUser.getInt(KEY_BALANCE));
        tvBio.setText(currUser.getString(KEY_BIO));

        ParseFile profileImage = currUser.getParseFile(KEY_PROFILE_IMAGE);
        if (profileImage != null) {
            Glide.with(getContext())
                    .load(profileImage.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }
        else {
            Glide.with(getContext())
                    .load(R.drawable.default_profile_image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }
    }

    private void setEditAccountButton(View view) {
        // Find reference for the view
        ivEdit = view.findViewById(R.id.ivEdit);

        // Set on-click listener for for image view to launch edit account activity
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditAccountActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setSettingsButton(View view) {
        // Find reference for the view
        ivSettings = view.findViewById(R.id.ivSettings);

        // Set up the settings button to open manage account fragment
        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ManageAccountActivity.class);
                startActivity(intent);
            }
        });
    }
}
