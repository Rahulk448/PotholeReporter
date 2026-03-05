package com.example.reportissueactivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reportissueactivity.model.User;
import com.example.reportissueactivity.network.ApiService;
import com.example.reportissueactivity.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    EditText email, password;
    Button loginBtn;
    TextView signupText, forgotPasswordText;
    ImageButton adminLoginBtn;
    CheckBox showPassword;
    private ApiService apiService;

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
        showPassword = findViewById(R.id.showPassword);


        apiService = RetrofitClient.getClient("https://pothole-backend-0je2.onrender.com/").create(ApiService.class);

        // Show / Hide Password
        showPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                // Show password
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                // Hide password
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }

            // Keep cursor at end
            password.setSelection(password.getText().length());
        });


        loginBtn.setOnClickListener(v -> {
            loginUser();
        });

        signupText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
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

    private void loginUser() {
        String emailInput = email.getText().toString();
        String passwordInput = password.getText().toString();

        User user = new User(emailInput, passwordInput);

        Call<User> call = apiService.login(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                    if (response.isSuccessful()) {

                        Toast.makeText(MainActivity.this,
                                "Login Successful",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);

                    } else {

                        Toast.makeText(MainActivity.this,
                                "Login Failed",
                                Toast.LENGTH_SHORT).show();
                    }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}