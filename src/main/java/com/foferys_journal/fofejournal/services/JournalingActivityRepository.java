package com.foferys_journal.fofejournal.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.foferys_journal.fofejournal.models.JournalingActivity;


public interface JournalingActivityRepository extends JpaRepository<JournalingActivity, Integer>{

    
    List<JournalingActivity> findByUserIdAndDateBetween(int userId, LocalDate startDate, LocalDate endDate);
    
    Optional<JournalingActivity> findByUserIdAndDate(int userId, LocalDate localDate);

    JournalingActivity findByUserId(int userid);

}
