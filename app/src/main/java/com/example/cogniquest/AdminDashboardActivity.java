package com.example.cogniquest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.cogniquest.databinding.ActivityAdminDashboardBinding;
import com.google.android.material.navigation.NavigationBarView;

public class AdminDashboardActivity extends AppCompatActivity {

    private ActivityAdminDashboardBinding binding;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        android.content.SharedPreferences prefs = getSharedPreferences("admin_pref", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        int targetMode = isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        if (AppCompatDelegate.getDefaultNightMode() != targetMode) {
            AppCompatDelegate.setDefaultNightMode(targetMode);
        }

        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.switchDarkMode.setChecked(isDarkMode);
        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            int newMode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(newMode);
        });

        databaseHelper = new DatabaseHelper(this);

        setupDashboardData();

        binding.btnManageUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, ManageQuizzesActivity.class));
            }
        });

        binding.btnCreateQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ManageQuizzesActivity.class);
                intent.putExtra("trigger_add", true);
                startActivity(intent);
            }
        });

        binding.btnSystemLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "System Logs", Toast.LENGTH_SHORT).show();
            }
        });

        binding.tvViewAllQuizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, ManageQuizzesActivity.class));
            }
        });

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    return true; // Already here
                } else if (itemId == R.id.nav_manage_quizzes) {
                    startActivity(new Intent(AdminDashboardActivity.this, ManageQuizzesActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_logout) {
                    UserManager userManager = new UserManager(AdminDashboardActivity.this);
                    userManager.logout();
                    Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });

        binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupDashboardData();
    }

    private void setupDashboardData() {
        java.util.List<Quiz> quizzes = databaseHelper.getAllQuizzes();
        binding.tvTotalQuizzes.setText(String.valueOf(quizzes.size()));

        // Get up to 3 most recent quizzes
        java.util.List<Quiz> recentQuizzes = new java.util.ArrayList<>();
        for (int i = quizzes.size() - 1; i >= 0 && recentQuizzes.size() < 3; i--) {
            recentQuizzes.add(quizzes.get(i));
        }

        QuizAdapter adapter = new QuizAdapter(recentQuizzes, null);
        binding.recyclerViewRecentQuizzes.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        binding.recyclerViewRecentQuizzes.setAdapter(adapter);
    }
}
