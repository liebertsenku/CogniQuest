package com.example.cogniquest.ui.ai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cogniquest.databinding.FragmentAiAssistantBinding;
import com.example.cogniquest.BuildConfig;
import com.example.cogniquest.R;

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

public class AiAssistantFragment extends Fragment {

    private FragmentAiAssistantBinding binding;
    private OkHttpClient client;
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAiAssistantBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
            sysPart.put("text", "Anda adalah CogniQuest, asisten belajar. Jawablah selalu dalam bahasa Indonesia dengan jelas, ramah, dan terstruktur. Gunakan baris baru dan bullet points secara teratur agar mudah dibaca.");
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
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> addAiMessageToUI("Error communicating with AI: " + e.getMessage()));
                    }
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
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(() -> addAiMessageToUI(text));
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> addAiMessageToUI("Error parsing response: " + e.getMessage()));
                            }
                        }
                    } else {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> addAiMessageToUI("Error: " + response.code() + " - " + response.message()));
                        }
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addUserMessageToUI(String message) {
        if (getContext() == null) return;
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(100, 16, 0, 16);
        params.gravity = android.view.Gravity.END;
        layout.setLayoutParams(params);

        TextView label = new TextView(requireContext());
        label.setText("You");
        label.setTextSize(12);
        label.setTextColor(getResources().getColor(R.color.outline_variant));
        label.setGravity(android.view.Gravity.END);
        
        TextView text = new TextView(requireContext());
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
        if (getContext() == null) return;
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 16, 100, 16);
        layout.setLayoutParams(params);

        TextView label = new TextView(requireContext());
        label.setText("CogniQuest");
        label.setTextSize(12);
        label.setTextColor(getResources().getColor(R.color.outline_variant));

        TextView text = new TextView(requireContext());
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
        
        html = html.replaceAll("(?m)^###\\s+(.*?)$", "<br><b>$1</b><br>");
        html = html.replaceAll("(?m)^##\\s+(.*?)$", "<br><b>$1</b><br>");
        html = html.replaceAll("(?m)^#\\s+(.*?)$", "<br><b>$1</b><br>");
        
        // Menghilangkan '---' karena bisa melampaui 1 baris
        html = html.replaceAll("(?m)^\\s*---+\\s*$", "");
        
        html = html.replaceAll("(?m)^\\*\\s+(.*?)$", "• $1");
        html = html.replaceAll("(?m)^-\\s+(.*?)$", "• $1");
        
        // Menangani format code block ``` dan inline code `
        html = html.replaceAll("(?s)```[a-zA-Z]*\\n?(.*?)```", "<br><tt>$1</tt><br>");
        html = html.replaceAll("(?s)`(.*?)`", "<i>$1</i>"); // Menggunakan italics/tt untuk inline code agar lebih rapi
        
        html = html.replaceAll("(?s)\\*\\*(.*?)\\*\\*", "<b>$1</b>");
        html = html.replaceAll("(?s)\\*(.*?)\\*", "<i>$1</i>");
        
        html = html.replace("\n", "<br>");
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return android.text.Html.fromHtml(html, android.text.Html.FROM_HTML_MODE_LEGACY);
        } else {
            return android.text.Html.fromHtml(html);
        }
    }

    private void scrollToBottom() {
        binding.chatScrollView.post(() -> {
            if (binding != null && binding.chatScrollView != null) {
                binding.chatScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
