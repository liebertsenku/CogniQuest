package com.example.cogniquest;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cogniquest.databinding.ActivityQuizSessionBinding;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class QuizSessionActivity extends AppCompatActivity {

    private ActivityQuizSessionBinding binding;
    private DatabaseHelper databaseHelper;
    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private String selectedOption = ""; // "A", "B", "C", "D" or ""
    private int score = 0;
    private int quizId;
    private String quizTitle;
    private String quizCategory;
    private String username;

    private CountDownTimer countDownTimer;
    private static final long TIMER_DURATION = 20000; // 20 seconds
    private boolean isCheckingState = true; // true = user can answer; false = showing correction, next button active

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizSessionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        username = new UserManager(this).getUsername();

        quizId = getIntent().getIntExtra("quiz_id", -1);
        quizTitle = getIntent().getStringExtra("quiz_title");
        quizCategory = getIntent().getStringExtra("quiz_category");
        if (quizCategory == null) quizCategory = "Uncategorized";

        binding.tvCategoryChip.setText(quizTitle != null ? quizTitle.toUpperCase() : "QUIZ");

        loadQuestions();

        binding.exitButton.setOnClickListener(v -> confirmExit());

        // Option Clicks
        binding.optionA.setOnClickListener(v -> selectOption("A"));
        binding.optionB.setOnClickListener(v -> selectOption("B"));
        binding.optionC.setOnClickListener(v -> selectOption("C"));
        binding.optionD.setOnClickListener(v -> selectOption("D"));

        binding.checkAnswerButton.setOnClickListener(v -> handleActionButtonClick());
    }

    private void loadQuestions() {
        questionList = databaseHelper.getQuestionsForQuiz(quizId);
        if (questionList == null || questionList.isEmpty()) {
            Toast.makeText(this, "This quiz does not have any questions yet!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        displayQuestion();
    }

    private void displayQuestion() {
        isCheckingState = true;
        selectedOption = "";
        binding.checkAnswerButton.setText("Check Answer");

        Question question = questionList.get(currentQuestionIndex);
        binding.tvQuestionText.setText(question.getQuestionText());
        binding.tvOptionAText.setText(question.getOptionA());
        binding.tvOptionBText.setText(question.getOptionB());
        binding.tvOptionCText.setText(question.getOptionC());
        binding.tvOptionDText.setText(question.getOptionD());

        // Update Progress Header
        int qNum = currentQuestionIndex + 1;
        int total = questionList.size();
        binding.tvQuestionProgress.setText("Question " + qNum + " of " + total);
        binding.progressBarQuiz.setProgress((qNum * 100) / total);

        // Reset option styles to default
        resetOptionStyles();
        enableOptionClicks(true);

        // Start Countdown timer
        startTimer();
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(TIMER_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secs = (int) (millisUntilFinished / 1000);
                binding.tvTimer.setText(String.valueOf(secs));
            }

            @Override
            public void onFinish() {
                binding.tvTimer.setText("0");
                // Automatically lock answers and check (user gets it wrong)
                selectedOption = ""; // No answer selected
                gradeQuestion();
            }
        }.start();
    }

    private void selectOption(String option) {
        if (!isCheckingState) return; // Answer already locked/checked

        selectedOption = option;
        resetOptionStyles();

        // Highlight selected option card
        MaterialCardView selectedCard = getCardForOption(option);
        if (selectedCard != null) {
            int primaryColor = androidx.core.content.ContextCompat.getColor(this, R.color.primary);
            int selectedBg = Color.argb(26, Color.red(primaryColor), Color.green(primaryColor), Color.blue(primaryColor));
            
            selectedCard.setStrokeColor(primaryColor);
            selectedCard.setStrokeWidth(dpToPx(2));
            selectedCard.setCardBackgroundColor(selectedBg);
            
            // Highlight letter indicator
            View indicator = getIndicatorForOption(option);
            if (indicator instanceof android.widget.TextView) {
                ((android.widget.TextView) indicator).setTextColor(Color.WHITE);
                indicator.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
            }
        }
    }

    private void resetOptionStyles() {
        String[] options = {"A", "B", "C", "D"};
        int strokeColor = androidx.core.content.ContextCompat.getColor(this, R.color.outline_variant);
        int cardBgColor = androidx.core.content.ContextCompat.getColor(this, R.color.white);
        int indicatorTextColor = androidx.core.content.ContextCompat.getColor(this, R.color.outline);
        int indicatorBgColor = androidx.core.content.ContextCompat.getColor(this, R.color.surface_container);

        for (String op : options) {
            MaterialCardView card = getCardForOption(op);
            if (card != null) {
                card.setStrokeColor(strokeColor);
                card.setStrokeWidth(dpToPx(1.5f));
                card.setCardBackgroundColor(cardBgColor);
            }
            View indicator = getIndicatorForOption(op);
            if (indicator instanceof android.widget.TextView) {
                ((android.widget.TextView) indicator).setTextColor(indicatorTextColor);
                indicator.setBackgroundTintList(android.content.res.ColorStateList.valueOf(indicatorBgColor));
            }
        }
    }

    private void enableOptionClicks(boolean enabled) {
        binding.optionA.setClickable(enabled);
        binding.optionB.setClickable(enabled);
        binding.optionC.setClickable(enabled);
        binding.optionD.setClickable(enabled);
    }

    private MaterialCardView getCardForOption(String option) {
        switch (option) {
            case "A": return binding.optionA;
            case "B": return binding.optionB;
            case "C": return binding.optionC;
            case "D": return binding.optionD;
            default: return null;
        }
    }

    private View getIndicatorForOption(String option) {
        switch (option) {
            case "A": return binding.tvOptionAIndex;
            case "B": return binding.tvOptionBIndex;
            case "C": return binding.tvOptionCIndex;
            case "D": return binding.tvOptionDIndex;
            default: return null;
        }
    }

    private void handleActionButtonClick() {
        if (isCheckingState) {
            if (selectedOption.isEmpty()) {
                Toast.makeText(this, "Please select an answer!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            gradeQuestion();
        } else {
            // Next Question or Finish
            currentQuestionIndex++;
            if (currentQuestionIndex < questionList.size()) {
                displayQuestion();
            } else {
                finishQuiz();
            }
        }
    }

    private void gradeQuestion() {
        isCheckingState = false;
        enableOptionClicks(false);

        Question question = questionList.get(currentQuestionIndex);
        String correct = question.getCorrectOption();

        boolean isCorrect = selectedOption.equalsIgnoreCase(correct);
        
        boolean isDarkTheme = (getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK) 
            == android.content.res.Configuration.UI_MODE_NIGHT_YES;
            
        int correctColor = isDarkTheme ? Color.parseColor("#81c784") : Color.parseColor("#006a2c");
        int correctBg = Color.argb(26, Color.red(correctColor), Color.green(correctColor), Color.blue(correctColor));
        
        int errorColor = androidx.core.content.ContextCompat.getColor(this, R.color.error);
        int errorBg = Color.argb(26, Color.red(errorColor), Color.green(errorColor), Color.blue(errorColor));

        if (isCorrect) {
            score++;
            highlightAnswerState(correct, correctColor, correctBg);
        } else {
            highlightAnswerState(correct, correctColor, correctBg);
            if (!selectedOption.isEmpty()) {
                highlightAnswerState(selectedOption, errorColor, errorBg);
            }
        }

        // Change button text
        if (currentQuestionIndex == questionList.size() - 1) {
            binding.checkAnswerButton.setText("Finish Quiz");
        } else {
            binding.checkAnswerButton.setText("Next Question");
        }
    }

    private void highlightAnswerState(String option, int strokeColor, int bgColor) {
        MaterialCardView card = getCardForOption(option);
        if (card != null) {
            card.setStrokeColor(strokeColor);
            card.setStrokeWidth(dpToPx(2));
            card.setCardBackgroundColor(bgColor);
            
            View indicator = getIndicatorForOption(option);
            if (indicator instanceof android.widget.TextView) {
                ((android.widget.TextView) indicator).setTextColor(Color.WHITE);
                indicator.setBackgroundTintList(android.content.res.ColorStateList.valueOf(strokeColor));
            }
        }
    }

    private void finishQuiz() {
        // Save to Database History
        databaseHelper.addQuizHistory(username, quizTitle, quizCategory, score, questionList.size());

        // Show score Dialog
        new AlertDialog.Builder(this)
                .setTitle("Quiz Completed!")
                .setMessage("Great job! You scored " + score + " out of " + questionList.size() + ".")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> finish())
                .show();
    }

    private void confirmExit() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Quiz")
                .setMessage("Are you sure you want to exit? Your current progress will be lost.")
                .setPositiveButton("Exit", (dialog, which) -> {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    finish();
                })
                .setNegativeButton("Resume", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        confirmExit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private int dpToPx(float dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
