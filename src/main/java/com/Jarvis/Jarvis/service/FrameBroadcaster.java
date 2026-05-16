package com.Jarvis.Jarvis.service;

import java.util.Map;

import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@DependsOn("webcamService")
public class FrameBroadcaster {
    private final SimpMessagingTemplate messagingTemplate;
    private final WebcamService webcamService;
    private final MoodService moodService;

    private long lastSavedTime = 0;

    public FrameBroadcaster(SimpMessagingTemplate messagingTemplate,
            WebcamService webcamService,
            MoodService moodService) {
        this.messagingTemplate = messagingTemplate;
        this.webcamService = webcamService;
        this.moodService = moodService;
    }

    @Scheduled(fixedRate = 100)
    public void broadcastFrame() {
        try {
            String frame = webcamService.captureAndDetect();
            if (frame.isEmpty()) {
                return;
            }

            int count = webcamService.getDetectedFaceCount();
            String emotion = "neutral";
            String responce = "";

            if (count > 0) {
                emotion = webcamService.detectEmotion(frame);
                responce = moodService.getAiResponse(emotion);

                long currentTime = System.currentTimeMillis();
                if (currentTime - lastSavedTime >= 30000) {
                    moodService.saveMood(emotion);
                    lastSavedTime = currentTime;
                }
            }

            Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("frame", frame);
            payload.put("faceCount", count);
            payload.put("emotion", emotion);
            payload.put("response", responce);

            System.out.println("Broadcasting frame size: " + frame.length());

            messagingTemplate.convertAndSend("/topic/frame", (Object) payload);
        } catch (Exception e) {
            System.out.println("Broadcast error: " + e.getMessage());
        }
    }


}
