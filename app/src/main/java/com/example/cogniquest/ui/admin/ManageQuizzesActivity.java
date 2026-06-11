package com.example.cogniquest.ui.admin;
import com.example.cogniquest.R;
import com.example.cogniquest.model.Question;
import com.example.cogniquest.model.Quiz;
import com.example.cogniquest.model.Difficulty;
import com.example.cogniquest.database.DatabaseHelper;
import com.example.cogniquest.utils.UserManager;
import com.example.cogniquest.ui.adapter.QuizAdapter;
import com.example.cogniquest.ui.auth.LoginActivity;

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
                    overridePendingTransition(0, 0);
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

        if (binding.etSearch != null) {
            binding.etSearch.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (quizAdapter != null) {
                        quizAdapter.filter(s.toString(), quizList);
                    }
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }
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

        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String cat = etCategory.getText().toString().trim();
            String qStr = etQuestions.getText().toString().trim();
            Difficulty difficulty = (Difficulty) spinnerDifficulty.getSelectedItem();

            if (title.isEmpty() || cat.isEmpty() || qStr.isEmpty()) {
                Toast.makeText(this, "Semua bidang wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            int qCount;
            try {
                qCount = Integer.parseInt(qStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Jumlah pertanyaan harus berupa angka", Toast.LENGTH_SHORT).show();
                return;
            }

            if (quiz == null) {
                if (qCount <= 0) {
                    Toast.makeText(this, "Jumlah pertanyaan minimal harus 1", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                startQuestionWizard(title, desc, cat, qCount, difficulty);
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
                dialog.dismiss();
                refreshData();
            }
        });
    }

    private void startQuestionWizard(String title, String desc, String cat, int totalQuestions, Difficulty difficulty) {
        java.util.List<Question> tempQuestions = new java.util.ArrayList<>();
        showQuestionWizardStep(title, desc, cat, totalQuestions, difficulty, tempQuestions, 1);
    }

    private void showQuestionWizardStep(String title, String desc, String cat, int totalQuestions, Difficulty difficulty, java.util.List<Question> tempQuestions, int currentStep) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah Pertanyaan (" + currentStep + "/" + totalQuestions + ")");
        builder.setCancelable(false);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText etText = new EditText(this);
        etText.setHint("Teks Pertanyaan");
        layout.addView(etText);

        final EditText etOptionA = new EditText(this);
        etOptionA.setHint("Pilihan A");
        layout.addView(etOptionA);

        final EditText etOptionB = new EditText(this);
        etOptionB.setHint("Pilihan B");
        layout.addView(etOptionB);

        final EditText etOptionC = new EditText(this);
        etOptionC.setHint("Pilihan C");
        layout.addView(etOptionC);

        final EditText etOptionD = new EditText(this);
        etOptionD.setHint("Pilihan D");
        layout.addView(etOptionD);

        final android.widget.Spinner spinnerCorrect = new android.widget.Spinner(this);
        String[] options = {"A", "B", "C", "D"};
        android.widget.ArrayAdapter<String> spinnerAdapter = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, options);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCorrect.setAdapter(spinnerAdapter);
        layout.addView(spinnerCorrect);

        builder.setView(layout);

        builder.setPositiveButton(currentStep == totalQuestions ? "Selesai" : "Lanjut", null);

        builder.setNegativeButton("Batal Buat Kuis", (dialog, which) -> {
            new AlertDialog.Builder(this)
                .setTitle("Batal Membuat Kuis?")
                .setMessage("Semua pertanyaan yang telah dimasukkan akan terhapus.")
                .setPositiveButton("Ya, Batal", (d, w) -> {
                    Toast.makeText(this, "Pembuatan kuis dibatalkan", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Kembali", (d, w) -> {
                    showQuestionWizardStep(title, desc, cat, totalQuestions, difficulty, tempQuestions, currentStep);
                })
                .show();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String text = etText.getText().toString().trim();
            String optA = etOptionA.getText().toString().trim();
            String optB = etOptionB.getText().toString().trim();
            String optC = etOptionC.getText().toString().trim();
            String optD = etOptionD.getText().toString().trim();
            String correct = (String) spinnerCorrect.getSelectedItem();

            if (text.isEmpty() || optA.isEmpty() || optB.isEmpty() || optC.isEmpty() || optD.isEmpty()) {
                Toast.makeText(this, "Semua bidang pertanyaan harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            tempQuestions.add(new Question(-1, text, optA, optB, optC, optD, correct));
            dialog.dismiss();

            if (currentStep < totalQuestions) {
                showQuestionWizardStep(title, desc, cat, totalQuestions, difficulty, tempQuestions, currentStep + 1);
            } else {
                DatabaseHelper db = new DatabaseHelper(this);
                long newQuizId = db.addQuiz(new Quiz(title, desc, cat, totalQuestions, difficulty));
                
                if (newQuizId != -1) {
                    for (Question q : tempQuestions) {
                        q.setQuizId((int) newQuizId);
                        db.addQuestion(q);
                    }
                    Toast.makeText(this, "Kuis berhasil dibuat dengan " + totalQuestions + " pertanyaan!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Gagal membuat kuis", Toast.LENGTH_SHORT).show();
                }
                refreshData();
            }
        });
    }
}
