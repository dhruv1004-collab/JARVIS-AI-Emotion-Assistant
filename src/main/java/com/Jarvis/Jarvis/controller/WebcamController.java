package com.Jarvis.Jarvis.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Jarvis.Jarvis.model.MoodHistory;
import com.Jarvis.Jarvis.service.DailyReportScheduler;
import com.Jarvis.Jarvis.service.MoodService;
import com.Jarvis.Jarvis.service.WebcamService;

import com.Jarvis.Jarvis.service.UserService;

@Controller
public class WebcamController {
    public final WebcamService webcamService;
    public final MoodService moodService;
    public final UserService userService;
    public final DailyReportScheduler dailyReportScheduler;

    private long lastSavedTime = 0;

    public WebcamController(WebcamService webcamService, MoodService moodService, UserService userService,
            DailyReportScheduler dailyReportScheduler) {
        this.webcamService = webcamService;
        this.moodService = moodService;
        this.userService = userService;
        this.dailyReportScheduler = dailyReportScheduler;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/frame")
    @ResponseBody
    public Map<String, Object> getFrame() {
        String frame = webcamService.captureAndDetect();
        int count = webcamService.getDetectedFaceCount();
        String emotion = "neutral";
        String response = "";

        if (count > 0) {
            emotion = webcamService.detectEmotion(frame);
            response = moodService.getAiResponse(emotion);

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastSavedTime >= 3000) {
                moodService.saveMood(emotion);
                lastSavedTime = currentTime;
            }
        }

        return Map.of(
                "frame", frame,
                "faceCount", count,
                "emotion", emotion,
                "response", response);
    }

    @GetMapping("/moods")
    @ResponseBody
    public List<MoodHistory> getMoods() {
        return moodService.getAllMoods();
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/latest-mood")
    @ResponseBody
    public Map<String, String> getLatestMood() {
        List<MoodHistory> moods = moodService.getAllMoods();
        if (moods.isEmpty()) {
            return Map.of("emotion", "neutral");
        }
        String latestEmotion = moods.get(moods.size() - 1).getEmotion();
        return Map.of("emotion", latestEmotion);
    }

    @GetMapping("/user-info")
    @ResponseBody
    public Map<String, String> getUserInfo(org.springframework.security.core.Authentication authentication) {
        String email = authentication.getName();
        String name = userService.findByEmail(email)
                .map(user -> user.getName())
                .orElse("User");
        return Map.of("name", name, "email", email);
    }

    @GetMapping("/test-email")
    @ResponseBody
    public String testEmail(org.springframework.security.core.Authentication authentication) {
        String email = authentication.getName();
        userService.findByEmail(email).ifPresent(user -> {
            dailyReportScheduler.sendReportToUser(user);
        });
        return "Email sent! Check your inbox.";
    }

    @PostMapping("/activate")
    @ResponseBody
    public Map<String, String> activate() {
        webcamService.activateCamera();
        return Map.of("status", "activated");
    }

    @PostMapping("/deactivate")
    @ResponseBody
    public Map<String, String> deactivate() {
        webcamService.deactivateCamera();
        return Map.of("status", "deactivated");
    }

}
