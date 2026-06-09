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
import android.widget.Button;
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
    private RecyclerView rvQuotes;
    private Button btnRefreshQuotes;
    private DatabaseHelper dbHelper;
    private QuoteAdapter quoteAdapter;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvWelcomeName = view.findViewById(R.id.tvWelcomeName);
        quoteText = view.findViewById(R.id.quoteText);
        quoteAuthor = view.findViewById(R.id.quoteAuthor);
        rvQuotes = view.findViewById(R.id.rvQuotes);
        btnRefreshQuotes = view.findViewById(R.id.btnRefreshQuotes);

        UserManager userManager = new UserManager(requireContext());
        tvWelcomeName.setText("Hello, " + userManager.getUsername() + "!");

        dbHelper = new DatabaseHelper(requireContext());
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        rvQuotes.setLayoutManager(new LinearLayoutManager(requireContext()));
        quoteAdapter = new QuoteAdapter(new ArrayList<>());
        rvQuotes.setAdapter(quoteAdapter);

        btnRefreshQuotes.setOnClickListener(v -> fetchQuotesFromApi());

        fetchQuotesFromApi();

        return view;
    }

    private void fetchQuotesFromApi() {
        btnRefreshQuotes.setVisibility(View.GONE);
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
        btnRefreshQuotes.setVisibility(View.VISIBLE);
        
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
            Quote firstQuote = quotes.get(0);
            quoteText.setText("\"" + firstQuote.getText() + "\"");
            quoteAuthor.setText("— " + firstQuote.getAuthor());

            // Display the rest in the RecyclerView
            List<Quote> restQuotes = quotes.subList(1, quotes.size());
            quoteAdapter.updateData(restQuotes);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
