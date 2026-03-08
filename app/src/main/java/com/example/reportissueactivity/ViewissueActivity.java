package com.example.reportissueactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.reportissueactivity.network.ApiService;
import com.example.reportissueactivity.network.RetrofitClient;

public class ViewissueActivity extends AppCompatActivity {
    ListView issuesList;
    private ArrayList<Issue> issues = new ArrayList<>();
    Button viewOnMapBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewissue);

        issuesList = findViewById(R.id.issuesList);
        viewOnMapBtn = findViewById(R.id.viewOnMapBtn);

        ApiService apiService = RetrofitClient
                .getClient("https://pothole-backend-0je2.onrender.com/")
                .create(ApiService.class);

        apiService.getAllIssues().enqueue(new Callback<List<Issue>>() {

            @Override
            public void onResponse(Call<List<Issue>> call, Response<List<Issue>> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<Issue> fetchedIssues = response.body();
                    
                    // Sorting: Latest issues on top
                    Collections.reverse(fetchedIssues);
                    
                    issues.clear();
                    issues.addAll(fetchedIssues);

                    IssueAdapter adapter = new IssueAdapter(ViewissueActivity.this, issues);
                    issuesList.setAdapter(adapter);

                    issuesList.setOnItemClickListener((parent, view, position, id) -> {
                        Issue selectedIssue = issues.get(position);
                        Intent intent = new Intent(ViewissueActivity.this, IssueDetailActivity.class);
                        intent.putExtra("issue", selectedIssue);
                        startActivity(intent);
                    });

                    if(!issues.isEmpty()){
                        viewOnMapBtn.setVisibility(View.VISIBLE);
                        viewOnMapBtn.setOnClickListener(v -> {
                            Intent intent = new Intent(ViewissueActivity.this, MapsActivity.class);
                            intent.putParcelableArrayListExtra("issues", issues);
                            startActivity(intent);
                        });
                    } else {
                        viewOnMapBtn.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(ViewissueActivity.this, "Failed to load issues", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Issue>> call, Throwable t) {
                Toast.makeText(ViewissueActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
