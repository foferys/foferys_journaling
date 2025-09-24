package com.foferys_journal.fofejournal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.foferys_journal.fofejournal.config.CustomOAuth2User;
import com.foferys_journal.fofejournal.exceptions.PasswordMismatchException;
import com.foferys_journal.fofejournal.models.User;
import com.foferys_journal.fofejournal.models.UserDto;
import com.foferys_journal.fofejournal.services.UserRepository;
import com.foferys_journal.fofejournal.services.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;



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

        model.addAttribute("utente",user);
        model.addAttribute("userDto",userDto);

        return "/account/edituser";
    }
    

    @PostMapping("/modificautente")
    public String modifica(Model model, @RequestParam int id, @Valid @ModelAttribute UserDto userDto, BindingResult result) {

        

        try {
            
            if(result.hasErrors()){
                System.out.println("SONO NEL hasErrors");
                result.getAllErrors().forEach(error -> {
                    System.out.println(error.toString()); // stampa l’oggetto errore
                    System.out.println("Default message: " + error.getDefaultMessage()); // messaggio leggibile
                    System.out.println("Object name: " + error.getObjectName()); // nome oggetto/field
                });

                //sarebbe meglio non usare direttamente il repository ma il service
                model.addAttribute("utente", userRepository.findById(id).get());

                return "account/editUser";
            }


            // Salva l'utente con l'immagine (se nuova), password (se nuova) o data di nascita (se nuova)
            userService.updateUser(id, userDto);

            System.out.println("Utente salvato con successo.");
  
           return "redirect:/account";
            
        }catch(PasswordMismatchException pswe) { // -> PasswordMismatchException è la classe creata da me per gestire questo errore specifico (passato qui dal service - metodo updateUser)

            model.addAttribute("pswerror", pswe.getMessage());
            model.addAttribute("utente", userRepository.findById(id).get()); // per riempire il form
            return "account/editUser"; // ritorna alla pagina di modifica con messaggio err

        } catch (Exception e) {
            System.out.println("Errore->" + e.getMessage());
            return "account/editUser";
        }


    }
    
    
    
}
