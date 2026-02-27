package com.example.reportissueactivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

public class IssueDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ImageView issueImage = findViewById(R.id.issueImage);
        TextView issueType = findViewById(R.id.issueType);
        TextView issueDescription = findViewById(R.id.issueDescription);
        TextView issueLocation = findViewById(R.id.issueLocation);

        if (getIntent().hasExtra("issue")) {
            Issue issue = getIntent().getParcelableExtra("issue");
            if (issue != null) {
                issueImage.setImageBitmap(issue.getImage());
                issueType.setText(issue.getIssueType());
                issueDescription.setText(issue.getDescription());
                LatLng location = issue.getLocation();
                if (location != null) {
                    issueLocation.setText("Location: " + location.latitude + ", " + location.longitude);
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
