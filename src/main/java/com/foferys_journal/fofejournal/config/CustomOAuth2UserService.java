package com.foferys_journal.fofejournal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.foferys_journal.fofejournal.models.User;
import com.foferys_journal.fofejournal.services.UserRepository;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>{


    @Autowired
    private UserRepository userRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = new DefaultOAuth2UserService().loadUser(userRequest);

        // Ottieni le informazioni di GitHub
        String githubId = oauthUser.getAttribute("id").toString();
        String username = oauthUser.getAttribute("login");
        String nome = oauthUser.getAttribute("name");
        String bio = oauthUser.getAttribute("bio");
        String image = oauthUser.getAttribute("avatar_url");

        
        // Controlla se l'utente esiste già nel database
        User user = userRepository.findByGithubId(githubId)
                .orElseGet(() -> {

                    // Se l'utente non esiste, creiamolo
                    User u = new User();
                    u.setGithubId(githubId);
                    if (nome.contains(" ")) {
                        u.setNome(nome.substring(0, nome.indexOf(" "))); 
                        // u.setCognome(nome.substring(nome.indexOf(" ") + 1));
                    } else {
                        u.setNome(nome);
                        // u.setCognome(""); // Nessun cognome
                    }
                    // u.setProfessione(bio);
                    u.setUsername(username);
                    u.setImg(image);

                    return userRepository.save(u);
                });

        // Ritorniamo l'utente con i dettagli OAuth
        return new CustomOAuth2User(oauthUser, user);
    }
    
}
