package com.example.cogniquest.ui.profile;
import com.example.cogniquest.R;
import com.example.cogniquest.utils.UserManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cogniquest.databinding.ActivityEditProfileBinding;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private UserManager userManager;
    private String tempAvatarUri = null;

    private final androidx.activity.result.ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            try {
                                String username = userManager.getUsername().toLowerCase();
                                java.io.File destFile = new java.io.File(getFilesDir(), "avatar_" + username + ".jpg");
                                
                                java.io.InputStream in = getContentResolver().openInputStream(uri);
                                java.io.OutputStream out = new java.io.FileOutputStream(destFile);
                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = in.read(buf)) > 0) {
                                    out.write(buf, 0, len);
                                }
                                in.close();
                                out.close();
                                
                                tempAvatarUri = destFile.getAbsolutePath();
                                binding.avatarImage.setImageURI(android.net.Uri.fromFile(destFile));
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

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

        // Pre-fill avatar
        tempAvatarUri = userManager.getAvatarUri();
        if (tempAvatarUri != null && !tempAvatarUri.isEmpty()) {
            binding.avatarImage.setImageURI(android.net.Uri.parse(tempAvatarUri));
        } else {
            binding.avatarImage.setImageResource(R.drawable.ic_profile);
        }

        View.OnClickListener changeAvatarListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageLauncher.launch("image/*");
            }
        };

        binding.avatarImage.setOnClickListener(changeAvatarListener);
        binding.btnEditAvatar.setOnClickListener(changeAvatarListener);
        binding.tvChangeAvatar.setOnClickListener(changeAvatarListener);

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
                if (tempAvatarUri != null) {
                    userManager.updateAvatarUri(tempAvatarUri);
                }

                Toast.makeText(EditProfileActivity.this, "Profile Saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
