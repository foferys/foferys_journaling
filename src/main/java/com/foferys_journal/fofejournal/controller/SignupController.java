package com.foferys_journal.fofejournal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.foferys_journal.fofejournal.models.UserDto;
import com.foferys_journal.fofejournal.services.UserService;
import jakarta.validation.Valid;

@Controller
public class SignupController {

    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String signup(Model model) {

        UserDto userDto = new UserDto();
        model.addAttribute("userDto", userDto);
        return "signup.html";
    }


    @PostMapping("/signupProcess")
    public String signupProcess(@Valid @ModelAttribute UserDto userDto, BindingResult result, Model model) {
        
        //uso questo con service al posto di questo appena sopra perché ho impostato li un metodo per il controllo invece di farlo qui
        userService.validateImageFile(userDto, result);

        //controlliamo se è presente qualche errore di validazione:
        if(result.hasErrors()) {

            System.out.println(result.getFieldError());

            model.addAttribute("error", "Errore nella creazione dell'utente: inserisci tutti i dati correttamente");
            return "/signup";
        }

        //se non abbiamo errori salviamo il file immagine nella cartella tramite il meodo creato nel service
        try {
            String storageFileName = userService.saveImage(userDto.getImg());
            
            //salviamo l'elemento nel db
            userService.saveUser(userDto, storageFileName);

            return "redirect:formlogin";

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            // return "/signup";
        }



        return "formlogin";
        
    }
    
    
    
}
