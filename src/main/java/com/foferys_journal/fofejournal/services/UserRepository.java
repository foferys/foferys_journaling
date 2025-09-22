package com.foferys_journal.fofejournal.services;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.foferys_journal.fofejournal.models.User;



@Component
public interface UserRepository extends JpaRepository<User, Integer>{

    User findByUsernameAndPassword(String username, String password);
    
    Optional<User> findByGithubId(String githubId);

    //Optional è un wrapper introdotto in Java 8 per rappresentare un valore che può esserci o non esserci (cioè null).
    //Se il valore è presente → contiene un oggetto.
    //Se il valore è assente → è vuoto.
    //con Optional possiamo usare il metodo isPresent() quando chiamiamo questo metodo per cercare l'utente
    Optional<User> findByUsername(String username);
    

}
