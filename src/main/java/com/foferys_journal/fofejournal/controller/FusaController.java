package com.foferys_journal.fofejournal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;

import com.foferys_journal.fofejournal.config.CustomOAuth2User;
import com.foferys_journal.fofejournal.models.Fusa;
import com.foferys_journal.fofejournal.models.FusaDto;
import com.foferys_journal.fofejournal.models.JournalingActivity;
import com.foferys_journal.fofejournal.models.User;
import com.foferys_journal.fofejournal.services.FusaRepository;
import com.foferys_journal.fofejournal.services.FusaService;
import com.foferys_journal.fofejournal.services.JournalingActivityRepository;
import com.foferys_journal.fofejournal.services.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/fusa")
public class FusaController {
    
    @Autowired
    private FusaRepository fusa_repo;
    @Autowired
    private FusaService fusaService;
    @Autowired
    private UserService userService;

    @Autowired
    private JournalingActivityRepository journalingActivityRepo;

   

    @GetMapping({"", "/"}) //-> indica che questo mapping sarà disponibile all'url /fusa o /fusa/
    public String showProductList(Model model, @AuthenticationPrincipal UserDetails userDetails, @AuthenticationPrincipal Object principalUser) {
        
        
        // if gestisce il tipo diverso di accesso da git/google con oauth oppure normale
        if(principalUser != null && principalUser instanceof CustomOAuth2User) {

            CustomOAuth2User oAuth2User = (CustomOAuth2User)principalUser;
            int userid = oAuth2User.getUser().getId();
    
            // activities di journaling || INDICARE ANNO ||
            LocalDate inizio = LocalDate.of(2026, 01, 01);
            LocalDate fine = LocalDate.of(2026, 12, 30);
            List<JournalingActivity> activities = journalingActivityRepo.findByUserIdAndDateBetween(userid, inizio, fine);
            model.addAttribute("activities", activities);

            
            
            // --stampa del numero di activities ---
            System.out.println("activities:\n");
            for (JournalingActivity journalingActivity : activities) {
                System.out.println(journalingActivity.getEntryCount());
            }
            // --stampa del numero di activities ---
            
            List<Fusa> listafusa = fusa_repo.findByUserId(userid);
            model.addAttribute("listafusa", listafusa);

        }else if(userDetails != null && principalUser instanceof UserDetails) {

            UserDetails userDet = (UserDetails) principalUser;
            int idUser = userService.getUserByUsername(userDet.getUsername()).getId();
            List<Fusa> listafusa = fusa_repo.findByUserId(idUser); //-> uso il metodo creato in fusaRepository per avere i prodotti in base all'id
            model.addAttribute("listafusa", listafusa);

            LocalDate inizio = LocalDate.of(2025, 01, 01);
            LocalDate fine = LocalDate.of(2025, 12, 30);
            List<JournalingActivity> activities = journalingActivityRepo.findByUserIdAndDateBetween(idUser, inizio, fine);
            model.addAttribute("activities", activities);
            System.out.println("activities:\n");
            for (JournalingActivity journalingActivity : activities) {
                System.out.println(journalingActivity.getEntryCount());
            }
        }




        return "fusa/index"; //--> file nella cartella templates>fusa>index.html
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {

        FusaDto fusaDto = new FusaDto();
        model.addAttribute("fusaDto", fusaDto);
        return "fusa/createFusa";
    }

    //creato usando il FusaBuilder e FusaService
    @PostMapping("/create")                                                                             
    public String createProduct(@Valid @ModelAttribute FusaDto fusaDto, BindingResult result, @AuthenticationPrincipal Object principalUser, @AuthenticationPrincipal UserDetails userDetails) {
        /*abbiamo tra i paramentri l'oggetto passato dalla form, e l'annotation @Valid serve a validare
        * e per vedere se ci sono errori di validazione dobbiamo aggiungere tra il parametro BindingResult che controlla se
        * ci sono errori con i dati di fusaDto ---
        * --- In FusaDto per il campo imageFile non abbiamo una validazione gia impostata come con gli altri parametri, ma
        * è importante che sia presente, quindi possiamo scriverla nel FusaService.java a mano:*/
        // if(fusaDto.getImageFile().isEmpty()){ -> per farlo direttamnete qua ma meglio nel service
        //     result.addError(new FieldError("fusaDto","imageFile", "the image file is required"));
        // }
        

        fusaService.validateImageFile(fusaDto, result);

        //controlliamo se è presente qualche errore di validazione:
        if(result.hasErrors()) {
            return "fusa/createFusa";
        }

        //se non abbiamo errori salviamo il file immagine nella cartella tramite il meodo creato nel service
        try {
            String storageFileName = fusaService.saveImage(fusaDto.getImageFile());
            
            //prendo l'utente tramite la session
            // User user = userRepo.findByUsername(userDetails.getUsername());
            // User user = userRepo.findById((Integer) session.getAttribute("user")).get();

            //salviamo l'elemento nel db
            if (principalUser instanceof CustomOAuth2User) {
                // Se l'utente è autenticato con OAuth2
                CustomOAuth2User oauthUser = (CustomOAuth2User) principalUser;
                User user = oauthUser.getUser(); // Recupera l'oggetto User direttamente dal CustomOAuth2User

                fusaService.saveFusa(fusaDto, storageFileName, user.getUsername());
       
            }
            if (userDetails != null) {
                fusaService.saveFusa(fusaDto, storageFileName, userDetails.getUsername());
            }

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return "fusa/createFusa";
        }
        
        return "redirect:/fusa";
    }
    

    //aggiornamento/mofifica
    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id) {

        try {
            
            /* findById(id) -> cerca un'entità nel database utilizzando il suo id; 
            Il metodo .get() estrae il valore contenuto, cioè l'oggetto Fusa, se è presente. 
            l'id ci viene passato dal tasto con th:href="@{/account/modificautente(id=${utente.id})} e qui prendendolo con @RequestParam int id" */
            Fusa fusa = fusa_repo.findById(id).get();
            model.addAttribute("fusa",fusa );

            //- oltre a passare l'oggetto da modificare passiamo anche l'oggetto dto con i parametri dell'elemento selezionato,
            // che sarà accessibile alla pagina e servirà per la modifica con la form
            FusaDto fusaDto = new FusaDto();
            fusaDto.setTitolo(fusa.getTitolo());
            // fusaDto.setCategory(fusa.getCategory());
            fusaDto.setContenuto(fusa.getContenuto());
            model.addAttribute("fusaDto", fusaDto);
            
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return "redirect:/fusa";
        }

        return "fusa/editFusa";
    }


