package com.example.reportissueactivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewStatusAdapter extends RecyclerView.Adapter<ViewStatusAdapter.ViewHolder> {

    private ArrayList<Issue> issues;

    public ViewStatusAdapter(ArrayList<Issue> issues) {
        this.issues = issues;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_issue_status, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Issue issue = issues.get(position);
        holder.issueTypeTextView.setText(issue.getIssueType());
        holder.issueStatusTextView.setText("Status: " + issue.getStatus());
        
        // Display reported date
        if (issue.getCreatedAt() != null) {
            holder.issueDateTextView.setText("Reported: " + issue.getCreatedAt());
            holder.issueDateTextView.setVisibility(View.VISIBLE);
        } else {
            holder.issueDateTextView.setVisibility(View.GONE);
        }

        // Display image
        if (issue.getImage() != null) {
            holder.issueImageView.setImageBitmap(issue.getImage());
            holder.issueImageView.setVisibility(View.VISIBLE);
        } else {
            holder.issueImageView.setVisibility(View.GONE);
        }

        // Color coding for status
        if ("Resolved".equalsIgnoreCase(issue.getStatus())) {
            holder.issueStatusTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.issueStatusTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
        }
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView issueTypeTextView;
        public TextView issueStatusTextView;
        public TextView issueDateTextView;
        public ImageView issueImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            issueTypeTextView = itemView.findViewById(R.id.issueTypeTextView);
            issueStatusTextView = itemView.findViewById(R.id.issueStatusTextView);
            issueDateTextView = itemView.findViewById(R.id.issueDateTextView);
            issueImageView = itemView.findViewById(R.id.issueImageView);
        }
    }
}
