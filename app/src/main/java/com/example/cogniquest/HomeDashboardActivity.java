package com.example.cogniquest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cogniquest.databinding.ActivityHomeDashboardBinding;
import com.google.android.material.navigation.NavigationBarView;

public class HomeDashboardActivity extends AppCompatActivity {

    private ActivityHomeDashboardBinding binding;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userManager = new UserManager(this);
        
        // The welcome text is now in HomeFragment, so we remove it from here.
        
        binding.addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeDashboardActivity.this, "Create new task", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup Navigation Component
        androidx.navigation.fragment.NavHostFragment navHostFragment = 
            (androidx.navigation.fragment.NavHostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.nav_host_fragment);
            
        if (navHostFragment != null) {
            androidx.navigation.NavController navController = navHostFragment.getNavController();
            
            // Handle SELECT_TAB extra
            int selectedTab = getIntent().getIntExtra("SELECT_TAB", R.id.nav_home);
            if (selectedTab != R.id.nav_home) {
                navController.navigate(selectedTab);
                binding.bottomNavigation.setSelectedItemId(selectedTab);
            }
            
            binding.bottomNavigation.setOnItemSelectedListener(new com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_home) {
                        if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() != R.id.nav_home) {
                            navController.navigate(R.id.nav_home);
                        }
                        return true;
                    } else if (itemId == R.id.nav_ai) {
                        startActivity(new Intent(HomeDashboardActivity.this, AiAssistantActivity.class));
                        return false;
                    } else if (itemId == R.id.nav_progress) {
                        startActivity(new Intent(HomeDashboardActivity.this, LearningProgressActivity.class));
                        return false;
                    } else if (itemId == R.id.nav_profile) {
                        if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() != R.id.nav_profile) {
                            navController.navigate(R.id.nav_profile);
                        }
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        
        androidx.navigation.fragment.NavHostFragment navHostFragment = 
            (androidx.navigation.fragment.NavHostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.nav_host_fragment);
            
        if (navHostFragment != null) {
            androidx.navigation.NavController navController = navHostFragment.getNavController();
            int selectedTab = intent.getIntExtra("SELECT_TAB", R.id.nav_home);
            if (navController != null) {
                navController.navigate(selectedTab);
                binding.bottomNavigation.setSelectedItemId(selectedTab);
            }
        }
    }
}
