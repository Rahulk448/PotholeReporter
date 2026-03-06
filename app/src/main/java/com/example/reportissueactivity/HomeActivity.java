package com.example.reportissueactivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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
            // Apply the custom style for blue background and white text
            ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.CustomPopupMenuTheme);
            PopupMenu popupMenu = new PopupMenu(wrapper, menuBtn);
            popupMenu.getMenuInflater().inflate(R.menu.home_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.menu_rate_us) {
                    rateApp();
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
                } else if (id == R.id.menu_contact_us) {
                    showContactDialog();
                    return true;
                }
                return false;
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