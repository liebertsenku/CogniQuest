package com.example.cogniquest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cogniquest.databinding.ActivityProfileStatsBinding;
import com.google.android.material.navigation.NavigationBarView;

import androidx.appcompat.app.AppCompatDelegate;

public class ProfileStatsActivity extends AppCompatActivity {

    private ActivityProfileStatsBinding binding;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileStatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userManager = new UserManager(this);

        // Set dynamic text
        binding.tvProfileName.setText(userManager.getFullname());
        binding.scholarInfo.setText(userManager.getBio());

        // Setup Dark Mode Switch
        binding.switchDarkMode.setChecked(userManager.isDarkMode());
        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            userManager.setDarkMode(isChecked);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        binding.editProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileStatsActivity.this, EditProfileActivity.class));
            }
        });

        binding.logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userManager.logout();
                Intent intent = new Intent(ProfileStatsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_profile) {
                    return true;
                } else if (itemId == R.id.nav_home) {
                    startActivity(new Intent(ProfileStatsActivity.this, HomeDashboardActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_history) {
                    startActivity(new Intent(ProfileStatsActivity.this, QuizHistoryActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
        binding.bottomNavigation.setSelectedItemId(R.id.nav_profile);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userManager != null && binding != null) {
            binding.tvProfileName.setText(userManager.getFullname());
            binding.scholarInfo.setText(userManager.getBio());
        }
    }
}
