package com.example.cogniquest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        UserManager userManager = new UserManager(this);
        Intent intent;
        if (userManager.isLoggedIn()) {
            if (userManager.getUsername().equalsIgnoreCase("admin")) {
                intent = new Intent(this, AdminDashboardActivity.class);
            } else {
                intent = new Intent(this, HomeDashboardActivity.class);
            }
        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
