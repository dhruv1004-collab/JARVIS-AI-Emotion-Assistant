package com.Jarvis.Jarvis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Jarvis.Jarvis.model.MoodHistory;
import com.Jarvis.Jarvis.repository.MoodHistoryRepository;

@Service
public class MoodService {
    private final MoodHistoryRepository moodHistoryRepository;

    public MoodService(MoodHistoryRepository moodHistoryRepository){
        this.moodHistoryRepository = moodHistoryRepository;
    }

    public void saveMood(String emotion){
        MoodHistory mood = new MoodHistory(emotion);
        moodHistoryRepository.save(mood);
        System.out.println("Mood saved: " + emotion);
    }

    // get AI reponse based on emotion
    public String getAiResponse(String emotion){
        return switch (emotion){
            case "happy"   -> "You Look Happy! Keep smiling, it suits you";
            case "sad"       -> "You seem sad. Remember, tough times never last!";
            case "angry"     -> "You look angry. Take a deep breath, it will be okay!";
            case "surprised" -> "You look surprised! Did something unexpected happen?";
            case "neutral"   -> "You seem calm and focused. Great state of mind!";
            case "fear"      -> "You look worried. Everything will be fine!";
            case "disgust"   -> "Something bothering you? Talk it out!";
            default        -> "Hello! I am JARVIS, Your emotion assistant";
        };
    }

    public List<MoodHistory> getAllMoods(){
        return moodHistoryRepository.findAll();
    } 


    public List<MoodHistory> getRecentMoods(int limit){
        List<MoodHistory> all = moodHistoryRepository.findAll();
        int size = all.size();
        if (size <= limit) {
            return all;
        }

        return all.subList(size - limit, size);
    }
}
