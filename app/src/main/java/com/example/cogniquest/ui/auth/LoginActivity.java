package com.example.cogniquest.ui.auth;
import com.example.cogniquest.utils.UserManager;
import com.example.cogniquest.ui.admin.AdminDashboardActivity;
import com.example.cogniquest.ui.dashboard.HomeDashboardActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cogniquest.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userManager = new UserManager(this);

        if (userManager.isLoggedIn()) {
            if (userManager.getUsername().equalsIgnoreCase("Admin")) {
                startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
            } else {
                startActivity(new Intent(LoginActivity.this, HomeDashboardActivity.class));
            }
            finish();
            return;
        }

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.usernameInput.getText().toString().trim();
                String password = binding.passwordInput.getText().toString().trim();
                
                if (!username.isEmpty() && !password.isEmpty()) {
                    if (username.equalsIgnoreCase("admin") && password.equals("admin")) {
                        userManager.registerUser("System Admin", "admin", "admin@cogniquest.com", "admin");
                        userManager.setLoggedIn(true);
                        Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (userManager.validateLogin(username, password)) {
                        userManager.setLoggedIn(true);
                        Intent intent = new Intent(LoginActivity.this, HomeDashboardActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
