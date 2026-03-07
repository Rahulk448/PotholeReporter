package com.example.reportissueactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reportissueactivity.model.User;
import com.example.reportissueactivity.network.ApiService;
import com.example.reportissueactivity.network.RetrofitClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    EditText name, email, password, confirmPassword;
    Button signupBtn;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Standardized URL
        apiService = RetrofitClient
                .getClient("https://pothole-backend-0je2.onrender.com/")
                .create(ApiService.class);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        signupBtn = findViewById(R.id.signupBtn);

        signupBtn.setOnClickListener(v -> {
            String nameInput = name.getText().toString().trim();
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();
            String confirmInput = confirmPassword.getText().toString().trim();

            if(nameInput.isEmpty() || emailInput.isEmpty() || passwordInput.isEmpty() || confirmInput.isEmpty()){
                Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!emailInput.contains("@") || !emailInput.contains(".")){
                Toast.makeText(SignupActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!passwordInput.equals(confirmInput)){
                Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Using the updated User model with @SerializedName
            User user = new User(nameInput, emailInput, passwordInput);

            Call<User> call = apiService.signup(user);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(SignupActivity.this, "Account Created Successfully! Please login.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = "Signup Failed (" + response.code() + ")";
                        try {
                            if (response.errorBody() != null) {
                                String rawError = response.errorBody().string();
                                if (rawError.toLowerCase().contains("<!doctype html>") || rawError.toLowerCase().contains("<html>")) {
                                    errorMsg = "Endpoint not found or server error (404/500)";
                                } else {
                                    errorMsg = rawError;
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.e("SignupActivity", "Error: " + errorMsg);
                        Toast.makeText(SignupActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e("SignupActivity", "Network Error: " + t.getMessage());
                    Toast.makeText(SignupActivity.this, "Connection Error: Check your internet", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
