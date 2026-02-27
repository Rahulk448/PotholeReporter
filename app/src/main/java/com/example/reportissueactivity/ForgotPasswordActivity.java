package com.example.reportissueactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText email;
    Button resetPasswordBtn;
    TextView backToLoginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.email);
        resetPasswordBtn = findViewById(R.id.resetPasswordBtn);
        backToLoginText = findViewById(R.id.backToLoginText);

        resetPasswordBtn.setOnClickListener(v -> {
            String mail = email.getText().toString();
            Toast.makeText(ForgotPasswordActivity.this, "Reset link sent to " + mail, Toast.LENGTH_SHORT).show();
        });

        backToLoginText.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}