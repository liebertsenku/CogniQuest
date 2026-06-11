package com.example.cogniquest.ui.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cogniquest.databinding.FragmentLearningProgressBinding;
import com.example.cogniquest.database.DatabaseHelper;
import com.example.cogniquest.utils.UserManager;
import com.example.cogniquest.model.Quiz;
import com.example.cogniquest.model.QuizHistory;
import com.example.cogniquest.ui.adapter.QuizAdapter;
import com.example.cogniquest.ui.quiz.QuizSessionActivity;
import com.example.cogniquest.ui.quiz.QuizHistoryActivity;
import com.example.cogniquest.R;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Locale;

public class LearningProgressFragment extends Fragment {

    private FragmentLearningProgressBinding binding;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLearningProgressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());

        // Load quizzes
        List<Quiz> quizList = databaseHelper.getAllQuizzes();
        
        QuizAdapter quizAdapter = new QuizAdapter(quizList, new QuizAdapter.OnQuizClickListener() {
            @Override
            public void onEditClick(Quiz quiz) {}

            @Override
            public void onDeleteClick(Quiz quiz) {}

            @Override
            public void onCardClick(Quiz quiz) {
                Intent intent = new Intent(requireContext(), QuizSessionActivity.class);
                intent.putExtra("quiz_id", quiz.getId());
                intent.putExtra("quiz_title", quiz.getTitle());
                intent.putExtra("quiz_category", quiz.getCategory());
                startActivity(intent);
            }
        }, false);

        binding.recyclerViewQuizzes.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(requireContext()));
        binding.recyclerViewQuizzes.setAdapter(quizAdapter);

        binding.tvViewAllHistory.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), QuizHistoryActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (databaseHelper != null) {
            loadDynamicStats(databaseHelper);
        }
    }

    private void loadDynamicStats(DatabaseHelper dbHelper) {
        if (getContext() == null || binding == null) return;
        UserManager userManager = new UserManager(requireContext());
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
            binding.cvRecentQuiz.setVisibility(View.VISIBLE);
            QuizHistory latest = history.get(0);
            binding.tvRecentQuizTitle.setText(latest.getQuizTitle());
            binding.tvRecentQuizSubtitle.setText(latest.getTimestamp() + " • " + latest.getScore() + "/" + latest.getTotalQuestions() + " correct");
        } else {
            binding.cvRecentQuiz.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
