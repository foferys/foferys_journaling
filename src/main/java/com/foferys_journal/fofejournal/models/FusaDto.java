package com.foferys_journal.fofejournal.models;


import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.*;

public class FusaDto {

    /*
    L'uso del DTO (Data Transfer Object ) in questo progetto serve principalmente per:
    1. Separare  separare il modello di dominio (entity) dal modello di trasferimento dati..
    Controllare quali dati vengono esposti all'interfaccia utente.
    Gestire la validazione dei dati in ingresso.
        ✅ Perché è utile?
        -Evita di esporre direttamente l'entity, proteggendola da modifiche indesiderate.
        -Può contenere solo i dati necessari, evitando informazioni superflue.
        -Può essere esteso con dati aggiuntivi utili per la vista/API senza modificare l'entity.
    */

    @NotEmpty(message = "The name is required")
    private String titolo;    

    // @NotEmpty(message = "The category is required")
    // private String category;

    @Size(min = 10, message = "The description should be at least 10 characters")
    @Size(max = 2000, message = "The description should be at least 10 characters")
    private String contenuto;

    private LocalDateTime dataCreazione;
    private LocalDateTime dataModifica;

    private MultipartFile imageFile;



    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getContenuto() {
        return contenuto;
    }

    public void setContenuto(String contenuto) {
        this.contenuto = contenuto;
    }

    

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public LocalDateTime getDataModifica() {
        return dataModifica;
    }

    public void setDataModifica(LocalDateTime dataModifica) {
        this.dataModifica = dataModifica;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }


    
}
