package com.foferys_journal.fofejournal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foferys_journal.fofejournal.config.CustomOAuth2User;
import com.foferys_journal.fofejournal.models.User;
import com.foferys_journal.fofejournal.services.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@Controller
public class HomeController {


    @Autowired
    private UserRepository userRepository; 

    //per visualizzare il json dell'utente github se viene impostato
    @GetMapping("/whoami")
    @ResponseBody
    public Authentication whoAmI() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
  

    
    @GetMapping("/")
    public String home(Model model, HttpSession session) {
    
        // Ottiene l'oggetto Authentication attuale dal SecurityContext 
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {

            // Controlla se l'oggetto che rappresenta l'utente autenticato (getPrincipal)
            // è un'istanza di CustomOAuth2User, che è la classe personalizzata per gestire gli utenti OAuth2
            if (auth.getPrincipal() instanceof CustomOAuth2User) {
                CustomOAuth2User oauthUser = (CustomOAuth2User) auth.getPrincipal();
                User user = oauthUser.getUser();

                session.setAttribute("imgUser", user.getImg());
                session.setAttribute("userName", user.getNome());

                return "index";
            } 

            if (auth.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                User user = userRepository.findByUsername(userDetails.getUsername()).get();

                session.setAttribute("imgUser", user.getImg());
                session.setAttribute("userName", user.getNome());

                return "index";
            }
        }
        return "/formlogin";

    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,  @RequestParam(value = "logout", required = false) String logout,  Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Credenziali non valide!");
        }
        return "formlogin"; // Il nome del template della pagina di login (Thymeleaf o JSP)
    }

    @GetMapping("/formlogin")
    public String formLogin() {
        return "formlogin";
    }


}