    @PostMapping("/edit")
    public String updateProduct(Model model, @RequestParam int id, @Valid @ModelAttribute FusaDto fusaDto, BindingResult result) {
        
        try {
    
            Fusa fusa = fusa_repo.findById(id).get();
            model.addAttribute("fusa", fusa);

            /*controlliamo se è presente qualche errore di validazione e se è presente rimandiamo su editFusa, e saremo
            * in grado di vedere l'elemento corrente della modifica perché abbiamo il prodotto che passiamo nel model e anche il 
            * fusaDto */
            if(result.hasErrors()) {
                return "fusa/editFusa";
            }
            // verifichiamo se abbiamo l'immagine o no:
            if(!fusaDto.getImageFile().isEmpty()){
                //cancella la vecchia immagine
                String uploadDir = "src/main/resources/static/images/";
                Path oldImagePath = Paths.get(uploadDir + fusa.getImageFileName());

                try {
                    Files.delete(oldImagePath);
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }

                //salva la muova immagine
                MultipartFile image = fusaDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }

                //salviamo nel fusa per poter essere salvato poi nel db
                fusa.setImageFileName(storageFileName);

            }

            //salviamo nel db dai dati del submit che si trovano nel fusaDto
            fusa.setTitolo(fusaDto.getTitolo());
            // fusa.setCategory(fusaDto.getCategory());
            fusa.setContenuto(fusaDto.getContenuto());
            fusa_repo.save(fusa);
            
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return "redirect:/fusa";
        }

        return "redirect:/fusa";
    }
    

    //delete
    /*
     * Per passare l'utente autenticato al service senza usare direttamente SecurityContextHolder, un metodo pulito è sfruttare 
     * l'annotazione @AuthenticationPrincipal nel controller per ottenere l'utente autenticato e poi passare tale utente o 
     * semplicemente il suo ID al service.
     */
    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id, @AuthenticationPrincipal CustomOAuth2User oauthUser, @AuthenticationPrincipal UserDetails userDetails) {


        User user;

        if(oauthUser !=null) {

            user = oauthUser.getUser();
        }else {
            user = userService.getUserByUsername(userDetails.getUsername());
        }


        try {
            Fusa fusa = fusa_repo.findById(id).get();
            //eliminazione immagine
            Path imagePath = Paths.get("public/images/" + fusa.getImageFileName());

            try {   
                Files.delete(imagePath);
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }

           fusaService.delete(fusa, user.getId());

            
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        return "redirect:/fusa";
        
    }
    


}
