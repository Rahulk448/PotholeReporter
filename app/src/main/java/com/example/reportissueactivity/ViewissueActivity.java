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

        if (getIntent().hasExtra("issues")) {
            issues = getIntent().getParcelableArrayListExtra("issues");
        }

        if (issues != null && !issues.isEmpty()) {
            IssueAdapter adapter = new IssueAdapter(this, issues);
            issuesList.setAdapter(adapter);

            issuesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Issue selectedIssue = issues.get(position);
                    Intent intent = new Intent(ViewissueActivity.this, IssueDetailActivity.class);
                    intent.putExtra("issue", selectedIssue);
                    startActivity(intent);
                }
            });

            // Assuming the user wants to see the location of the first issue for the map button
            Issue firstIssue = issues.get(0);
            LatLng location = firstIssue.getLocation();

            viewOnMapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ViewissueActivity.this, MapsActivity.class);
                    intent.putExtra("lat", location.latitude);
                    intent.putExtra("lon", location.longitude);
                    intent.putExtra("place", firstIssue.getIssueType());
                    startActivity(intent);
                }
            });
        } else {
            viewOnMapBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
