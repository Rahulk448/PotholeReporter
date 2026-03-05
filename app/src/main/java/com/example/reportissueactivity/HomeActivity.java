package com.example.reportissueactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button reportIssueBtn = findViewById(R.id.reportIssueBtn);
        Button viewIssuesBtn = findViewById(R.id.viewIssuesBtn);
        Button viewStatusBtn = findViewById(R.id.viewStatusBtn);
        Button logoutBtn = findViewById(R.id.logoutBtn);
        ImageView menuBtn = findViewById(R.id.menuBtn);

        menuBtn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(HomeActivity.this, menuBtn);
            popupMenu.getMenuInflater().inflate(R.menu.home_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.menu_rate_us) {
                        Toast.makeText(HomeActivity.this, "Rate Us clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (id == R.id.menu_mode_change) {
                        int currentMode = AppCompatDelegate.getDefaultNightMode();
                        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        }
                        return true;
                    } else if (id == R.id.menu_share_app) {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Road Guardian App");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this app to report road issues: https://play.google.com/store/apps/details?id=" + getPackageName());
                        startActivity(Intent.createChooser(shareIntent, "Share via"));
                        return true;
                    }
                    return false;
                }
            });
            popupMenu.show();
        });

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
            // In a real app, you'd fetch issues from a database or API here
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