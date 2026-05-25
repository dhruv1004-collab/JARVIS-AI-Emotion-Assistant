package com.Jarvis.Jarvis.service;

import com.Jarvis.Jarvis.model.MoodHistory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.create("https://generativelanguage.googleapis.com");

    public String chat(String userMessage, String emotion, List<MoodHistory> recentMoods) {
        try {
            StringBuilder moodHistory = new StringBuilder();
            for (MoodHistory mood : recentMoods) {
                moodHistory.append(mood.getEmotion())
                          .append(" at ")
                          .append(mood.getDetectedAt())
                          .append(", ");
            }

            String prompt = "You are JARVIS, a personal AI emotion assistant like Iron Man's JARVIS. " +
                "You are warm, empathetic and intelligent. " +
                "The user's current detected emotion from webcam is: " + emotion + ". " +
                "Recent mood history: " + moodHistory.toString() + ". " +
                "RULES: " +
                "1. If user talks about feelings or emotions — be empathetic and supportive. " +
                "2. If user asks about their mood — use the detected emotion data. " +
                "3. For greetings — greet warmly and personally. " +
                "4. For general questions — answer helpfully. " +
                "5. Always respond in 2-3 sentences maximum. " +
                "6. Be conversational and natural — not robotic. " +
                "User says: " + userMessage;

            Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(
                        Map.of("text", prompt)
                    ))
                )
            );

            Map<String, Object> response = webClient.post()
                .uri("/v1beta/models/gemini-2.5-flash-lite:generateContent?key=" + apiKey)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

            List candidates = (List) response.get("candidates");
            Map candidate = (Map) candidates.get(0);
            Map content = (Map) candidate.get("content");
            List parts = (List) content.get("parts");
            Map part = (Map) parts.get(0);

            return (String) part.get("text");

        } catch (Exception e) {
            System.out.println("Gemini error: " + e.getMessage());
            return "I am having trouble connecting. Please try again!";
        }
    }
}