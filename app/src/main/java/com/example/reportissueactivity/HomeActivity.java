package com.example.reportissueactivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ImageView menuBtn = findViewById(R.id.menuBtn);

        Button reportIssueBtn = findViewById(R.id.reportIssueBtn);
        Button viewIssuesBtn = findViewById(R.id.viewIssuesBtn);
        Button viewStatusBtn = findViewById(R.id.viewStatusBtn);
        Button logoutBtn = findViewById(R.id.logoutBtn);

        menuBtn.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_rate_us) {
                    rateApp();
                } else if (id == R.id.menu_mode_change) {
                    int currentMode = AppCompatDelegate.getDefaultNightMode();
                    if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                } else if (id == R.id.menu_share_app) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Road Guardian App");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this app to report road issues: https://play.google.com/store/apps/details?id=" + getPackageName());
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                } else if (id == R.id.menu_contact_us) {
                    showContactDialog();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
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
            startActivity(intent);
        });

        logoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void showContactDialog() {
        String email = "support@roadguardian.com";
        String phone = "8885477996";

        new MaterialAlertDialogBuilder(this)
                .setTitle("Contact Us")
                .setMessage("Email: " + email + "\nPhone: " + phone)
                .setPositiveButton("Email Us", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + email));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Support Request - Road Guardian");
                    startActivity(Intent.createChooser(intent, "Send Email"));
                })
                .setNeutralButton("Call Us", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}