package com.example.cogniquest.ui.adapter;
import com.example.cogniquest.R;
import com.example.cogniquest.model.Question;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private List<Question> questionList;
    private OnQuestionClickListener listener;

    public interface OnQuestionClickListener {
        void onEditClick(Question question);
        void onDeleteClick(Question question);
    }

    public QuestionAdapter(List<Question> questionList, OnQuestionClickListener listener) {
        this.questionList = questionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questionList.get(position);
        
        holder.tvQuestionIndex.setText("Question " + (position + 1));
        holder.tvQuestionText.setText(question.getQuestionText());
        holder.tvOptionA.setText("A: " + question.getOptionA());
        holder.tvOptionB.setText("B: " + question.getOptionB());
        holder.tvOptionC.setText("C: " + question.getOptionC());
        holder.tvOptionD.setText("D: " + question.getOptionD());
        
        holder.tvCorrectAnswer.setText("Correct Answer: Option " + question.getCorrectOption());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(question);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(question);
        });
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public void updateData(List<Question> newQuestionList) {
        this.questionList = newQuestionList;
        notifyDataSetChanged();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionIndex, tvQuestionText, tvOptionA, tvOptionB, tvOptionC, tvOptionD, tvCorrectAnswer;
        ImageButton btnEdit, btnDelete;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionIndex = itemView.findViewById(R.id.tvQuestionIndex);
            tvQuestionText = itemView.findViewById(R.id.tvQuestionText);
            tvOptionA = itemView.findViewById(R.id.tvOptionA);
            tvOptionB = itemView.findViewById(R.id.tvOptionB);
            tvOptionC = itemView.findViewById(R.id.tvOptionC);
            tvOptionD = itemView.findViewById(R.id.tvOptionD);
            tvCorrectAnswer = itemView.findViewById(R.id.tvCorrectAnswer);
            btnEdit = itemView.findViewById(R.id.btnEditQuestion);
            btnDelete = itemView.findViewById(R.id.btnDeleteQuestion);
        }
    }
}
