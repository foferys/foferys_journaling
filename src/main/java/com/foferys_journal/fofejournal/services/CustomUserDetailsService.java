package com.foferys_journal.fofejournal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.foferys_journal.fofejournal.models.User;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /*
    Spring Security chiama questo metodo quando un utente tenta di accedere.
    Riceve come parametro l' username inserito dall’utente nel form di login.
    Se l’utente esiste, restituisce un oggetto UserDetails.
    Se non esiste, lancia un'eccezione UsernameNotFoundException. */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).get();
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        // Converte l'oggetto User in un oggetto UserDetails (necessario per Spring Security).
        return new CustomUserDetails(user);
    }

    /*Come interviene Spring Security nell'autenticazione?
    Quando un utente tenta di accedere alla tua applicazione, Spring Security segue questi passi:

    1️⃣ L’utente inserisce il nome utente e la password nel form di login.
    2️⃣ Spring Security chiama il metodo loadUserByUsername(username).
    3️⃣ CustomUserDetailsService cerca l’utente nel database usando UserRepository.
                
    Se l'utente esiste, restituisce un UserDetails.
    Se l'utente non esiste, lancia UsernameNotFoundException.
    4️⃣ Spring Security verifica la password:
    Confronta la password inserita con quella salvata nel database.
    Se la password è corretta, l'utente è autenticato.
    Se la password è errata, viene restituito un errore.
    5️⃣ Dopo l’autenticazione, Spring Security gestisce l’autorizzazione, controllando i ruoli e i permessi dell’utente.
    */
}
