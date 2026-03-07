package com.example.reportissueactivity.network;

import com.example.reportissueactivity.model.User;
import com.example.reportissueactivity.Issue;

import java.util.Map;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface ApiService {
    @POST("login")
    Call<Map<String, Object>> login(@Body User user);

    @POST("signup")
    Call<User> signup(@Body User user);

    @POST("report-issue")
    Call<Map<String, String>> reportIssue(@Body Map<String, Object> issueData);

    @POST("forgot-password")
    Call<Map<String, String>> forgotPassword(@Body Map<String, String> emailData);

    @GET("issues")
    Call<List<Issue>> getIssues();

    @PATCH("issues/{id}/status")
    Call<Map<String, String>> updateIssueStatus(@Path("id") String issueId, @Body Map<String, String> statusData);
}
