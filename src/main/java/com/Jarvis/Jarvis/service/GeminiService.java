package com.Jarvis.Jarvis.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.Jarvis.Jarvis.model.MoodHistory;

@Service
public class GeminiService {
    @Value("${gemini.api.key}")
    public String apiKey;

    private final WebClient webClient = WebClient.create("https://generativelanguage.googleapis.com");

    public String chat(String userMessage, String emotion, List<MoodHistory> recentMoods) {
        try {

            // Build mood history string
            StringBuilder moodHistory = new StringBuilder();
            for (MoodHistory mood : recentMoods) {
                moodHistory.append(mood.getEmotion())
                        .append(" at ")
                        .append(mood.getDetectedAt())
                        .append(", ");
            }

            String prompt = "You are JARVIS, a smart AI assistant. " +
                    "The user's current detected emotion is: " + emotion + ". " +
                    "Recent mood history: " + moodHistory.toString() + ". " +
                    "IMPORTANT RULES: " +
                    "1. Only mention the user's emotion or mood history if they specifically ask about it. " +
                    "2. For greetings like 'hello' or 'hi' — just greet back normally. " +
                    "3. For general questions — answer normally like a smart assistant. " +
                    "4. Only use emotion/mood data when user asks 'how am I feeling', 'what is my mood', 'analyze my emotion' etc. "
                    +
                    "Respond in 1-2 sentences. " +
                    "User says: " + userMessage;

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)))));

            Map response = webClient.post()
                    .uri("/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // Extract Text from reponse
            List candidates = (List) response.get("candidates");
            Map candidate = (Map) candidates.get(0);
            Map content = (Map) candidate.get("content");
            List parts = (List) content.get("parts");
            Map part = (Map) parts.get(0);

            return (String) part.get("text");

        } catch (Exception e) {
            System.out.println("Gemini error: " + e.getMessage());
            return "I am having trouble connecting to my AI brain. Please try again!";
        }
    }
}
