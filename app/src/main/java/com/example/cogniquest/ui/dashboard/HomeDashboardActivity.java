package com.example.cogniquest.ui.dashboard;
import com.example.cogniquest.R;
import com.example.cogniquest.utils.UserManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
        updateAvatar();

        binding.notificationButton.setOnClickListener(v -> showNotificationDialog());
        


        // Setup Navigation Component
        androidx.navigation.fragment.NavHostFragment navHostFragment = 
            (androidx.navigation.fragment.NavHostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.nav_host_fragment);
            
        if (navHostFragment != null) {
            androidx.navigation.NavController navController = navHostFragment.getNavController();
            
            navController.addOnDestinationChangedListener(new androidx.navigation.NavController.OnDestinationChangedListener() {
                @Override
                public void onDestinationChanged(@NonNull androidx.navigation.NavController controller, 
                                               @NonNull androidx.navigation.NavDestination destination, 
                                               @Nullable Bundle arguments) {
                    int id = destination.getId();
                    if (id == R.id.nav_home) {
                        binding.appBarLayout.setVisibility(View.VISIBLE);
                        binding.appBarBorder.setVisibility(View.VISIBLE);
                    } else if (id == R.id.nav_profile) {
                        binding.appBarLayout.setVisibility(View.VISIBLE);
                        binding.appBarBorder.setVisibility(View.VISIBLE);
                    } else {
                        binding.appBarLayout.setVisibility(View.GONE);
                        binding.appBarBorder.setVisibility(View.GONE);
                    }
                }
            });

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
                        if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() != R.id.nav_ai) {
                            navController.navigate(R.id.nav_ai);
                        }
                        return true;
                    } else if (itemId == R.id.nav_progress) {
                        if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() != R.id.nav_progress) {
                            navController.navigate(R.id.nav_progress);
                        }
                        return true;
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

    @Override
    protected void onResume() {
        super.onResume();
        updateAvatar();
    }

    private void updateAvatar() {
        String avatarUri = userManager.getAvatarUri();
        if (avatarUri != null && !avatarUri.isEmpty()) {
            binding.profileImage.setImageURI(android.net.Uri.parse(avatarUri));
        } else {
            binding.profileImage.setImageResource(R.drawable.ic_profile);
        }
    }

    private void showNotificationDialog() {
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_notifications, null);
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create();
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        dialogView.findViewById(R.id.btnCloseNotifications).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
