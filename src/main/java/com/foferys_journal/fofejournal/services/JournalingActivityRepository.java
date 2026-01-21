package com.foferys_journal.fofejournal.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foferys_journal.fofejournal.models.JournalingActivity;


public interface JournalingActivityRepository extends JpaRepository<JournalingActivity, Integer>{

    
    List<JournalingActivity> findByUserIdAndDateBetween(int userId, LocalDate startDate, LocalDate endDate);
    
    Optional<JournalingActivity> findByUserIdAndDate(int userId, LocalDate localDate);

    JournalingActivity findByUserId(int userid);

   @Query("""
    SELECT ja
    FROM JournalingActivity ja
    WHERE ja.userId = :id_user
      AND ja.date = :date
    """)
    JournalingActivity findByUserIDAndDate(@Param("id_user") int id_user, @Param("date") LocalDate date);
    
}
