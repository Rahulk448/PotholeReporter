package com.example.reportissueactivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AdminIssueAdapter extends ArrayAdapter<Issue> {

    public interface OnStatusUpdateListener {
        void onStatusUpdate(String issueId, String newStatus);
    }

    private OnStatusUpdateListener listener;

    public AdminIssueAdapter(@NonNull Context context, @NonNull List<Issue> issues, OnStatusUpdateListener listener) {
        super(context, 0, issues);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_admin_issue, parent, false);
        }

        Issue issue = getItem(position);

        ImageView issueImageView = convertView.findViewById(R.id.issueImageView);
        TextView typeText = convertView.findViewById(R.id.issueTypeTextView);
        TextView locationText = convertView.findViewById(R.id.issueLocationTextView);
        TextView statusText = convertView.findViewById(R.id.issueStatusTextView);
        TextView dateText = convertView.findViewById(R.id.issueDateTextView);
        Button updateBtn = convertView.findViewById(R.id.updateStatusBtn);

        if (issue != null) {
            // Display image if available
            if (issue.getImage() != null) {
                issueImageView.setImageBitmap(issue.getImage());
                issueImageView.setVisibility(View.VISIBLE);
            } else {
                issueImageView.setVisibility(View.GONE);
            }

            typeText.setText(issue.getIssueType());
            
            // Display proper location name
            String locName = issue.getLocationName();
            if (locName != null && !locName.isEmpty()) {
                locationText.setText("📍 " + locName);
            } else if (issue.getLocation() != null) {
                locationText.setText("📍 Lat: " + issue.getLocation().latitude + ", Lon: " + issue.getLocation().longitude);
            } else {
                locationText.setText("📍 Location not available");
            }

            statusText.setText("Status: " + issue.getStatus());

            // Display reported date
            if (issue.getCreatedAt() != null) {
                dateText.setText("Reported: " + issue.getCreatedAt());
                dateText.setVisibility(View.VISIBLE);
            } else {
                dateText.setVisibility(View.GONE);
            }

            String nextStatus = "Pending".equalsIgnoreCase(issue.getStatus()) ? "Resolved" : "Pending";
            updateBtn.setText("Mark as " + nextStatus);

            updateBtn.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStatusUpdate(issue.getId(), nextStatus);
                }
            });
        }

        return convertView;
    }
}
