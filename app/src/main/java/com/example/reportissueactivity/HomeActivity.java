package com.example.reportissueactivity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.reportissueactivity.network.ApiService;
import com.example.reportissueactivity.network.RetrofitClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private static final String CHANNEL_ID = "road_guardian_notifications";
    private static final int NOTIFICATION_PERMISSION_CODE = 123;
    private ApiService apiService;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        createNotificationChannel();
        checkNotificationPermission();

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ImageView menuBtn = findViewById(R.id.menuBtn);

        Button reportIssueBtn = findViewById(R.id.reportIssueBtn);
        Button viewIssuesBtn = findViewById(R.id.viewIssuesBtn);
        Button viewStatusBtn = findViewById(R.id.viewStatusBtn);
        Button logoutBtn = findViewById(R.id.logoutBtn);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userEmail = prefs.getString("user_email", null);
        apiService = RetrofitClient.getClient("https://pothole-backend-0je2.onrender.com/").create(ApiService.class);

        // Periodically check for resolved issues if user is logged in
        if (userEmail != null) {
            checkForStatusUpdates();
        }

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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Issue Fix Alerts";
            String description = "Notifications when your reported issues are fixed";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    private void checkForStatusUpdates() {
        apiService.getUserIssues(userEmail).enqueue(new Callback<List<Issue>>() {
            @Override
            public void onResponse(Call<List<Issue>> call, Response<List<Issue>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SharedPreferences statusPrefs = getSharedPreferences("IssueStatusMem", MODE_PRIVATE);
                    SharedPreferences.Editor editor = statusPrefs.edit();

                    for (Issue issue : response.body()) {
                        String issueId = issue.getId();
                        String currentStatus = issue.getStatus();
                        String lastKnownStatus = statusPrefs.getString(issueId, "Pending");

                        if ("Resolved".equalsIgnoreCase(currentStatus) && !"Resolved".equalsIgnoreCase(lastKnownStatus)) {
                            sendResolutionNotification(issue.getIssueType());
                        }
                        
                        // Update memory
                        editor.putString(issueId, currentStatus);
                    }
                    editor.apply();
                }
            }

            @Override
            public void onFailure(Call<List<Issue>> call, Throwable t) {
                // Fail silently in background
            }
        });
    }

    private void sendResolutionNotification(String issueType) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_status)
                .setContentTitle("Issue Resolved!")
                .setContentText("Good news! Your reported " + issueType + " has been fixed.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
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