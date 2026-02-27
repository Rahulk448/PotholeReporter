package com.example.reportissueactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class IssueAdapter extends ArrayAdapter<Issue> {

    public IssueAdapter(@NonNull Context context, @NonNull List<Issue> issues) {
        super(context, 0, issues);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_issue, parent, false);
        }

        Issue issue = getItem(position);

        ImageView issueImageView = convertView.findViewById(R.id.issueImageView);
        TextView issueTypeTextView = convertView.findViewById(R.id.issueTypeTextView);
        TextView issueDescriptionTextView = convertView.findViewById(R.id.issueDescriptionTextView);
        TextView locationTextView = convertView.findViewById(R.id.locationTextView);

        if (issue != null) {
            issueImageView.setImageBitmap(issue.getImage());
            issueTypeTextView.setText(issue.getIssueType());
            issueDescriptionTextView.setText(issue.getDescription());
            if (issue.getLocation() != null) {
                locationTextView.setText("Lat: " + issue.getLocation().latitude + ", Lon: " + issue.getLocation().longitude);
            } else {
                locationTextView.setText("Location not available");
            }
        }

        return convertView;
    }
}
