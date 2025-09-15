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
    public String modifica(Model model, @RequestParam int id, @Valid @ModelAttribute UserDto userDto, BindingResult result) {
    
        try {

            User user = userRepository.findById(id).get();
            model.addAttribute("utente", user);
            
            if(result.hasErrors()){
                return "account/edituser";
            }

            if(!userDto.getImg().isEmpty()){

                String uploadDir = "src/main/resources/static/images/";
                Path oldImagePath =  Paths.get(uploadDir, user.getImg());

                try {
                    Files.delete(oldImagePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //salva immagine
                MultipartFile image = userDto.getImg();
                String userImageName = image.getOriginalFilename();

                try(InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream,Paths.get(uploadDir + userImageName), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    System.out.println(e.getCause());
                }

                user.setImg(userImageName);
            }

            //salviamo gli altri elementi nel product e salviamo nel db
            user.setNome(userDto.getNome());
            user.setUsername(userDto.getUsername());
            user.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));

            userRepository.save(user);
            
        } catch (Exception e) {
            System.out.println("Errore->" + e.getMessage());
            return "account/edituser";
        }

        return "redirect:/account";
    }
    
    
}
