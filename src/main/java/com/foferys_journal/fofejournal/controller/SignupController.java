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


    // @Valid -> attiva la Bean Validation su UserDto prima di entrare nel metodo.
    //           Se qualche campo viola i vincoli (es. @NotNull, @Size, ecc. dentro UserDto),
    //           gli errori vengono inseriti automaticamente nel BindingResult collegato
    //           al parametro validato.
    //
    // @ModelAttribute -> indica a Spring di legare (data binding) i parametri della richiesta
    //                    (form HTML, query param, ecc.) alle proprietà dell’oggetto UserDto
    //                    e di metterlo anche nel Model con il nome predefinito "userDto"
    //                    (o un nome personalizzato se specificato: @ModelAttribute("userDto")).
    //
    // BindingResult -> oggetto che contiene tutti gli errori di binding e di validazione
    //                  relativi all’oggetto immediatamente precedente nella firma del metodo
    //                  (qui: UserDto userDto). DEVE venire subito dopo il parametro annotato con @Valid,
    //                  altrimenti Spring non associa correttamente gli errori e, in caso di errore,
    //                  potrebbe sollevare un’eccezione invece di permetterti di gestirli nel controller.
    //
    // Differenza tra usare solo @Valid e usarlo insieme a @ModelAttribute + BindingResult:
    // - Solo @Valid su UserDto: viene comunque eseguita la validazione, ma senza BindingResult
    //   non puoi intercettare e gestire gli errori manualmente (Spring potrebbe rimandare a una pagina
    //   di errore generica).
    // - @Valid + @ModelAttribute + BindingResult: hai sia il binding dei campi del form su UserDto,
    //   sia la validazione automatica, sia la possibilità di controllare result.hasErrors()
    //   per mostrare messaggi di errore personalizzati e ritornare alla view corretta.
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

        }catch(RuntimeException re) {
            System.out.println("runtime exeption: " + re.getMessage());
            model.addAttribute("usernamePresent", "L'Username "+ userDto.getUsername()+" già presente");
            return "/signup";
        }catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return "/signup";
        }

    }
    
}
