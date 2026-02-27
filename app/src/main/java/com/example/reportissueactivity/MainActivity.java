package com.example.reportissueactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText email,password;
    Button loginBtn;
    TextView signupText, forgotPasswordText;
    ImageButton adminLoginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        signupText = findViewById(R.id.signupText);
        adminLoginBtn = findViewById(R.id.adminLoginBtn);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);

        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            Toast.makeText(MainActivity.this, "Login Clicked", Toast.LENGTH_SHORT).show();
        });

        signupText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
            Toast.makeText(MainActivity.this, "Sign Up Clicked", Toast.LENGTH_SHORT).show();
        });

        adminLoginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });

        forgotPasswordText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }
}