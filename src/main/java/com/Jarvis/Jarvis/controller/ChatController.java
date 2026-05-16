package com.Jarvis.Jarvis.controller;

import java.util.List;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.Jarvis.Jarvis.model.MoodHistory;
import com.Jarvis.Jarvis.service.GeminiService;
import com.Jarvis.Jarvis.service.MoodService;

@Controller
public class ChatController {

    private final GeminiService geminiService;

    private final MoodService moodService;

    public ChatController(GeminiService geminiService , MoodService moodService) {
        this.geminiService = geminiService;
        this.moodService = moodService;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Map<String, String> handleMessage(Map<String, String> message) {
        String userMessage = message.get("text");
        String emotion     = message.get("emotion");

        List<MoodHistory> recentMoods = moodService.getRecentMoods(5);

        String jarvisReply = geminiService.chat(userMessage, emotion , recentMoods);

        return Map.of(
            "user",   userMessage,
            "jarvis", jarvisReply
        );
    }
}