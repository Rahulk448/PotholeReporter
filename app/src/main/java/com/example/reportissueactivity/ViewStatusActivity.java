package com.example.reportissueactivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reportissueactivity.network.ApiService;
import com.example.reportissueactivity.network.RetrofitClient;

import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_status);

        totalStatusText = findViewById(R.id.totalStatusText);
        pendingStatusText = findViewById(R.id.pendingStatusText);
        resolvedStatusText = findViewById(R.id.resolvedStatusText);

        statusRecyclerView = findViewById(R.id.statusRecyclerView);
        statusRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        apiService = RetrofitClient.getClient("https://pothole-backend-0je2.onrender.com/").create(ApiService.class);

        statusAdapter = new ViewStatusAdapter(issues);
        statusRecyclerView.setAdapter(statusAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatusData();
    }

    private void loadStatusData() {
        apiService.getIssues().enqueue(new Callback<List<Issue>>() {
            @Override
            public void onResponse(Call<List<Issue>> call, Response<List<Issue>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    issues.clear();
                    issues.addAll(response.body());
                    
                    updateSummary(issues);
                    statusAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ViewStatusActivity.this, "Failed to fetch updated status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Issue>> call, Throwable t) {
                Toast.makeText(ViewStatusActivity.this, "Sync error: check internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSummary(List<Issue> issueList) {
        int total = issueList.size();
        int resolved = 0;
        int pending = 0;

        for (Issue issue : issueList) {
            // Case-insensitive check to be safe
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