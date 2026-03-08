package com.example.reportissueactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reportissueactivity.model.User;
import com.example.reportissueactivity.network.ApiService;
import com.example.reportissueactivity.network.RetrofitClient;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    EditText email, password;
    Button loginBtn;
    TextView signupText, forgotPasswordText;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        signupText = findViewById(R.id.signupText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);

        apiService = RetrofitClient.getClient("https://pothole-backend-0je2.onrender.com/").create(ApiService.class);

        loginBtn.setOnClickListener(v -> loginUser());

        signupText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        forgotPasswordText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String emailInput = email.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();

        if (emailInput.isEmpty() || passwordInput.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Departmental Admin Check
        if (emailInput.equals("pwd@road.com") && passwordInput.equals("pwd123")) {
            saveUserEmail(emailInput);
            startAdminHome("PWD");
            return;
        } else if (emailInput.equals("police@traffic.com") && passwordInput.equals("police123")) {
            saveUserEmail(emailInput);
            startAdminHome("Police");
            return;
        } else if (emailInput.equals("others@road.com") && passwordInput.equals("others123")) {
            saveUserEmail(emailInput);
            startAdminHome("Others");
            return;
        }

        // Regular User API Login
        User user = new User(emailInput, passwordInput);
        Call<Map<String, Object>> call = apiService.login(user);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    saveUserEmail(emailInput); // SAVE EMAIL TO LOCAL STORAGE
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Login Failed: Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserEmail(String userEmail) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_email", userEmail);
        editor.apply();
    }

    private void startAdminHome(String department) {
        Toast.makeText(this, "Admin Login: " + department, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, AdminHomeActivity.class);
        intent.putExtra("DEPARTMENT", department);
        startActivity(intent);
        finish();
    }
}
