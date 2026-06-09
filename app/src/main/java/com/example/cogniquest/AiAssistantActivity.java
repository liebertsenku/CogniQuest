package com.example.cogniquest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cogniquest.databinding.ActivityAiAssistantBinding;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AiAssistantActivity extends AppCompatActivity {

    private ActivityAiAssistantBinding binding;
    private OkHttpClient client;
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAiAssistantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        client = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = binding.etMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessageToGemini(message);
                }
            }
        });

        binding.btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chatContainer.removeAllViews();
                binding.etMessage.setText("");
            }
        });



        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(AiAssistantActivity.this, HomeDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("SELECT_TAB", R.id.nav_home);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_ai) {
                    return true;
                } else if (itemId == R.id.nav_progress) {
                    Intent intent = new Intent(AiAssistantActivity.this, LearningProgressActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(AiAssistantActivity.this, HomeDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("SELECT_TAB", R.id.nav_profile);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });

        binding.bottomNavigation.setSelectedItemId(R.id.nav_ai);
    }

    private void sendMessageToGemini(String message) {
        addUserMessageToUI(message);
        binding.etMessage.setText("");

        if (BuildConfig.GEMINI_API_KEY.isEmpty()) {
            addAiMessageToUI("Error: GEMINI_API_KEY is not set in local.properties");
            return;
        }

        try {
            JSONObject jsonBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();
            
            part.put("text", message);
            parts.put(part);
            content.put("parts", parts);
            contents.put(content);
            jsonBody.put("contents", contents);

            JSONObject systemInstruction = new JSONObject();
            JSONArray sysParts = new JSONArray();
            JSONObject sysPart = new JSONObject();
            sysPart.put("text", "Anda adalah EduMaster AI, asisten belajar. Jawablah selalu dalam bahasa Indonesia dengan jelas, ramah, dan terstruktur. Gunakan baris baru dan bullet points secara teratur agar mudah dibaca.");
            sysParts.put(sysPart);
            systemInstruction.put("parts", sysParts);
            jsonBody.put("systemInstruction", systemInstruction);

            RequestBody requestBody = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(GEMINI_API_URL + BuildConfig.GEMINI_API_KEY)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> addAiMessageToUI("Error communicating with AI: " + e.getMessage()));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            JSONArray candidates = jsonResponse.getJSONArray("candidates");
                            if (candidates.length() > 0) {
                                JSONObject candidate = candidates.getJSONObject(0);
                                JSONObject contentObj = candidate.getJSONObject("content");
                                JSONArray partsArray = contentObj.getJSONArray("parts");
                                if (partsArray.length() > 0) {
                                    String text = partsArray.getJSONObject(0).getString("text");
                                    runOnUiThread(() -> addAiMessageToUI(text));
                                }
                            }
                        } catch (JSONException e) {
                            runOnUiThread(() -> addAiMessageToUI("Error parsing response: " + e.getMessage()));
                        }
                    } else {
                        runOnUiThread(() -> addAiMessageToUI("Error: " + response.code() + " - " + response.message()));
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addUserMessageToUI(String message) {
        // Simple programmatically created TextView for User Message
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(100, 16, 0, 16);
        params.gravity = android.view.Gravity.END;
        layout.setLayoutParams(params);

        TextView label = new TextView(this);
        label.setText("You");
        label.setTextSize(12);
        label.setTextColor(getResources().getColor(R.color.outline_variant));
        label.setGravity(android.view.Gravity.END);
        
        TextView text = new TextView(this);
        text.setText(formatMarkdown(message));
        text.setTextColor(getResources().getColor(R.color.on_primary));
        text.setBackgroundResource(R.drawable.chat_bubble_background);
        text.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.primary)));
        text.setPadding(32, 32, 32, 32);

        layout.addView(label);
        layout.addView(text);

        binding.chatContainer.addView(layout);
        scrollToBottom();
    }

    private void addAiMessageToUI(String message) {
        // Simple programmatically created TextView for AI Message
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 16, 100, 16);
        layout.setLayoutParams(params);

        TextView label = new TextView(this);
        label.setText("EduMaster AI");
        label.setTextSize(12);
        label.setTextColor(getResources().getColor(R.color.outline_variant));

        TextView text = new TextView(this);
        text.setText(formatMarkdown(message));
        text.setTextColor(getResources().getColor(R.color.on_surface));
        text.setBackgroundResource(R.drawable.chat_bubble_background);
        text.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.surface_container)));
        text.setPadding(32, 32, 32, 32);

        layout.addView(label);
        layout.addView(text);

        binding.chatContainer.addView(layout);
        scrollToBottom();
    }

    private android.text.Spanned formatMarkdown(String text) {
        if (text == null) return new android.text.SpannedString("");
        
        String html = text;
        
        // 1. Headings (### Heading, ## Heading, # Heading)
        html = html.replaceAll("(?m)^###\\s+(.*?)$", "<br><b>$1</b><br>");
        html = html.replaceAll("(?m)^##\\s+(.*?)$", "<br><b>$1</b><br>");
        html = html.replaceAll("(?m)^#\\s+(.*?)$", "<br><b>$1</b><br>");
        
        // 1.5. Horizontal Divider (---)
        html = html.replaceAll("(?m)^---+$", "<br><font color='#888888'>────────────────────────────────</font><br>");
        
        // 2. Lists starting with * or -
        html = html.replaceAll("(?m)^\\*\\s+(.*?)$", "• $1");
        html = html.replaceAll("(?m)^-\\s+(.*?)$", "• $1");
        
        // 3. Bold (**text**) with multiline support
        html = html.replaceAll("(?s)\\*\\*(.*?)\\*\\*", "<b>$1</b>");
        
        // 4. Italic (*text*) with multiline support
        html = html.replaceAll("(?s)\\*(.*?)\\*", "<i>$1</i>");
        
        // 5. Replace newlines with <br>
        html = html.replace("\n", "<br>");
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return android.text.Html.fromHtml(html, android.text.Html.FROM_HTML_MODE_LEGACY);
        } else {
            return android.text.Html.fromHtml(html);
        }
    }

    private void scrollToBottom() {
        binding.chatScrollView.post(() -> binding.chatScrollView.fullScroll(View.FOCUS_DOWN));
    }
}
