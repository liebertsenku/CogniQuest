package com.example.cogniquest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cogniquest.databinding.ActivityLearningProgressBinding;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Locale;

public class LearningProgressActivity extends AppCompatActivity {

    private ActivityLearningProgressBinding binding;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLearningProgressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(LearningProgressActivity.this, HomeDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("SELECT_TAB", R.id.nav_home);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_progress) {
                    return true; // Already here
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(LearningProgressActivity.this, HomeDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("SELECT_TAB", R.id.nav_profile);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_ai) {
                    Intent intent = new Intent(LearningProgressActivity.this, AiAssistantActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });

        binding.bottomNavigation.setSelectedItemId(R.id.nav_progress);

        // Load quizzes
        java.util.List<Quiz> quizList = databaseHelper.getAllQuizzes();
        
        QuizAdapter quizAdapter = new QuizAdapter(quizList, new QuizAdapter.OnQuizClickListener() {
            @Override
            public void onEditClick(Quiz quiz) {}

            @Override
            public void onDeleteClick(Quiz quiz) {}

            @Override
            public void onCardClick(Quiz quiz) {
                Intent intent = new Intent(LearningProgressActivity.this, QuizSessionActivity.class);
                intent.putExtra("quiz_id", quiz.getId());
                intent.putExtra("quiz_title", quiz.getTitle());
                intent.putExtra("quiz_category", quiz.getCategory());
                startActivity(intent);
            }
        }, false); // Pass false to hide edit/delete admin controls

        binding.recyclerViewQuizzes.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        binding.recyclerViewQuizzes.setAdapter(quizAdapter);

        binding.tvViewAllHistory.setOnClickListener(v -> {
            Intent intent = new Intent(LearningProgressActivity.this, QuizHistoryActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDynamicStats(databaseHelper);
    }

    private void loadDynamicStats(DatabaseHelper dbHelper) {
        UserManager userManager = new UserManager(this);
        String username = userManager.getUsername();
        List<QuizHistory> history = dbHelper.getQuizHistory(username);

        int totalQuestions = 0;
        int totalCorrect = 0;
        Set<String> uniqueDates = new HashSet<>();

        for (QuizHistory h : history) {
            totalQuestions += h.getTotalQuestions();
            totalCorrect += h.getScore();
            String ts = h.getTimestamp();
            if (ts != null && ts.contains(",")) {
                uniqueDates.add(ts.split(",")[0].trim());
            }
        }

        // Progress Overview
        int accuracy = 0;
        if (totalQuestions > 0) {
            accuracy = Math.round(((float) totalCorrect / totalQuestions) * 100f);
        }
        binding.tvProgressPercent.setText(accuracy + "%");
        binding.progressBarOverview.setProgress(accuracy);

        if (accuracy >= 80) {
            binding.tvProgressSubtitle.setText("You're doing great! Keep up the momentum!");
        } else if (accuracy >= 50) {
            binding.tvProgressSubtitle.setText("You're making solid progress. Keep practicing!");
        } else if (history.size() > 0) {
            binding.tvProgressSubtitle.setText("Keep going! Every practice session helps.");
        } else {
            binding.tvProgressSubtitle.setText("No quizzes taken yet. Start learning!");
        }

        // Learning Streak
        int streakDays = uniqueDates.size();
        binding.tvCurrentStreak.setText("Current Streak: " + streakDays + " Days");
        binding.tvBestStreak.setText(Math.max(streakDays, 0) + " Days");

        // Goal Tracking
        int sessions = history.size();
        double hoursVal = (sessions * 5.0) / 60.0;
        
        double dailyProgress = Math.min(hoursVal, 2.0);
        int dailyPercent = (int) Math.round((dailyProgress / 2.0) * 100);
        binding.progressBarDaily.setProgress(dailyPercent);
        binding.tvDailyGoalLeft.setText(String.format(Locale.US, "%.1fh done", dailyProgress));

        double weeklyProgress = Math.min(hoursVal, 15.0);
        int weeklyPercent = (int) Math.round((weeklyProgress / 15.0) * 100);
        binding.progressBarWeekly.setProgress(weeklyPercent);
        binding.tvWeeklyGoalLeft.setText(String.format(Locale.US, "%.1fh done", weeklyProgress));

        // Recent History
        if (!history.isEmpty()) {
            binding.cvRecentQuiz.setVisibility(android.view.View.VISIBLE);
            QuizHistory latest = history.get(0);
            binding.tvRecentQuizTitle.setText(latest.getQuizTitle());
            binding.tvRecentQuizSubtitle.setText(latest.getTimestamp() + " • " + latest.getScore() + "/" + latest.getTotalQuestions() + " correct");
        } else {
            binding.cvRecentQuiz.setVisibility(android.view.View.GONE);
        }
    }
}
