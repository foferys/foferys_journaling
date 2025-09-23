package com.foferys_journal.fofejournal.services;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import com.foferys_journal.fofejournal.models.User;
import com.foferys_journal.fofejournal.models.UserDto;
import com.foferys_journal.fofejournal.models.builder.UserDtoBuilder;

import jakarta.transaction.Transactional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    public void validateImageFile(UserDto userDto, BindingResult result) {
        /* --- In UserDto per il campo imageFile non abbiamo una validazione gia impostata come con gli altri parametri, ma
        * è importante che sia presente, quindi possiamo scriverla qui a mano:*/
        if (userDto.getImg().isEmpty()) {
            result.addError(new FieldError("userDto", "img", "the image file is required"));
        }
    }




    public String saveImage(MultipartFile image) throws IOException {
        
        /* Questo blocco di codice mostra un'implementazione del concetto di try-with-resources, una funzionalità introdotta in Java 7 che semplifica 
        la gestione delle risorse che devono essere chiuse, come file, stream o connessioni di rete. Esaminiamo ogni parte del codice:
        InputStream inputStream = image.getInputStream(): Questo esprime che stai aprendo una risorsa, in questo caso un InputStream ottenuto dall'oggetto image. 
        Il metodo image.getInputStream() restituisce un flusso di input che può essere utilizzato per leggere i dati contenuti in image.
        
        -inputStream: È il flusso di input da cui leggere i dati. In questo caso, i dati vengono letti dall'immagine rappresentata dall'oggetto image.
        -Paths.get(uploadDir + storageFileName): Questo costruisce il percorso completo dove il file verrà salvato. uploadDir è la directory in cui il file 
        verrà memorizzato, e storageFileName è il nome del file.
        -StandardCopyOption.REPLACE_EXISTING: Questa opzione specifica che, se un file esiste già nella destinazione con lo stesso nome, esso verrà sovrascritto.*/
        
        String userImageName = image.getOriginalFilename();

        try {
            String uploadDir = "src/main/resources/static/images/";
            Path uploadPath = Paths.get(uploadDir);

            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + userImageName), StandardCopyOption.REPLACE_EXISTING);
                // System.out.println("sono ne try caricamento nella cartella");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


        } catch (Exception e) {
            System.out.println("errore-> " + e.getMessage());
        }

        return userImageName;
    }

    @Transactional
    public User saveUser(UserDto userDto, String imageFileName) {

        // fare controllo su esistenza username 
        // if(userRepository.findByUsername(userDto.getUsername()).isPresent()){
        //     throw new RuntimeException("Username gia presente: " + userDto.getUsername());
        // }

        User insertUser = UserDtoBuilder.UserFromDtoToEntity(userDto, imageFileName, passwordEncoder.encode(userDto.getPassword()));

        return userRepository.save(insertUser);
    }


    @Transactional
    public User updateUser(UserDto userDto, String imageFileName) {

        User user = userRepository.findByUsername(userDto.getUsername())
            .orElseThrow(() -> new RuntimeException("Utente non trovato: " + userDto.getUsername()));

        String passwConf = userDto.getConfermaPass();
		System.out.println("la password scritta è "+passwConf);
		System.out.println("la password scritta è uguale a quella hashata? "+ passwordEncoder.matches(passwConf, user.getPassword()));

        if (passwordEncoder.matches(passwConf, user.getPassword())) {
            
            // Se la password non corrisponde → errore
            if (!passwordEncoder.matches(passwConf, user.getPassword())) {
                throw new RuntimeException("Password di conferma non corretta");
            }
    
        }

        User insertUser = UserDtoBuilder.UserFromDtoToEntity(userDto, imageFileName, passwordEncoder.encode(userDto.getPassword()));
        return userRepository.save(insertUser);
        

    }


    public User getUserByUsername(String username) {

        User user = userRepository.findByUsername(username).get();

        return user;
    }


    public String getFakePass(String username) {
        User user = this.getUserByUsername(username);

        // Nascondo la password
        String pw = user.getPassword();
        
        // Genero una stringa con al massimo 20 '*' con lo strean al posto di un classico for
        String hiddenPw = IntStream.range(0, Math.min(pw.length(), 20)) // Genera numeri da 0 a min(length, 20)
                                .mapToObj(i -> "*")                  // Converte ogni numero in "*"
                                .collect(Collectors.joining());      // Unisce tutto in una stringa

        return hiddenPw;
    }

    




}
