package com.Jarvis.Jarvis.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mood_history")
public class MoodHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "emotion")
    private String emotion;

    @Column(name = "detected_at")
    private LocalDateTime detectedAt;

    public MoodHistory(String emotion){
        this.emotion = emotion;
        this.detectedAt = LocalDateTime.now();
    }

    public MoodHistory() {}

    public Long getId() {
        return id;
    }

    public String getEmotion(){
        return emotion;
    }

    public LocalDateTime getDetectedAt(){
        return detectedAt;
    }

}
