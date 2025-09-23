package com.foferys_journal.fofejournal.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.foferys_journal.fofejournal.config.CustomOAuth2User;
import com.foferys_journal.fofejournal.models.User;
import com.foferys_journal.fofejournal.models.UserDto;
import com.foferys_journal.fofejournal.models.builder.UserDtoBuilder;
import com.foferys_journal.fofejournal.services.UserRepository;
import com.foferys_journal.fofejournal.services.UserService;

import java.io.InputStream;
import java.nio.file.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private UserDtoBuilder userDtoBuilder;



    @GetMapping({"", "/"})
    public String gestioneAccount(Model model, @AuthenticationPrincipal UserDetails userDetails) {

        /* !!!! - DA SISTEMARE IN MODO PIU FUNZIONALE - tipo password nell'accesso con github non deve esserci e non deve essere modificabile !!!! */

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            if (auth.getPrincipal() instanceof CustomOAuth2User) {
                CustomOAuth2User oauthUser = (CustomOAuth2User) auth.getPrincipal();
                User user = oauthUser.getUser();

            
                String hiddenPw = "*********";
                model.addAttribute("userPw", hiddenPw);
                model.addAttribute("utente", user);

            } 

            if (auth.getPrincipal() instanceof UserDetails) {
                
                User user = userService.getUserByUsername(userDetails.getUsername());

                String passString = userService.getFakePass(userDetails.getUsername());
             
                model.addAttribute("utente", user);
                model.addAttribute("userPw", passString);
                
            }
        }
        
        return "/account/userdetails";


    }

    @GetMapping("/modificautente")
    public String modificautente(Model model, @RequestParam int id) {

        /* ho preso l'id dell'utente dalla sessione, ma si puo fare anche passandolo dal tasto con 
        *  th:href="@{/account/modificautente(id=${utente.id})} e qui prendendolo con @RequestParam int id"*/
        //User user = userRepository.findById((Integer) session.getAttribute("user")).get();
        User user = userRepository.findById(id).get();


        UserDto userDto = new UserDto();
        userDto.setNome(user.getNome());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());

        model.addAttribute("utente",user);
        model.addAttribute("userDto",userDto);

        return "/account/edituser";
    }
    
    @PostMapping("/modificautente")
    public String modifica(Model model, @RequestParam int id, @Valid @ModelAttribute UserDto userDto, BindingResult result, @AuthenticationPrincipal UserDetails userDetails) {

        userService.validateImageFile(userDto, result);

        //controlliamo se è presente qualche errore di validazione:
        if(result.hasErrors()) {

            System.out.println(result.getFieldError());

            model.addAttribute("error", "Errore nella modifica dell'utente: inserisci tutti i dati correttamente");
            return "account/edituser";
        }

        //se non abbiamo errori salviamo il file immagine nella cartella tramite il meodo creato nel service
        try {
            String storageFileName = userService.saveImage(userDto.getImg());
            
            //salviamo l'elemento nel db
            userService.updateUser(userDto, storageFileName, userDetails.getUsername());

            return "redirect:/account";

        }catch(RuntimeException re) {
            System.out.println("runtime exeption: " + re.getMessage());
            // model.addAttribute("usernamePresent", "L'Username "+ userDto.getUsername()+" già presente");
            return "account/edituser";
        }catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return "account/edituser";
        }


    }
    
    
}
