package com.Jarvis.Jarvis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Jarvis.Jarvis.model.MoodHistory;

@Repository
public interface MoodHistoryRepository extends JpaRepository<MoodHistory , Long> {
    
}
