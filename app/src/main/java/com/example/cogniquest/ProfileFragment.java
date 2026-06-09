package com.example.cogniquest;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private UserManager userManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile_stats, container, false);

        userManager = new UserManager(requireContext());

        // Get views from R.layout.activity_profile_stats
        View appBarLayout = view.findViewById(R.id.appBarLayout);
        View appBarBorder = view.findViewById(R.id.appBarBorder);
        View bottomNavigation = view.findViewById(R.id.bottomNavigation);

        // Hide inner header and inner bottom navigation to resolve the double navbar & header issue
        if (appBarLayout != null) {
            appBarLayout.setVisibility(View.GONE);
        }
        if (appBarBorder != null) {
            appBarBorder.setVisibility(View.GONE);
        }
        if (bottomNavigation != null) {
            bottomNavigation.setVisibility(View.GONE);
        }

        // Bind data
        TextView tvProfileName = view.findViewById(R.id.tvProfileName);
        TextView scholarInfo = view.findViewById(R.id.scholarInfo);
        SwitchCompat switchDarkMode = view.findViewById(R.id.switchDarkMode);
        View editProfileLayout = view.findViewById(R.id.editProfileLayout);
        View logoutLayout = view.findViewById(R.id.logoutLayout);

        if (tvProfileName != null) {
            tvProfileName.setText(userManager.getFullname());
        }
        if (scholarInfo != null) {
            scholarInfo.setText(userManager.getBio());
        }

        if (switchDarkMode != null) {
            switchDarkMode.setChecked(userManager.isDarkMode());
            switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                userManager.setDarkMode(isChecked);
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            });
        }

        if (editProfileLayout != null) {
            editProfileLayout.setOnClickListener(v -> {
                startActivity(new Intent(requireContext(), EditProfileActivity.class));
            });
        }

        if (logoutLayout != null) {
            logoutLayout.setOnClickListener(v -> {
                userManager.logout();
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userManager != null && getView() != null) {
            TextView tvProfileName = getView().findViewById(R.id.tvProfileName);
            TextView scholarInfo = getView().findViewById(R.id.scholarInfo);
            if (tvProfileName != null) {
                tvProfileName.setText(userManager.getFullname());
            }
            if (scholarInfo != null) {
                scholarInfo.setText(userManager.getBio());
            }
        }
    }
}
