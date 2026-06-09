package com.example.cogniquest;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

public class CogniQuestApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UserManager userManager = new UserManager(this);
        if (userManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
