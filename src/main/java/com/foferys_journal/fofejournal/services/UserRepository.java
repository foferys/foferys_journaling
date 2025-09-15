package com.foferys_journal.fofejournal.services;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.foferys_journal.fofejournal.models.User;



@Component
public interface UserRepository extends JpaRepository<User, Integer>{

    User findByUsernameAndPassword(String username, String password);
    
    Optional<User> findByGithubId(String githubId);

    User findByUsername(String username);

}
