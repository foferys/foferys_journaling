package com.foferys_journal.fofejournal.models.builder;


import java.util.Date;

import com.foferys_journal.fofejournal.models.Fusa;
import com.foferys_journal.fofejournal.models.FusaDto;
import com.foferys_journal.fofejournal.models.User;



public class FusaBuilder {


    // Questa classe si occupa della conversione tra DTO ed entity.
    public static Fusa toEntity(FusaDto fusaDto, User user, String imageFileName) {

        Fusa product = new Fusa();
        product.setTitolo(fusaDto.getTitolo());
        // product.setCategory(fusaDto.getCategory());
        product.setContenuto(fusaDto.getContenuto());
        // product.setCreatedAt(new Date());
        product.setImageFileName(imageFileName);
        product.setUser(user); // -> Associo l'utente al prodotto creato tramite la relazione molti-a-uno (@ManyToOne) creata tra Product e User e il mapping 
        //con @JoinColumn(name = "user_id"), JPA imposterà automaticamente il valore dell'ID dell'utente (il campo id dell'entità User) nella colonna 
        //user_id della tabella product.

        return product;
    }

    // public static ProductDto toDto(Product product) {
    //     ProductDto productDto = new ProductDto();
        
    //     productDto.setName(product.getName());
    //     productDto.setBrand(product.getBrand());
    //     productDto.setCategory(product.getCategory());
    //     productDto.setPrice(product.getPrice());
    //     productDto.setDescription(product.getDescription());
    //     productDto.setCreatedAt(product.getCreatedAt());
    //     productDto.setImageFile(product.getImageFileName());
    //     productDto.setUser(product.getUser());
    // }


}
