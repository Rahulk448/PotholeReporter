package com.example.reportissueactivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reportissueactivity.network.ApiService;
import com.example.reportissueactivity.network.RetrofitClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewStatusActivity extends AppCompatActivity {

    private RecyclerView statusRecyclerView;
    private ViewStatusAdapter statusAdapter;
    private ArrayList<Issue> issues = new ArrayList<>();
    private TextView totalStatusText, pendingStatusText, resolvedStatusText;
    private ApiService apiService;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_status);

        totalStatusText = findViewById(R.id.totalStatusText);
        pendingStatusText = findViewById(R.id.pendingStatusText);
        resolvedStatusText = findViewById(R.id.resolvedStatusText);

        statusRecyclerView = findViewById(R.id.statusRecyclerView);
        statusRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userEmail = prefs.getString("user_email", null);

        // Updated to use the correct endpoint path if needed, but keeping the base URL consistent
        apiService = RetrofitClient.getClient("https://pothole-backend-0je2.onrender.com/").create(ApiService.class);

        statusAdapter = new ViewStatusAdapter(issues);
        statusRecyclerView.setAdapter(statusAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userEmail != null && !userEmail.isEmpty()) {
            loadUserSpecificStatus();
        } else {
            Toast.makeText(this, "Session expired, please login again", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserSpecificStatus() {
        // Log for debugging 404
        Log.d("API_URL", "Fetching: user-issues/" + userEmail);

        apiService.getUserIssues(userEmail).enqueue(new Callback<List<Issue>>() {
            @Override
            public void onResponse(Call<List<Issue>> call, Response<List<Issue>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Issue> fetchedIssues = response.body();
                    
                    // Latest first
                    Collections.reverse(fetchedIssues);
                    
                    issues.clear();
                    issues.addAll(fetchedIssues);
                    
                    updateSummary(issues);
                    statusAdapter.notifyDataSetChanged();
                } else {
                    // Show error code to help diagnose 404
                    Toast.makeText(ViewStatusActivity.this, "Error: " + response.code() + ". Check server endpoint.", Toast.LENGTH_LONG).show();
                    Log.e("API_ERROR", "Code: " + response.code() + " Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Issue>> call, Throwable t) {
                Toast.makeText(ViewStatusActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSummary(List<Issue> issueList) {
        int total = issueList.size();
        int resolved = 0;
        int pending = 0;

        for (Issue issue : issueList) {
            if ("Resolved".equalsIgnoreCase(issue.getStatus())) {
                resolved++;
            } else {
                pending++;
            }
        }

        totalStatusText.setText(String.valueOf(total));
        pendingStatusText.setText(String.valueOf(pending));
        resolvedStatusText.setText(String.valueOf(resolved));
    }
}
