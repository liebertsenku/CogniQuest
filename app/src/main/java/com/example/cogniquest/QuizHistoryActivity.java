package com.example.cogniquest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cogniquest.databinding.ActivityQuizHistoryBinding;
import com.google.android.material.navigation.NavigationBarView;

public class QuizHistoryActivity extends AppCompatActivity {

    private ActivityQuizHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(QuizHistoryActivity.this, HomeDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("SELECT_TAB", R.id.nav_home);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_ai) {
                    Intent intent = new Intent(QuizHistoryActivity.this, AiAssistantActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_progress) {
                    Intent intent = new Intent(QuizHistoryActivity.this, LearningProgressActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(QuizHistoryActivity.this, HomeDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("SELECT_TAB", R.id.nav_profile);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });

        binding.bottomNavigation.setSelectedItemId(R.id.nav_progress);
    }
}
