package com.example.cogniquest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ZenQuotesApi {
    @GET("api/quotes")
    Call<List<Quote>> getQuotes();
}
