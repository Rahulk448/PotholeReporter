package com.example.reportissueactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ArrayList<Issue> issues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button reportIssueBtn = findViewById(R.id.reportIssueBtn);
        Button viewIssuesBtn = findViewById(R.id.viewIssuesBtn);
        Button viewStatusBtn = findViewById(R.id.viewStatusBtn);
        Button logoutBtn = findViewById(R.id.logoutBtn);

        // Dummy issues for demonstration
        issues.add(new Issue("Pothole", "Large pothole on main street", null, null));
        issues.add(new Issue("Water on Road", "Water logging near the park", null, null));
        issues.get(1).setStatus("Resolved");

        reportIssueBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ReportIssueActivity.class);
            startActivity(intent);
        });

        viewIssuesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ViewissueActivity.class);
            startActivity(intent);
        });

        viewStatusBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ViewStatusActivity.class);
            intent.putParcelableArrayListExtra("issues", issues);
            startActivity(intent);
        });

        logoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}