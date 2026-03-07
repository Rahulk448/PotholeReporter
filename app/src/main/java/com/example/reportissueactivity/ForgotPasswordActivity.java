package com.example.reportissueactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reportissueactivity.network.ApiService;
import com.example.reportissueactivity.network.RetrofitClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText email;
    Button resetPasswordBtn;
    TextView backToLoginText;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        apiService = RetrofitClient
                .getClient("https://pothole-backend-0je2.onrender.com/")
                .create(ApiService.class);

        email = findViewById(R.id.email);
        resetPasswordBtn = findViewById(R.id.resetPasswordBtn);
        backToLoginText = findViewById(R.id.backToLoginText);

        resetPasswordBtn.setOnClickListener(v -> {
            String mail = email.getText().toString().trim();
            if (mail.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> data = new HashMap<>();
            data.put("email", mail);

            apiService.forgotPassword(data).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Reset link sent to " + mail, Toast.LENGTH_LONG).show();
                    } else {
                        String errorMsg = "Server Error (" + response.code() + ")";
                        try {
                            if (response.errorBody() != null) {
                                String rawError = response.errorBody().string();
                                // Check if the response is HTML
                                if (rawError.toLowerCase().contains("<!doctype html>") || rawError.toLowerCase().contains("<html>")) {
                                    errorMsg = "Server endpoint not found or server is down (Error " + response.code() + ")";
                                } else {
                                    errorMsg = rawError;
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.e("ForgotPassword", "Error Response: " + errorMsg);
                        Toast.makeText(ForgotPasswordActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    Log.e("ForgotPassword", "Network Error: " + t.getMessage());
                    Toast.makeText(ForgotPasswordActivity.this, "Network Error: Check your internet connection", Toast.LENGTH_SHORT).show();
                }
            });
        });

        backToLoginText.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
