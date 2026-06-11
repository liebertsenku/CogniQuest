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

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

        binding.notificationButton.setOnClickListener(v -> showNotificationDialog());
    }

    private void showNotificationDialog() {
        if (getContext() == null) return;
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_notifications, null);
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        dialogView.findViewById(R.id.btnCloseNotifications).setOnClickListener(v2 -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (databaseHelper != null) {
            loadDynamicStats(databaseHelper);
        }
        updateAvatar();
    }

    private void updateAvatar() {
        if (getContext() == null || binding == null) return;
        UserManager userManager = new UserManager(requireContext());
        String avatarUri = userManager.getAvatarUri();
        if (avatarUri != null && !avatarUri.isEmpty()) {
            binding.profileImage.setImageURI(android.net.Uri.parse(avatarUri));
        } else {
            binding.profileImage.setImageResource(R.drawable.ic_profile);
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
        SharedPreferences prefs = requireContext().getSharedPreferences("CogniQuest_Prefs", Context.MODE_PRIVATE);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEE", Locale.US);

        Calendar todayCal = Calendar.getInstance();
        String todayStr = dateFormat.format(todayCal.getTime());

        Set<String> accessedDates = prefs.getStringSet("accessed_dates", new HashSet<>());
        Set<String> activeDates = new HashSet<>(uniqueDates);
        Set<String> updatedAccess = new HashSet<>(accessedDates);
        updatedAccess.add(todayStr);
        prefs.edit().putStringSet("accessed_dates", updatedAccess).apply();
        activeDates.addAll(updatedAccess);

        // Calculate consecutive streak
        int currentStreak = calculateCurrentStreak(activeDates);
        int bestStreak = prefs.getInt("best_streak", 0);
        if (currentStreak > bestStreak) {
            bestStreak = currentStreak;
            prefs.edit().putInt("best_streak", bestStreak).apply();
        }
        binding.tvCurrentStreak.setText("Current Streak: " + currentStreak + " Days");
        binding.tvBestStreak.setText(Math.max(bestStreak, currentStreak) + " Days");

        // Dynamic week days visualization
        int todayDayOfWeek = todayCal.get(Calendar.DAY_OF_WEEK);
        int daysFromMonday = todayDayOfWeek - Calendar.MONDAY;
        if (daysFromMonday < 0) {
            daysFromMonday += 7;
        }

        Calendar weekCal = (Calendar) todayCal.clone();
        weekCal.add(Calendar.DAY_OF_YEAR, -daysFromMonday);

        String[] weekDayDates = new String[7];
        String[] weekDayNames = new String[7];
        for (int i = 0; i < 7; i++) {
            Date d = weekCal.getTime();
            weekDayDates[i] = dateFormat.format(d);
            weekDayNames[i] = dayNameFormat.format(d);
            weekCal.add(Calendar.DAY_OF_YEAR, 1);
        }

        android.widget.TextView[] dayTextViews = new android.widget.TextView[]{
            binding.tvDay1, binding.tvDay2, binding.tvDay3, binding.tvDay4, binding.tvDay5, binding.tvDay6, binding.tvDay7
        };
        android.widget.ImageView[] dayImageViews = new android.widget.ImageView[]{
            binding.ivDay1, binding.ivDay2, binding.ivDay3, binding.ivDay4, binding.ivDay5, binding.ivDay6, binding.ivDay7
        };

        for (int i = 0; i < 7; i++) {
            android.widget.TextView tv = dayTextViews[i];
            android.widget.ImageView iv = dayImageViews[i];
            if (tv == null || iv == null) continue;

            tv.setText(weekDayNames[i]);
            int pad = (int) (6 * getResources().getDisplayMetrics().density);
            iv.setPadding(pad, pad, pad, pad);

            if (i < daysFromMonday) {
                // Past day
                tv.setTypeface(null, android.graphics.Typeface.NORMAL);
                if (activeDates.contains(weekDayDates[i])) {
                    tv.setTextColor(getResources().getColor(R.color.on_surface_variant));
                    iv.setBackgroundResource(R.drawable.bg_circle_solid_purple);
                    iv.setImageResource(R.drawable.ic_check_white);
                } else {
                    tv.setTextColor(getResources().getColor(R.color.outline));
                    iv.setBackgroundResource(R.drawable.bg_circle_inactive);
                    iv.setImageDrawable(null);
                }
            } else if (i == daysFromMonday) {
                // Today
                tv.setTypeface(null, android.graphics.Typeface.BOLD);
                tv.setTextColor(getResources().getColor(R.color.on_surface));
                iv.setBackgroundResource(R.drawable.bg_circle_light_purple);
                iv.setImageResource(R.drawable.ic_lightning_purple);
            } else {
                // Future day
                tv.setTypeface(null, android.graphics.Typeface.NORMAL);
                tv.setTextColor(getResources().getColor(R.color.outline));
                iv.setBackgroundResource(R.drawable.bg_circle_inactive);
                iv.setImageDrawable(null);
            }
        }

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

        // AI Learning Tips
        String[] aiTips = new String[]{
            "You learn most effectively between 9:00 AM - 11:00 AM. Consider scheduling your deep work sessions then.",
            "Taking a 5-minute break after every 25 minutes of study can significantly improve retention.",
            "Reviewing incorrect answers immediately after a quiz helps reinforce the correct concepts.",
            "Consistency is key! A 15-minute daily session is better than cramming for 2 hours once a week.",
            "Mix up your topics. Interleaving different subjects can enhance problem-solving skills.",
            "Teaching a concept you just learned to someone else is one of the best ways to master it.",
            "Stay hydrated and well-rested. Your brain processes and stores information while you sleep!"
        };
        int randomTipIndex = (int) (Math.random() * aiTips.length);
        if (binding.tvAiTip != null) {
            binding.tvAiTip.setText(aiTips[randomTipIndex]);
        }
    }

    private int calculateCurrentStreak(Set<String> activeDates) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        Calendar cal = Calendar.getInstance();
        int streak = 0;
        
        // Check today
        String todayStr = dateFormat.format(cal.getTime());
        if (activeDates.contains(todayStr)) {
            streak++;
            cal.add(Calendar.DAY_OF_YEAR, -1);
            while (activeDates.contains(dateFormat.format(cal.getTime()))) {
                streak++;
                cal.add(Calendar.DAY_OF_YEAR, -1);
            }
        } else {
            // Check yesterday
            cal.add(Calendar.DAY_OF_YEAR, -1);
            String yesterdayStr = dateFormat.format(cal.getTime());
            if (activeDates.contains(yesterdayStr)) {
                streak++;
                cal.add(Calendar.DAY_OF_YEAR, -1);
                while (activeDates.contains(dateFormat.format(cal.getTime()))) {
                    streak++;
                    cal.add(Calendar.DAY_OF_YEAR, -1);
                }
            }
        }
        return streak;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
