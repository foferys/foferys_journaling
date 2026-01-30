package com.foferys_journal.fofejournal.models.builder;

import java.time.LocalDate;
import java.util.Date;

import com.foferys_journal.fofejournal.models.Fusa;
import com.foferys_journal.fofejournal.models.FusaApiRequest;
import com.foferys_journal.fofejournal.models.FusaDto;
import com.foferys_journal.fofejournal.models.User;



public class FusaBuilder {


    // Questa classe si occupa della conversione tra DTO ed entity.
    public static Fusa toEntity(FusaDto fusaDto, User user, String imageFileName) {

        LocalDate today = LocalDate.now();

        Fusa fusa = new Fusa();
        fusa.setTitolo(fusaDto.getTitolo());
        // fusa.setCategory(fusaDto.getCategory());
        fusa.setContenuto(fusaDto.getContenuto());
        fusa.setDataCreazione(today);
        fusa.setImageFileName(imageFileName);
        fusa.setUser(user); // -> Associo l'utente al prodotto creato tramite la relazione molti-a-uno (@ManyToOne) creata tra fusa e User e il mapping 
        //con @JoinColumn(name = "user_id"), JPA imposterà automaticamente il valore dell'ID dell'utente (il campo id dell'entità User) nella colonna 
        //user_id della tabella fusa.

        return fusa;
    }

    /** Crea una Fusa da una richiesta API (senza immagine). */
    public static Fusa toEntityFromApi(FusaApiRequest request, User user) {
        LocalDate today = LocalDate.now();
        Fusa fusa = new Fusa();
        fusa.setTitolo(request.getTitolo());
        fusa.setContenuto(request.getContenuto());
        fusa.setDataCreazione(today);
        fusa.setImageFileName(null);
        fusa.setUser(user);
        return fusa;
    }

    // public static fusaDto toDto(fusa fusa) {
    //     FusaDto fusaDto = new FusaDto();
        
    //     fusaDto.setName(fusa.getName());
    //     fusaDto.setBrand(fusa.getBrand());
    //     fusaDto.setCategory(fusa.getCategory());
    //     fusaDto.setPrice(fusa.getPrice());
    //     fusaDto.setDescription(fusa.getDescription());
    //     fusaDto.setCreatedAt(fusa.getCreatedAt());
    //     fusaDto.setImageFile(fusa.getImageFileName());
    //     fusaDto.setUser(fusa.getUser());
    // }


}
