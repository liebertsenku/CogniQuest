package com.example.cogniquest.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {
    private static final String PREF_NAME = "CogniQuestPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_FULLNAME = "fullname";
    private static final String KEY_BIO = "bio";
    private static final String KEY_DARK_MODE = "darkMode";

    private SharedPreferences prefs;

    public UserManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean registerUser(String fullname, String username, String email, String password) {
        java.util.Set<String> registeredUsernames = prefs.getStringSet("registered_usernames", new java.util.HashSet<>());
        if (username.equalsIgnoreCase("admin")) {
            return false; // admin is reserved
        }
        if (registeredUsernames.contains(username.toLowerCase())) {
            return false; // not unique
        }
        
        // Add to registered list
        java.util.Set<String> newSet = new java.util.HashSet<>(registeredUsernames);
        newSet.add(username.toLowerCase());
        
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("registered_usernames", newSet);
        editor.putString("email_" + username.toLowerCase(), email);
        editor.putString("password_" + username.toLowerCase(), password);
        editor.putString("fullname_" + username.toLowerCase(), fullname);
        editor.putString("bio_" + username.toLowerCase(), "I am a new learner here!");
        
        // Also set the current registered user as active
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_FULLNAME, fullname);
        editor.putString(KEY_BIO, "I am a new learner here!");
        
        editor.apply();
        return true;
    }

    public boolean isUsernameTaken(String username) {
        if (username.equalsIgnoreCase("admin")) {
            return true;
        }
        java.util.Set<String> registeredUsernames = prefs.getStringSet("registered_usernames", new java.util.HashSet<>());
        return registeredUsernames.contains(username.toLowerCase());
    }

    public boolean validateLogin(String usernameOrEmail, String password) {
        if (usernameOrEmail.equalsIgnoreCase("admin") && password.equals("admin")) {
            return true;
        }
        
        // Try username first
        String savedPassword = prefs.getString("password_" + usernameOrEmail.toLowerCase(), "");
        if (!savedPassword.isEmpty() && password.equals(savedPassword)) {
            // Set active user details
            String savedEmail = prefs.getString("email_" + usernameOrEmail.toLowerCase(), "");
            String savedFullname = prefs.getString("fullname_" + usernameOrEmail.toLowerCase(), "");
            String savedBio = prefs.getString("bio_" + usernameOrEmail.toLowerCase(), "");
            
            prefs.edit()
                .putString(KEY_USERNAME, usernameOrEmail)
                .putString(KEY_EMAIL, savedEmail)
                .putString(KEY_PASSWORD, savedPassword)
                .putString(KEY_FULLNAME, savedFullname)
                .putString(KEY_BIO, savedBio)
                .apply();
            return true;
        }
        
        // Try email
        java.util.Set<String> registeredUsernames = prefs.getStringSet("registered_usernames", new java.util.HashSet<>());
        for (String u : registeredUsernames) {
            String savedEmail = prefs.getString("email_" + u, "");
            if (usernameOrEmail.equalsIgnoreCase(savedEmail)) {
                String pass = prefs.getString("password_" + u, "");
                if (password.equals(pass)) {
                    String savedFullname = prefs.getString("fullname_" + u, "");
                    String savedBio = prefs.getString("bio_" + u, "");
                    
                    prefs.edit()
                        .putString(KEY_USERNAME, u)
                        .putString(KEY_EMAIL, savedEmail)
                        .putString(KEY_PASSWORD, pass)
                        .putString(KEY_FULLNAME, savedFullname)
                        .putString(KEY_BIO, savedBio)
                        .apply();
                     return true;
                }
            }
        }
        
        return false;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logout() {
        setLoggedIn(false);
    }

    public void updateProfile(String name, String email, String bio) {
        String currentUsername = getUsername().toLowerCase();
        SharedPreferences.Editor editor = prefs.edit();
        if (name != null) {
            editor.putString(KEY_FULLNAME, name);
            editor.putString("fullname_" + currentUsername, name);
        }
        if (email != null) {
            editor.putString(KEY_EMAIL, email);
            editor.putString("email_" + currentUsername, email);
        }
        if (bio != null) {
            editor.putString(KEY_BIO, bio);
            editor.putString("bio_" + currentUsername, bio);
        }
        editor.apply();
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "Guest User");
    }

    public String getFullname() {
        String fullname = prefs.getString(KEY_FULLNAME, "");
        if (fullname.isEmpty()) {
            return getUsername();
        }
        return fullname;
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "guest@example.com");
    }

    public String getBio() {
        return prefs.getString(KEY_BIO, "Welcome to CogniQuest!");
    }

    public String getAvatarUri() {
        return prefs.getString("avatarUri_" + getUsername().toLowerCase(), "");
    }

    public void updateAvatarUri(String uri) {
        prefs.edit().putString("avatarUri_" + getUsername().toLowerCase(), uri).apply();
    }
    
    public void setDarkMode(boolean isDark) {
        prefs.edit().putBoolean(KEY_DARK_MODE, isDark).apply();
    }
    
    public boolean isDarkMode() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }
}
