package com.example.cogniquest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.recyclerview.widget.LinearLayoutManager;

public class HomeFragment extends Fragment {

    private TextView tvWelcomeName;
    private TextView quoteText;
    private TextView quoteAuthor;
    private ImageView btnNextQuote;
    private DatabaseHelper dbHelper;
    private ExecutorService executorService;
    private Handler mainHandler;
    
    private List<Quote> currentQuotes = new ArrayList<>();
    private int currentQuoteIndex = 0;
    private Runnable quoteRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvWelcomeName = view.findViewById(R.id.tvWelcomeName);
        quoteText = view.findViewById(R.id.quoteText);
        quoteAuthor = view.findViewById(R.id.quoteAuthor);
        btnNextQuote = view.findViewById(R.id.btnNextQuote);

        UserManager userManager = new UserManager(requireContext());
        tvWelcomeName.setText("Hello, " + userManager.getUsername() + "!");

        dbHelper = new DatabaseHelper(requireContext());
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        TextView tvStatTotalQuestions = view.findViewById(R.id.tvStatTotalQuestions);
        TextView tvStatSessions = view.findViewById(R.id.tvStatSessions);
        TextView tvStatHours = view.findViewById(R.id.tvStatHours);
        TextView tvStatAccuracy = view.findViewById(R.id.tvStatAccuracy);
        TextView tvHomeStreak = view.findViewById(R.id.tvHomeStreak);

        loadDynamicStats(tvStatTotalQuestions, tvStatSessions, tvStatHours, tvStatAccuracy, tvHomeStreak, userManager.getUsername());

        btnNextQuote.setOnClickListener(v -> {
            if (!currentQuotes.isEmpty()) {
                currentQuoteIndex = (currentQuoteIndex + 1) % currentQuotes.size();
                displayCurrentQuote();
                resetQuoteTimer();
            }
        });

        fetchQuotesFromApi();

        return view;
    }

    private void fetchQuotesFromApi() {
        btnNextQuote.setVisibility(View.GONE);
        quoteText.setText("Loading motivation...");
        quoteAuthor.setText("");

        ZenQuotesApi api = RetrofitClient.getClient().create(ZenQuotesApi.class);
        Call<List<Quote>> call = api.getQuotes();
        call.enqueue(new Callback<List<Quote>>() {
            @Override
            public void onResponse(Call<List<Quote>> call, Response<List<Quote>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<Quote> quotes = response.body();
                    updateUI(quotes);
                    
                    // Save to local database using Background Thread (ExecutorService)
                    executorService.execute(() -> {
                        dbHelper.insertQuotes(quotes);
                    });
                } else {
                    handleErrorState();
                }
            }

            @Override
            public void onFailure(Call<List<Quote>> call, Throwable t) {
                handleErrorState();
            }
        });
    }

    private void handleErrorState() {
        // Load from local database using Background Thread
        executorService.execute(() -> {
            List<Quote> localQuotes = dbHelper.getAllQuotes();
            mainHandler.post(() -> {
                if (!localQuotes.isEmpty()) {
                    updateUI(localQuotes);
                    Toast.makeText(requireContext(), "Showing offline quotes", Toast.LENGTH_SHORT).show();
                } else {
                    quoteText.setText("Failed to load motivation. Please check your network.");
                    quoteAuthor.setText("");
                    Toast.makeText(requireContext(), "Network error and no offline data", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateUI(List<Quote> quotes) {
        if (!quotes.isEmpty()) {
            this.currentQuotes = quotes;
            this.currentQuoteIndex = 0;
            btnNextQuote.setVisibility(View.VISIBLE);
            displayCurrentQuote();
            startQuoteTimer();
        }
    }

    private void displayCurrentQuote() {
        if (!currentQuotes.isEmpty() && currentQuoteIndex < currentQuotes.size()) {
            Quote q = currentQuotes.get(currentQuoteIndex);
            quoteText.setText("\"" + q.getText() + "\"");
            quoteAuthor.setText("— " + q.getAuthor());
        }
    }

    private void startQuoteTimer() {
        if (quoteRunnable == null) {
            quoteRunnable = new Runnable() {
                @Override
                public void run() {
                    if (!currentQuotes.isEmpty()) {
                        currentQuoteIndex = (currentQuoteIndex + 1) % currentQuotes.size();
                        displayCurrentQuote();
                    }
                    mainHandler.postDelayed(this, 60000); // 1 minute
                }
            };
        }
        mainHandler.removeCallbacks(quoteRunnable);
        mainHandler.postDelayed(quoteRunnable, 60000);
    }

    private void resetQuoteTimer() {
        if (quoteRunnable != null) {
            mainHandler.removeCallbacks(quoteRunnable);
            mainHandler.postDelayed(quoteRunnable, 60000);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mainHandler != null && quoteRunnable != null) {
            mainHandler.removeCallbacks(quoteRunnable);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    private void loadDynamicStats(TextView tvTotal, TextView tvSessions, TextView tvHours, TextView tvAccuracy, TextView tvStreak, String username) {
        executorService.execute(() -> {
            List<QuizHistory> history = dbHelper.getQuizHistory(username);
            
            int totalQuestions = 0;
            int totalCorrect = 0;
            int sessions = history.size();
            for (QuizHistory h : history) {
                totalQuestions += h.getTotalQuestions();
                totalCorrect += h.getScore();
            }
            
            int accuracy = 0;
            if (totalQuestions > 0) {
                accuracy = Math.round(((float) totalCorrect / totalQuestions) * 100f);
            }
            
            double hoursVal = (sessions * 5.0) / 60.0;
            String hoursStr = String.format(java.util.Locale.US, "%.1fh", hoursVal);
            
            int streakDays = 0;
            java.util.Set<String> uniqueDates = new java.util.HashSet<>();
            for (QuizHistory h : history) {
                String ts = h.getTimestamp();
                if (ts != null && ts.contains(",")) {
                    String datePart = ts.split(",")[0].trim();
                    uniqueDates.add(datePart);
                }
            }
            streakDays = uniqueDates.size();
            
            final int finalQuestions = totalQuestions;
            final int finalSessions = sessions;
            final String finalHours = hoursStr;
            final int finalAccuracy = accuracy;
            final int finalStreak = streakDays;
            
            mainHandler.post(() -> {
                if (tvTotal != null) tvTotal.setText(String.valueOf(finalQuestions));
                if (tvSessions != null) tvSessions.setText(String.valueOf(finalSessions));
                if (tvHours != null) tvHours.setText(finalHours);
                if (tvAccuracy != null) tvAccuracy.setText(finalAccuracy + "%");
                if (tvStreak != null) tvStreak.setText(finalStreak + " Day Streak 🔥");
            });
        });
    }
}
