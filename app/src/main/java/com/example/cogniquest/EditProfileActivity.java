package com.example.cogniquest;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cogniquest.databinding.ActivityEditProfileBinding;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userManager = new UserManager(this);

        // Pre-fill fields
        binding.etEditName.setText(userManager.getFullname());
        binding.etEditUsername.setText(userManager.getUsername());
        binding.etEditEmail.setText(userManager.getEmail());
        binding.etEditBio.setText(userManager.getBio());

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = binding.etEditName.getText().toString().trim();
                String newEmail = binding.etEditEmail.getText().toString().trim();
                String newBio = binding.etEditBio.getText().toString().trim();

                userManager.updateProfile(newName, newEmail, newBio);

                Toast.makeText(EditProfileActivity.this, "Profile Saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
