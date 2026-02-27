package com.example.reportissueactivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewStatusActivity extends AppCompatActivity {

    private RecyclerView statusRecyclerView;
    private ViewStatusAdapter statusAdapter;
    private ArrayList<Issue> issues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_status);

        statusRecyclerView = findViewById(R.id.statusRecyclerView);
        statusRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        issues = getIntent().getParcelableArrayListExtra("issues");

        statusAdapter = new ViewStatusAdapter(issues);
        statusRecyclerView.setAdapter(statusAdapter);
    }
}