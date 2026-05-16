package com.Jarvis.Jarvis.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.Jarvis.Jarvis.model.MoodHistory;
import com.Jarvis.Jarvis.model.User;
import com.Jarvis.Jarvis.repository.UserRepository;

@Service
public class DailyReportScheduler {
    private final UserRepository userRepository;
    private final MoodService moodService;
    private final EmailService emailService;

    public DailyReportScheduler(UserRepository userRepository , MoodService moodService , EmailService emailService){
        this.userRepository = userRepository;
        this.moodService = moodService;
        this.emailService = emailService;
    }

    // Runs everyday at 9 pm
    @Scheduled(cron = "0 0 21 * * *")
    public void sendDailyReports(){
        System.out.println("📧 Sending daily mood reports...");

        List<User> users = userRepository.findAll();
        for (User user : users) {
            sendReportToUser(user);
        }
    }

    public void sendReportToUser(User user){
        try{
            List<MoodHistory> allMoods = moodService.getAllMoods();
            LocalDate today = LocalDate.now();

            List<MoodHistory> todayMoods = allMoods.stream()
                   .filter(m -> m.getDetectedAt().toLocalDate().equals(today))
                   .collect(Collectors.toList());
            
            if (todayMoods.isEmpty()) {
                System.out.println("No Moods user: " + user.getEmail());
                return;
            }

            Map<String , Long> emotionCounts = todayMoods.stream()
                  .collect(Collectors.groupingBy(
                    MoodHistory::getEmotion,
                    Collectors.counting()
                  ));
            
            String dominant = emotionCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("neutral");

              int total   = todayMoods.size();
            int happy   = emotionCounts.getOrDefault("happy", 0L).intValue();
            int sad     = emotionCounts.getOrDefault("sad", 0L).intValue();
            int neutral = emotionCounts.getOrDefault("neutral", 0L).intValue();

            emailService.sendDailyReport(
                user.getEmail(),
                user.getName(),
                total,
                dominant,
                happy,
                sad,
                neutral
            );

        }catch(Exception e){
            System.out.println("Report error for " + user.getEmail() + ": " + e.getMessage());
        }
    }
}
