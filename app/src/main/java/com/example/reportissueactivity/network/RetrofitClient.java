package com.example.reportissueactivity.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;
    private static String currentBaseUrl = "";

    public static Retrofit getClient(String baseUrl) {
        // If the URL has changed or it's the first time, create a new instance
        if (retrofit == null || !currentBaseUrl.equals(baseUrl)) {
            currentBaseUrl = baseUrl;
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
