package com.foferys_journal.fofejournal.services;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import com.foferys_journal.fofejournal.models.Fusa;
import com.foferys_journal.fofejournal.models.FusaDto;
import com.foferys_journal.fofejournal.models.User;
import com.foferys_journal.fofejournal.models.builder.FusaBuilder;


// La classe di servizio gestisce la logica di business e interagisce con il repository.
@Service
public class FusaService {
    
    @Autowired
    private FusaRepository fusaRepository; 
    @Autowired
    private UserRepository uRepo;
    

    //costruttore se non uso @component o @service ecc alla classe che voglio ignettare 
    // public ProductService(ProductsRepository productsRepository, UserRepository userRepository) {

    //     this.pRepo = productsRepository;
    //     this.uRepo = userRepository;

    // }


    
    public void validateImageFile(FusaDto fusaDto, BindingResult result) {
        /* --- In ProductDto per il campo imageFile non abbiamo una validazione gia impostata come con gli altri parametri, ma
        * è importante che sia presente, quindi possiamo scriverla qui a mano:*/
        if (fusaDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "the image file is required"));
        }
    }



    public String saveImage(MultipartFile image) throws IOException {
        Date createdAt = new Date(); /*createdAt.getTime() restituisce 1725088960724, Questo numero è semplicemente un conteggio dei millisecondi trascorsi dall'inizio dell'epoca UNIX (1 gennaio 1970). */
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
        String uploadDir = "src/main/resources/static/images/";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        /* Questo blocco di codice mostra un'implementazione del concetto di try-with-resources, una funzionalità introdotta in Java 7 che semplifica 
        la gestione delle risorse che devono essere chiuse, come file, stream o connessioni di rete. Esaminiamo ogni parte del codice:
        InputStream inputStream = image.getInputStream(): Questo esprime che stai aprendo una risorsa, in questo caso un InputStream ottenuto dall'oggetto image. 
        Il metodo image.getInputStream() restituisce un flusso di input che può essere utilizzato per leggere i dati contenuti in image.

        -inputStream: È il flusso di input da cui leggere i dati. In questo caso, i dati vengono letti dall'immagine rappresentata dall'oggetto image.
        -Paths.get(uploadDir + storageFileName): Questo costruisce il percorso completo dove il file verrà salvato. uploadDir è la directory in cui il file 
            verrà memorizzato, e storageFileName è il nome del file.
        -StandardCopyOption.REPLACE_EXISTING: Questa opzione specifica che, se un file esiste già nella destinazione con lo stesso nome, esso verrà sovrascritto.*/
        try (InputStream inputStream = image.getInputStream()) {
            Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
        }

        return storageFileName;
    }

    public void saveProduct(FusaDto fusaDto, String imageFileName, String username) {

        User user = uRepo.findByUsername(username);

        //accediamo direttamente al metodo statico del builder passandogli i dati di cui ha bisogno
        Fusa fusa = FusaBuilder.toEntity(fusaDto, user, imageFileName);

        fusaRepository.save(fusa);
    }



}
