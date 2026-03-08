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

import java.util.Map;

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

            if(nameInput.isEmpty() || emailInput.isEmpty() || passwordInput.isEmpty()){
                Toast.makeText(SignupActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(nameInput, emailInput, passwordInput);

            // CHANGED: signup -> register to match Flask backend
            Call<Map<String, String>> call = apiService.register(user);
            call.enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(SignupActivity.this, "Account Created Successfully! Please login.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this, "Registration Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    Toast.makeText(SignupActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
