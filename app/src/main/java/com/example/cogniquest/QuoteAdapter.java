package com.example.cogniquest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder> {

    private List<Quote> quoteList;

    public QuoteAdapter(List<Quote> quoteList) {
        this.quoteList = quoteList;
    }

    @NonNull
    @Override
    public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quote, parent, false);
        return new QuoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteViewHolder holder, int position) {
        Quote quote = quoteList.get(position);
        holder.tvQuoteText.setText("\"" + quote.getText() + "\"");
        holder.tvQuoteAuthor.setText("- " + quote.getAuthor());
    }

    @Override
    public int getItemCount() {
        return quoteList != null ? quoteList.size() : 0;
    }

    public void updateData(List<Quote> newQuoteList) {
        this.quoteList = newQuoteList;
        notifyDataSetChanged();
    }

    static class QuoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuoteText, tvQuoteAuthor;

        public QuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuoteText = itemView.findViewById(R.id.tvQuoteText);
            tvQuoteAuthor = itemView.findViewById(R.id.tvQuoteAuthor);
        }
    }
}
