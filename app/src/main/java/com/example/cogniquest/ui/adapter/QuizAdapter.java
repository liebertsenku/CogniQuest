package com.example.cogniquest.ui.adapter;
import com.example.cogniquest.R;
import com.example.cogniquest.model.Quiz;
import com.example.cogniquest.model.Difficulty;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private List<Quiz> quizList;
    private OnQuizClickListener listener;
    private boolean showAdminControls = false;

    public interface OnQuizClickListener {
        void onEditClick(Quiz quiz);
        void onDeleteClick(Quiz quiz);
        void onCardClick(Quiz quiz);
    }

    public QuizAdapter(List<Quiz> quizList, OnQuizClickListener listener) {
        this.quizList = quizList;
        this.listener = listener;
        this.showAdminControls = (listener != null);
    }

    public QuizAdapter(List<Quiz> quizList, OnQuizClickListener listener, boolean showAdminControls) {
        this.quizList = quizList;
        this.listener = listener;
        this.showAdminControls = showAdminControls;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);
        holder.tvTitle.setText(quiz.getTitle());
        holder.tvDesc.setText(quiz.getDescription());
        holder.tvQuestionsCount.setText(quiz.getQuestionsCount() + " Questions");
        holder.tvDifficulty.setText(quiz.getDifficulty() != null ? quiz.getDifficulty().getDisplayName() : "");

        int colorHard = android.graphics.Color.parseColor("#a2002c");
        int colorMedium = android.graphics.Color.parseColor("#00838f");
        int colorEasy = android.graphics.Color.parseColor("#512da8");

        if (quiz.getDifficulty() == Difficulty.HARD) {
            holder.tvDifficulty.setTextColor(colorHard);
            holder.tvDifficulty.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fire, 0, 0, 0);
            holder.tvDifficulty.getCompoundDrawables()[0].setTint(colorHard);
        } else if (quiz.getDifficulty() == Difficulty.MEDIUM) {
            holder.tvDifficulty.setTextColor(colorMedium);
            holder.tvDifficulty.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lightning, 0, 0, 0);
            holder.tvDifficulty.getCompoundDrawables()[0].setTint(colorMedium);
        } else {
            holder.tvDifficulty.setTextColor(colorEasy);
            holder.tvDifficulty.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0);
            holder.tvDifficulty.getCompoundDrawables()[0].setTint(colorEasy);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCardClick(quiz);
            }
        });

        if (!showAdminControls) {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEditClick(quiz);
            });
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteClick(quiz);
            });
        }
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public void updateData(List<Quiz> newQuizList) {
        this.quizList = newQuizList;
        notifyDataSetChanged();
    }

    public void filter(String query, List<Quiz> allQuizzes) {
        if (query == null || query.trim().isEmpty()) {
            this.quizList = allQuizzes;
        } else {
            java.util.List<Quiz> filteredList = new java.util.ArrayList<>();
            String lowerCaseQuery = query.toLowerCase().trim();
            for (Quiz quiz : allQuizzes) {
                if (quiz.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                    (quiz.getCategory() != null && quiz.getCategory().toLowerCase().contains(lowerCaseQuery))) {
                    filteredList.add(quiz);
                }
            }
            this.quizList = filteredList;
        }
        notifyDataSetChanged();
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvQuestionsCount, tvDifficulty;
        ImageButton btnEdit, btnDelete;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvQuizTitle);
            tvDesc = itemView.findViewById(R.id.tvQuizDesc);
            tvQuestionsCount = itemView.findViewById(R.id.tvQuestionCount);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
