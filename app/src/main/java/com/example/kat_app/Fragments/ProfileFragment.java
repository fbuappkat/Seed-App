package com.example.kat_app.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kat_app.Activities.LoginActivity;
import com.example.kat_app.Activities.MainActivity;
import com.example.kat_app.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";

    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvUsername;
    private TextView tvProjectsCount;
    private TextView tvInvestmentsCount;
    private TextView tvDescription;
    private ImageView ivLogout;
    private ImageView ivEdit;

    private static final String KEY_NAME = "name";
    private static final String KEY_PROFILE_IMAGE = "profile_image";
    private static final String KEY_DESCRIPTION = "description";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setProfileInfo(view);
        setLogoutButton(view);
    }

    private void setProfileInfo(View view) {
        // Find references for the views
        tvName = view.findViewById(R.id.tvName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvProjectsCount = view.findViewById(R.id.tvProjectsCount);
        tvInvestmentsCount = view.findViewById(R.id.tvInvestmentsCount);
        tvDescription = view.findViewById(R.id.tvDescription);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);

        ParseUser currUser = ParseUser.getCurrentUser();

        tvName.setText(currUser.getString(KEY_NAME));
        tvUsername.setText("@" + currUser.getUsername());
        tvDescription.setText(currUser.getString(KEY_DESCRIPTION));

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

    private void setLogoutButton(View view) {
        // Find reference for the view
        ivLogout = view.findViewById(R.id.ivLogout);

        // Make the logout image clickable and open up dialog on click
        ivLogout.setClickable(true);
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Log Out");
                builder.setIcon(R.drawable.ic_alert);
                builder.setMessage("Are you sure you want to log out?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.logOut();
                        final Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        dialog.dismiss();

                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                Button negButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                negButton.setTextColor(getResources().getColor(R.color.kat_orange_1));
                Button posButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                posButton.setTextColor(getResources().getColor(R.color.kat_orange_1));
            }
        });
    }
}
