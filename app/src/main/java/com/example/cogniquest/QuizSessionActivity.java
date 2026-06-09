package com.example.cogniquest;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cogniquest.databinding.ActivityQuizSessionBinding;

public class QuizSessionActivity extends AppCompatActivity {

    private ActivityQuizSessionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizSessionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.checkAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(QuizSessionActivity.this, "Correct Answer!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
