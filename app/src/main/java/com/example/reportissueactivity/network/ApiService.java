package com.example.reportissueactivity.network;

import com.example.reportissueactivity.model.User;
import com.example.reportissueactivity.Issue;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.Call;

public interface ApiService {
    @POST("/login")
    Call<User> login(@Body User user);

    @POST("/report-issue")
    Call<Map<String, String>> reportIssue(@Body Map<String, Object> issueData);

    @POST("signup")
    Call<User> signup(@Body User user);

    @GET("issues")
    Call<List<Issue>> getIssues();
}
