package com.Jarvis.Jarvis.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    public final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void sendDailyReport(String toEmail , String userName , int totalMoods , String dominantEmotion , int happyCount , int sadCount , int neutralCount){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("🤖 JARVIS Daily Mood Report — " +
                java.time.LocalDate.now());

            String motivationalMsg = switch (dominantEmotion) {
                case "happy"     -> "Keep up the positive energy! You're doing amazing! 🌟";
                case "sad"       -> "Tomorrow is a new day. Stay strong! 💪";
                case "angry"     -> "Take a deep breath. Peace starts from within! 🧘";
                case "surprised" -> "Life is full of surprises! Embrace them! 🎉";
                default          -> "Stay balanced and keep going! 🚀";
            };

            String html = """
                <div style="font-family: monospace; background: #0a0a0a;
                            color: #00ff88; padding: 40px; max-width: 600px;">

                    <h1 style="color: #00f5ff; letter-spacing: 8px;">🤖 JARVIS</h1>
                    <p style="color: #ffffff50; letter-spacing: 3px; font-size: 12px;">
                        DAILY MOOD REPORT
                    </p>

                    <hr style="border-color: rgba(0,245,255,0.2); margin: 20px 0;"/>

                    <p style="color: #00f5ff;">Hello %s! Here is your mood summary for today.</p>

                    <div style="background: rgba(0,245,255,0.05);
                                border: 1px solid rgba(0,245,255,0.2);
                                border-radius: 8px; padding: 20px; margin: 20px 0;">

                        <table style="width: 100%%;">
                            <tr>
                                <td style="color: #ffffff50; padding: 8px 0;">
                                    TOTAL MOODS TRACKED
                                </td>
                                <td style="color: #00f5ff; text-align: right;">%d</td>
                            </tr>
                            <tr>
                                <td style="color: #ffffff50; padding: 8px 0;">
                                    DOMINANT EMOTION
                                </td>
                                <td style="color: #00f5ff; text-align: right;">%s</td>
                            </tr>
                            <tr>
                                <td style="color: #ffffff50; padding: 8px 0;">
                                    😊 HAPPY MOMENTS
                                </td>
                                <td style="color: #00ff88; text-align: right;">%d</td>
                            </tr>
                            <tr>
                                <td style="color: #ffffff50; padding: 8px 0;">
                                    😢 SAD MOMENTS
                                </td>
                                <td style="color: #4488ff; text-align: right;">%d</td>
                            </tr>
                            <tr>
                                <td style="color: #ffffff50; padding: 8px 0;">
                                    😐 NEUTRAL MOMENTS
                                </td>
                                <td style="color: #00f5ff; text-align: right;">%d</td>
                            </tr>
                        </table>
                    </div>

                    <div style="background: rgba(0,255,136,0.05);
                                border: 1px solid rgba(0,255,136,0.2);
                                border-radius: 8px; padding: 16px; margin: 20px 0;">
                        <p style="color: #00ff88; margin: 0;">💬 %s</p>
                    </div>

                    <hr style="border-color: rgba(0,245,255,0.2); margin: 20px 0;"/>
                    <p style="color: #ffffff30; font-size: 11px;">
                        JARVIS — Personal AI Emotion Assistant
                    </p>
                </div>
                """.formatted(userName, totalMoods, dominantEmotion.toUpperCase(),
                              happyCount, sadCount, neutralCount, motivationalMsg);

            helper.setText(html, true);
            mailSender.send(message);
            System.out.println("✅ Daily report sent to: " + toEmail);

        } catch (Exception e) {
           System.out.println("❌ Email error: " + e.getMessage());
        }
    }
}
