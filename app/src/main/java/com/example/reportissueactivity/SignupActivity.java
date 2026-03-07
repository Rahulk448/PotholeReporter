package com.example.reportissueactivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reportissueactivity.model.User;
import com.example.reportissueactivity.network.ApiService;
import com.example.reportissueactivity.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    EditText name, email, password, confirmPassword;
    CheckBox showPassword;
    Button signupBtn;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        apiService = RetrofitClient
                .getClient("https://your-render-backend-url.onrender.com/")
                .create(ApiService.class);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        showPassword = findViewById(R.id.showPassword);
        signupBtn = findViewById(R.id.signupBtn);

        showPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        signupBtn.setOnClickListener(v -> {

            String nameInput = name.getText().toString().trim();
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();
            String confirmInput = confirmPassword.getText().toString().trim();

            // Empty field check
            if(nameInput.isEmpty() || emailInput.isEmpty() || passwordInput.isEmpty() || confirmInput.isEmpty()){
                Toast.makeText(SignupActivity.this,"Please fill all fields",Toast.LENGTH_SHORT).show();
                return;
            }

            // Email validation
            if(!emailInput.contains("@") || !emailInput.contains(".")){
                Toast.makeText(SignupActivity.this,"Enter valid email",Toast.LENGTH_SHORT).show();
                return;
            }

            // Password match check
            if(!passwordInput.equals(confirmInput)){
                Toast.makeText(SignupActivity.this,"Passwords do not match",Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(nameInput, emailInput, passwordInput);

            Call<User> call = apiService.signup(user);

            call.enqueue(new Callback<User>() {

                @Override
                public void onResponse(Call<User> call, Response<User> response) {

                    if(response.isSuccessful()){
                        Toast.makeText(SignupActivity.this,"Account Created",Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(SignupActivity.this,"Signup Failed",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                    Toast.makeText(SignupActivity.this,"Error: "+t.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}