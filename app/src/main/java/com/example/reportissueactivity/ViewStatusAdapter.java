package com.example.reportissueactivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        holder.issueStatusTextView.setText(issue.getStatus());
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView issueTypeTextView;
        public TextView issueStatusTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            issueTypeTextView = itemView.findViewById(R.id.issueTypeTextView);
            issueStatusTextView = itemView.findViewById(R.id.issueStatusTextView);
        }
    }
}