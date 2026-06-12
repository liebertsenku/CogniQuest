package com.example.cogniquest.ui.admin;
import com.example.cogniquest.R;
import com.example.cogniquest.model.Question;
import com.example.cogniquest.model.Quiz;
import com.example.cogniquest.database.DatabaseHelper;
import com.example.cogniquest.ui.adapter.QuestionAdapter;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cogniquest.databinding.ActivityManageQuestionsBinding;

import java.util.List;

public class ManageQuestionsActivity extends AppCompatActivity {

    private ActivityManageQuestionsBinding binding;
    private DatabaseHelper databaseHelper;
    private QuestionAdapter questionAdapter;
    private List<Question> questionList;
    private int quizId;
    private String quizTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Handle dark mode setting
        android.content.SharedPreferences prefs = getSharedPreferences("admin_pref", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        int targetMode = isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        if (AppCompatDelegate.getDefaultNightMode() != targetMode) {
            AppCompatDelegate.setDefaultNightMode(targetMode);
        }

        super.onCreate(savedInstanceState);
        binding = ActivityManageQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        // Get intents
        quizId = getIntent().getIntExtra("quiz_id", -1);
        quizTitle = getIntent().getStringExtra("quiz_title");

        binding.tvQuizTitleHeader.setText(quizTitle);
        binding.tvQuizDescSubtitle.setText("Questions for quiz: " + quizTitle);

        setupRecyclerView();

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.addQuestionFab.setOnClickListener(v -> showQuestionDialog(null));
    }

    private void setupRecyclerView() {
        questionList = databaseHelper.getQuestionsForQuiz(quizId);
        questionAdapter = new QuestionAdapter(questionList, new QuestionAdapter.OnQuestionClickListener() {
            @Override
            public void onEditClick(Question question) {
                showQuestionDialog(question);
            }

            @Override
            public void onDeleteClick(Question question) {
                confirmDelete(question);
            }
        });
        binding.recyclerViewQuestions.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewQuestions.setAdapter(questionAdapter);
    }

    private void refreshData() {
        questionList = databaseHelper.getQuestionsForQuiz(quizId);
        questionAdapter.updateData(questionList);
        
        // Optionally update the questions count in the quiz object automatically
        Quiz quiz = null;
        List<Quiz> quizzes = databaseHelper.getAllQuizzes();
        for (Quiz q : quizzes) {
            if (q.getId() == quizId) {
                quiz = q;
                break;
            }
        }
        if (quiz != null) {
            quiz.setQuestionsCount(questionList.size());
            databaseHelper.updateQuiz(quiz);
        }
    }

    private void confirmDelete(Question question) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Question")
                .setMessage("Are you sure you want to delete this question?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    databaseHelper.deleteQuestion(question.getId());
                    refreshData();
                    Toast.makeText(this, "Question deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showQuestionDialog(Question question) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(question == null ? "Add New Question" : "Edit Question");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText etText = new EditText(this);
        etText.setHint("Question Text");
        layout.addView(etText);

        final EditText etOptionA = new EditText(this);
        etOptionA.setHint("Option A");
        layout.addView(etOptionA);

        final EditText etOptionB = new EditText(this);
        etOptionB.setHint("Option B");
        layout.addView(etOptionB);

        final EditText etOptionC = new EditText(this);
        etOptionC.setHint("Option C");
        layout.addView(etOptionC);

        final EditText etOptionD = new EditText(this);
        etOptionD.setHint("Option D");
        layout.addView(etOptionD);

        // Correct Option Spinner
        final Spinner spinnerCorrect = new Spinner(this);
        String[] options = {"A", "B", "C", "D"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, options);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCorrect.setAdapter(spinnerAdapter);
        layout.addView(spinnerCorrect);

        if (question != null) {
            etText.setText(question.getQuestionText());
            etOptionA.setText(question.getOptionA());
            etOptionB.setText(question.getOptionB());
            etOptionC.setText(question.getOptionC());
            etOptionD.setText(question.getOptionD());
            
            String corr = question.getCorrectOption();
            int pos = 0;
            for (int i = 0; i < options.length; i++) {
                if (options[i].equalsIgnoreCase(corr)) {
                    pos = i;
                    break;
                }
            }
            spinnerCorrect.setSelection(pos);
        }

        builder.setView(layout);

        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", null);

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
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (question == null) {
                // Add new
                DatabaseHelper db = new DatabaseHelper(this);
                db.addQuestion(new Question(quizId, text, optA, optB, optC, optD, correct));
                Toast.makeText(this, "Question added", Toast.LENGTH_SHORT).show();
            } else {
                // Edit
                question.setQuestionText(text);
                question.setOptionA(optA);
                question.setOptionB(optB);
                question.setOptionC(optC);
                question.setOptionD(optD);
                question.setCorrectOption(correct);
                DatabaseHelper db = new DatabaseHelper(this);
                db.updateQuestion(question);
                Toast.makeText(this, "Question updated", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
            refreshData();
        });
    }
}
