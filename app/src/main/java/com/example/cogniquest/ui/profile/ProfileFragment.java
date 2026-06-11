package com.example.cogniquest.ui.profile;
import com.example.cogniquest.R;
import com.example.cogniquest.model.QuizHistory;
import com.example.cogniquest.database.DatabaseHelper;
import com.example.cogniquest.utils.UserManager;
import com.example.cogniquest.ui.auth.LoginActivity;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.graphics.Color;
import android.content.res.ColorStateList;

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

        com.google.android.material.imageview.ShapeableImageView profileImageSmall = view.findViewById(R.id.profileImageSmall);
        com.google.android.material.imageview.ShapeableImageView profileImageLarge = view.findViewById(R.id.profileImageLarge);
        updateProfileImages(profileImageSmall, profileImageLarge);

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

        View shareProfileButton = view.findViewById(R.id.shareProfileButton);
        if (shareProfileButton != null) {
            shareProfileButton.setOnClickListener(v -> shareProfile());
        }

        loadProfileStats(view);

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
            com.google.android.material.imageview.ShapeableImageView profileImageSmall = getView().findViewById(R.id.profileImageSmall);
            com.google.android.material.imageview.ShapeableImageView profileImageLarge = getView().findViewById(R.id.profileImageLarge);
            updateProfileImages(profileImageSmall, profileImageLarge);
            loadProfileStats(getView());
        }
    }

    private void shareProfile() {
        if (getContext() == null || userManager == null) return;
        
        String username = userManager.getFullname();
        String bio = userManager.getBio();
        
        TextView tvAccuracy = getView() != null ? getView().findViewById(R.id.tvProfileAccuracy) : null;
        TextView tvQuestions = getView() != null ? getView().findViewById(R.id.tvProfileQuestions) : null;
        
        String accuracy = tvAccuracy != null ? tvAccuracy.getText().toString() : "0%";
        String questions = tvQuestions != null ? tvQuestions.getText().toString() : "0";
        
        String shareText = "Check out my progress on CogniQuest!\n\n" +
                "👤 Name: " + username + "\n" +
                "🎓 Level: " + bio + "\n" +
                "🎯 Accuracy: " + accuracy + "\n" +
                "📚 Questions Answered: " + questions + "\n\n" +
                "Join me on CogniQuest today!";
                
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My CogniQuest Profile");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        
        startActivity(Intent.createChooser(shareIntent, "Share Profile via"));
    }

    private void loadProfileStats(View view) {
        if (view == null) return;
        TextView tvStreakXP = view.findViewById(R.id.tvProfileStreakXP);
        TextView tvAccuracy = view.findViewById(R.id.tvProfileAccuracy);
        TextView tvQuestions = view.findViewById(R.id.tvProfileQuestions);
        TextView tvScholarInfo = view.findViewById(R.id.scholarInfo);
        
        if (tvStreakXP == null && tvAccuracy == null && tvQuestions == null) return;
        
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        String username = userManager.getUsername();
        
        new Thread(() -> {
            List<QuizHistory> history = dbHelper.getQuizHistory(username);
            int totalQuestions = 0;
            int totalCorrect = 0;
            int sessions = history.size();
            for (QuizHistory h : history) {
                totalQuestions += h.getTotalQuestions();
                totalCorrect += h.getScore();
            }
            
            int accuracy = 0;
            if (totalQuestions > 0) {
                accuracy = Math.round(((float) totalCorrect / totalQuestions) * 100f);
            }
            
            int xp = totalCorrect * 10;
            
            int streakDays = 0;
            java.util.Set<String> uniqueDates = new java.util.HashSet<>();
            for (QuizHistory h : history) {
                String ts = h.getTimestamp();
                if (ts != null && ts.contains(",")) {
                    String datePart = ts.split(",")[0].trim();
                    uniqueDates.add(datePart);
                }
            }
            streakDays = uniqueDates.size();
            
            final int finalQuestions = totalQuestions;
            final int finalAccuracy = accuracy;
            final int finalXp = xp;
            final int finalStreak = streakDays;
            
            // Group by category to find Top Categories
            Map<String, int[]> categoryStats = new HashMap<>(); // [totalCorrect, totalQuestions]
            for (QuizHistory h : history) {
                String cat = h.getCategory() != null ? h.getCategory() : "Uncategorized";
                if (!categoryStats.containsKey(cat)) {
                    categoryStats.put(cat, new int[]{0, 0});
                }
                categoryStats.get(cat)[0] += h.getScore();
                categoryStats.get(cat)[1] += h.getTotalQuestions();
            }

            List<Map.Entry<String, int[]>> sortedCategories = new ArrayList<>(categoryStats.entrySet());
            sortedCategories.sort((a, b) -> {
                int accA = a.getValue()[1] > 0 ? (a.getValue()[0] * 100) / a.getValue()[1] : 0;
                int accB = b.getValue()[1] > 0 ? (b.getValue()[0] * 100) / b.getValue()[1] : 0;
                return Integer.compare(accB, accA); // Descending
            });

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (tvStreakXP != null) {
                        tvStreakXP.setText(finalStreak + " 🔥 " + finalXp + " XP");
                    }
                    if (tvAccuracy != null) {
                        tvAccuracy.setText(finalAccuracy + "%");
                    }
                    if (tvQuestions != null) {
                        tvQuestions.setText(String.valueOf(finalQuestions));
                    }
                    if (tvScholarInfo != null) {
                        tvScholarInfo.setText("XP: " + finalXp + " • Completed Quests: " + sessions);
                    }
                    
                    LinearLayout llTopCategoriesContainer = view.findViewById(R.id.llTopCategoriesContainer);
                    if (llTopCategoriesContainer != null) {
                        // Remove existing dynamic views (keep the title TextView which is at index 0)
                        int childCount = llTopCategoriesContainer.getChildCount();
                        if (childCount > 1) {
                            llTopCategoriesContainer.removeViews(1, childCount - 1);
                        }
                        
                        int[] colors = {
                            android.graphics.Color.parseColor("#4A3B9C"),
                            android.graphics.Color.parseColor("#007B8C"),
                            android.graphics.Color.parseColor("#4BC8F2")
                        };
                        
                        int limit = Math.min(3, sortedCategories.size());
                        for (int i = 0; i < limit; i++) {
                            Map.Entry<String, int[]> entry = sortedCategories.get(i);
                            String catName = entry.getKey();
                            int cAcc = entry.getValue()[1] > 0 ? (entry.getValue()[0] * 100) / entry.getValue()[1] : 0;
                            int tintColor = colors[i % colors.length];
                            
                            LinearLayout catLayout = new LinearLayout(requireContext());
                            catLayout.setOrientation(LinearLayout.VERTICAL);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(0, 0, 0, dpToPx(12));
                            catLayout.setLayoutParams(layoutParams);
                            
                            RelativeLayout rl = new RelativeLayout(requireContext());
                            rl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            rl.setPadding(0, 0, 0, dpToPx(4));
                            
                            TextView tvCat = new TextView(requireContext());
                            tvCat.setText(catName);
                            tvCat.setTextSize(14);
                            tvCat.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
                            
                            TextView tvPct = new TextView(requireContext());
                            tvPct.setText(cAcc + "%");
                            tvPct.setTextSize(14);
                            tvPct.setTextColor(tintColor);
                            tvPct.setTypeface(null, android.graphics.Typeface.BOLD);
                            RelativeLayout.LayoutParams pctParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            pctParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                            tvPct.setLayoutParams(pctParams);
                            
                            rl.addView(tvCat);
                            rl.addView(tvPct);
                            
                            ProgressBar pb = new ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal);
                            pb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(6)));
                            pb.setMax(100);
                            pb.setProgress(cAcc);
                            pb.setProgressTintList(ColorStateList.valueOf(tintColor));
                            pb.setProgressBackgroundTintList(ColorStateList.valueOf(Color.argb(26, Color.red(tintColor), Color.green(tintColor), Color.blue(tintColor))));
                            
                            catLayout.addView(rl);
                            catLayout.addView(pb);
                            
                            llTopCategoriesContainer.addView(catLayout);
                        }
                        
                        if (sortedCategories.isEmpty()) {
                            TextView tvEmpty = new TextView(requireContext());
                            tvEmpty.setText("No quizzes taken yet.");
                            tvEmpty.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
                            llTopCategoriesContainer.addView(tvEmpty);
                        }
                    }
                });
            }
        }).start();
    }

    private void updateProfileImages(com.google.android.material.imageview.ShapeableImageView small, com.google.android.material.imageview.ShapeableImageView large) {
        String avatarUri = userManager.getAvatarUri();
        if (avatarUri != null && !avatarUri.isEmpty()) {
            android.net.Uri uri = android.net.Uri.parse(avatarUri);
            if (small != null) {
                small.setImageURI(uri);
            }
            if (large != null) {
                large.setImageURI(uri);
            }
        } else {
            if (small != null) {
                small.setImageResource(R.drawable.ic_profile);
            }
            if (large != null) {
                large.setImageResource(R.drawable.ic_profile);
            }
        }
    }

    private int dpToPx(float dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
