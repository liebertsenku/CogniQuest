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

        // Simple color coding for difficulty (optional enhancement based on earlier UI)
        if (quiz.getDifficulty() == Difficulty.HARD) {
            holder.tvDifficulty.setTextColor(android.graphics.Color.parseColor("#a2002c"));
        } else if (quiz.getDifficulty() == Difficulty.MEDIUM) {
            holder.tvDifficulty.setTextColor(android.graphics.Color.parseColor("#004e60"));
        } else {
            holder.tvDifficulty.setTextColor(android.graphics.Color.parseColor("#006a2c"));
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
