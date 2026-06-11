package com.example.cogniquest.ui.quiz;
import com.example.cogniquest.R;
import com.example.cogniquest.model.Quiz;
import com.example.cogniquest.model.QuizHistory;
import com.example.cogniquest.database.DatabaseHelper;
import com.example.cogniquest.utils.UserManager;
import com.example.cogniquest.ui.adapter.QuizHistoryAdapter;
import com.example.cogniquest.ui.dashboard.HomeDashboardActivity;

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
                    Intent intent = new Intent(QuizHistoryActivity.this, HomeDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("SELECT_TAB", R.id.nav_ai);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_progress) {
                    Intent intent = new Intent(QuizHistoryActivity.this, HomeDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("SELECT_TAB", R.id.nav_progress);
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

        UserManager userManager = new UserManager(this);
        String avatarUri = userManager.getAvatarUri();
        if (avatarUri != null && !avatarUri.isEmpty()) {
            binding.profileImage.setImageURI(android.net.Uri.parse(avatarUri));
        } else {
            binding.profileImage.setImageResource(R.drawable.ic_profile);
        }

        // Load Quiz History dynamically
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        String username = userManager.getUsername();
        java.util.List<QuizHistory> historyList = databaseHelper.getQuizHistory(username);

        QuizHistoryAdapter adapter = new QuizHistoryAdapter(historyList);
        binding.recyclerViewHistory.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        binding.recyclerViewHistory.setAdapter(adapter);
    }
}
