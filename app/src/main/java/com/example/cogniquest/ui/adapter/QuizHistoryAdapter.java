package com.example.cogniquest.ui.adapter;
import com.example.cogniquest.R;
import com.example.cogniquest.model.QuizHistory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuizHistoryAdapter extends RecyclerView.Adapter<QuizHistoryAdapter.HistoryViewHolder> {

    private List<QuizHistory> historyList;

    public QuizHistoryAdapter(List<QuizHistory> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        QuizHistory history = historyList.get(position);
        
        holder.tvTitle.setText(history.getQuizTitle());
        holder.tvDate.setText(history.getTimestamp());
        holder.tvScore.setText(String.valueOf(history.getScore()));
        holder.tvTotal.setText("/" + history.getTotalQuestions());

        // Grade feedback text based on accuracy percentage
        float pct = 0f;
        if (history.getTotalQuestions() > 0) {
            pct = ((float) history.getScore() / history.getTotalQuestions()) * 100f;
        }

        if (pct >= 85) {
            holder.tvFeedback.setText("Excellent Work! 🌟");
            holder.tvFeedback.setTextColor(android.graphics.Color.parseColor("#006a2c")); // Green
        } else if (pct >= 60) {
            holder.tvFeedback.setText("Well Done! 👍");
            holder.tvFeedback.setTextColor(android.graphics.Color.parseColor("#004e60")); // Medium Blue
        } else {
            holder.tvFeedback.setText("Keep Practicing! 💪");
            holder.tvFeedback.setTextColor(android.graphics.Color.parseColor("#a2002c")); // Dark Red
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateData(List<QuizHistory> newHistoryList) {
        this.historyList = newHistoryList;
        notifyDataSetChanged();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvScore, tvTotal, tvFeedback;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvHistoryTitle);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            tvScore = itemView.findViewById(R.id.tvHistoryScore);
            tvTotal = itemView.findViewById(R.id.tvHistoryTotal);
            tvFeedback = itemView.findViewById(R.id.tvFeedback);
        }
    }
}
