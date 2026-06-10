package com.example.cogniquest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.cogniquest.databinding.ActivityManageQuizzesBinding;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

public class ManageQuizzesActivity extends AppCompatActivity {

    private ActivityManageQuizzesBinding binding;
    private DatabaseHelper databaseHelper;
    private QuizAdapter quizAdapter;
    private List<Quiz> quizList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        android.content.SharedPreferences prefs = getSharedPreferences("admin_pref", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        int targetMode = isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        if (AppCompatDelegate.getDefaultNightMode() != targetMode) {
            AppCompatDelegate.setDefaultNightMode(targetMode);
        }

        super.onCreate(savedInstanceState);
        binding = ActivityManageQuizzesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        setupRecyclerView();

        binding.addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuizDialog(null);
            }
        });

        if (getIntent().getBooleanExtra("trigger_add", false)) {
            showQuizDialog(null);
        }

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_manage_quizzes) {
                    return true;
                } else if (itemId == R.id.nav_home) {
                    startActivity(new Intent(ManageQuizzesActivity.this, AdminDashboardActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_logout) {
                    UserManager userManager = new UserManager(ManageQuizzesActivity.this);
                    userManager.logout();
                    Intent intent = new Intent(ManageQuizzesActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });

        binding.bottomNavigation.setSelectedItemId(R.id.nav_manage_quizzes);
    }

    private void setupRecyclerView() {
        quizList = databaseHelper.getAllQuizzes();
        quizAdapter = new QuizAdapter(quizList, new QuizAdapter.OnQuizClickListener() {
            @Override
            public void onEditClick(Quiz quiz) {
                showQuizDialog(quiz);
            }

            @Override
            public void onDeleteClick(Quiz quiz) {
                confirmDelete(quiz);
            }

            @Override
            public void onCardClick(Quiz quiz) {
                Intent intent = new Intent(ManageQuizzesActivity.this, ManageQuestionsActivity.class);
                intent.putExtra("quiz_id", quiz.getId());
                intent.putExtra("quiz_title", quiz.getTitle());
                startActivity(intent);
            }
        });
        binding.recyclerViewQuizzes.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewQuizzes.setAdapter(quizAdapter);
    }

    private void refreshData() {
        quizList = databaseHelper.getAllQuizzes();
        quizAdapter.updateData(quizList);
    }

    private void confirmDelete(Quiz quiz) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Quiz")
                .setMessage("Are you sure you want to delete '" + quiz.getTitle() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    databaseHelper.deleteQuiz(quiz.getId());
                    refreshData();
                    Toast.makeText(this, "Quiz deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showQuizDialog(Quiz quiz) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(quiz == null ? "Add New Quiz" : "Edit Quiz");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText etTitle = new EditText(this);
        etTitle.setHint("Title");
        layout.addView(etTitle);

        final EditText etDesc = new EditText(this);
        etDesc.setHint("Description");
        layout.addView(etDesc);

        final EditText etCategory = new EditText(this);
        etCategory.setHint("Category (e.g. Science)");
        layout.addView(etCategory);

        final EditText etQuestions = new EditText(this);
        etQuestions.setHint("Number of Questions");
        etQuestions.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(etQuestions);

        final android.widget.Spinner spinnerDifficulty = new android.widget.Spinner(this);
        android.widget.ArrayAdapter<Difficulty> spinnerAdapter = new android.widget.ArrayAdapter<>(this,
                R.layout.spinner_item, Difficulty.values());
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerDifficulty.setAdapter(spinnerAdapter);
        layout.addView(spinnerDifficulty);

        if (quiz != null) {
            etTitle.setText(quiz.getTitle());
            etDesc.setText(quiz.getDescription());
            etCategory.setText(quiz.getCategory());
            etQuestions.setText(String.valueOf(quiz.getQuestionsCount()));
            if (quiz.getDifficulty() != null) {
                int pos = spinnerAdapter.getPosition(quiz.getDifficulty());
                spinnerDifficulty.setSelection(pos);
            }
        }

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String cat = etCategory.getText().toString().trim();
            String qStr = etQuestions.getText().toString().trim();
            Difficulty difficulty = (Difficulty) spinnerDifficulty.getSelectedItem();

            if (title.isEmpty() || cat.isEmpty() || qStr.isEmpty()) {
                Toast.makeText(this, "Title, Category, and Question Count required", Toast.LENGTH_SHORT).show();
                return;
            }

            int qCount = Integer.parseInt(qStr);

            if (quiz == null) {
                // Add new
                DatabaseHelper db = new DatabaseHelper(this);
                db.addQuiz(new Quiz(title, desc, cat, qCount, difficulty));
                Toast.makeText(this, "Quiz added", Toast.LENGTH_SHORT).show();
            } else {
                // Edit
                quiz.setTitle(title);
                quiz.setDescription(desc);
                quiz.setCategory(cat);
                quiz.setQuestionsCount(qCount);
                quiz.setDifficulty(difficulty);
                DatabaseHelper db = new DatabaseHelper(this);
                db.updateQuiz(quiz);
                Toast.makeText(this, "Quiz updated", Toast.LENGTH_SHORT).show();
            }
            refreshData();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
