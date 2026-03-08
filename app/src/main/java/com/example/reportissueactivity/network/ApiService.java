package com.example.reportissueactivity.network;

import com.example.reportissueactivity.model.User;
import com.example.reportissueactivity.Issue;

import java.util.Map;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("login")
    Call<Map<String, Object>> login(@Body User user);

    @POST("register")
    Call<Map<String, String>> register(@Body User user);

    @POST("report-issue")
    Call<Map<String, String>> reportIssue(@Body Map<String, Object> issueData);

    @POST("forgot-password")
    Call<Map<String, String>> forgotPassword(@Body Map<String, String> emailData);

    @GET("issues")
    Call<List<Issue>> getIssues(@Query("department") String department);

    @GET("issues")
    Call<List<Issue>> getAllIssues();

    @GET("user-issues/{email}")
    Call<List<Issue>> getUserIssues(@Path(value = "email", encoded = true) String email);

    @PUT("update-status/{id}")
    Call<Map<String, String>> updateStatus(@Path("id") String issueId, @Body Map<String, String> statusData);
}
