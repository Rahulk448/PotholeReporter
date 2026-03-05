package com.example.reportissueactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.reportissueactivity.network.ApiService;
import com.example.reportissueactivity.network.RetrofitClient;

public class ViewissueActivity extends AppCompatActivity {
    ListView issuesList;
    private ArrayList<Issue> issues;
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

        apiService.getIssues().enqueue(new Callback<List<Issue>>() {

            @Override
            public void onResponse(Call<List<Issue>> call, Response<List<Issue>> response) {

                if(response.isSuccessful()){

                    issues = new ArrayList<>(response.body());

                    IssueAdapter adapter = new IssueAdapter(ViewissueActivity.this, issues);
                    issuesList.setAdapter(adapter);

                    issuesList.setOnItemClickListener((parent, view, position, id) -> {

                        Issue selectedIssue = issues.get(position);

                        Intent intent = new Intent(ViewissueActivity.this, IssueDetailActivity.class);
                        intent.putExtra("issue", selectedIssue);
                        startActivity(intent);
                    });

                    // ADD THIS PART
                    if(!issues.isEmpty()){

                        viewOnMapBtn.setOnClickListener(v -> {

                            Issue firstIssue = issues.get(0);

                            Intent intent = new Intent(ViewissueActivity.this, MapsActivity.class);
                            intent.putParcelableArrayListExtra("issues",issues);
                            startActivity(intent);


                        });

                    } else {

                        viewOnMapBtn.setVisibility(View.GONE);

                    }

                }


            }

            @Override
            public void onFailure(Call<List<Issue>> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
