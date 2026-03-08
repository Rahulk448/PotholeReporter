package com.example.reportissueactivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reportissueactivity.network.ApiService;
import com.example.reportissueactivity.network.RetrofitClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminHomeActivity extends AppCompatActivity {
    TextView deptTitle, totalIssuesText, pendingIssuesText, resolvedIssuesText;
    ListView adminListView;
    AdminIssueAdapter adapter;
    ArrayList<Issue> issueList = new ArrayList<>();
    private ApiService apiService;
    String currentDepartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        deptTitle = findViewById(R.id.deptTitle);
        totalIssuesText = findViewById(R.id.totalIssuesText);
        pendingIssuesText = findViewById(R.id.pendingIssuesText);
        resolvedIssuesText = findViewById(R.id.resolvedIssuesText);
        adminListView = findViewById(R.id.adminListView);

        currentDepartment = getIntent().getStringExtra("DEPARTMENT");
        deptTitle.setText("Department Dashboard: " + currentDepartment);

        apiService = RetrofitClient.getClient("https://pothole-backend-0je2.onrender.com/").create(ApiService.class);

        adapter = new AdminIssueAdapter(this, issueList, this::updateStatus);
        adminListView.setAdapter(adapter);
        
        loadDepartmentIssues();
    }

    private void loadDepartmentIssues() {
        apiService.getAllIssues().enqueue(new Callback<List<Issue>>() {
            @Override
            public void onResponse(Call<List<Issue>> call, Response<List<Issue>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    issueList.clear();
                    List<Issue> fetchedIssues = response.body();
                    
                    // Reverse the list to show latest issues on top
                    Collections.reverse(fetchedIssues);
                    
                    updateStats(fetchedIssues);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Issue>> call, Throwable t) {
                Toast.makeText(AdminHomeActivity.this, "Error fetching issues", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStats(List<Issue> allIssues) {
        int total = 0, pending = 0, resolved = 0;
        for (Issue issue : allIssues) {
            if (isRelevantToDepartment(issue.getIssueType(), currentDepartment)) {
                issueList.add(issue);
                total++;
                if ("Resolved".equalsIgnoreCase(issue.getStatus())) resolved++;
                else pending++;
            }
        }
        totalIssuesText.setText(String.valueOf(total));
        pendingIssuesText.setText(String.valueOf(pending));
        resolvedIssuesText.setText(String.valueOf(resolved));
    }

    private boolean isRelevantToDepartment(String type, String dept) {
        if (type == null) return false;
        String typeLower = type.toLowerCase();
        
        if ("PWD".equals(dept)) {
            return typeLower.contains("pothole") || typeLower.contains("water");
        } else if ("Police".equals(dept)) {
            return typeLower.contains("traffic") || typeLower.contains("accident");
        } else if ("Others".equals(dept)) {
            // "Others" admin sees everything that is NOT PWD or Police
            boolean isPwd = typeLower.contains("pothole") || typeLower.contains("water");
            boolean isPolice = typeLower.contains("traffic") || typeLower.contains("accident");
            return !isPwd && !isPolice;
        }
        return false;
    }

    private void updateStatus(String issueId, String newStatus) {
        for (Issue issue : issueList) {
            if (issue.getId() != null && issue.getId().equals(issueId)) {
                issue.setStatus(newStatus);
                break;
            }
        }
        
        adapter.notifyDataSetChanged();
        
        int total = issueList.size();
        int resolved = 0;
        for(Issue i : issueList) if("Resolved".equalsIgnoreCase(i.getStatus())) resolved++;
        pendingIssuesText.setText(String.valueOf(total - resolved));
        resolvedIssuesText.setText(String.valueOf(resolved));

        if (issueId != null) {
            Map<String, String> data = new HashMap<>();
            data.put("status", newStatus);
            apiService.updateStatus(issueId, data).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminHomeActivity.this, "Status Synced: " + newStatus, Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    Log.e("AdminHome", "Sync failed, but UI updated locally");
                }
            });
        }
    }
}
