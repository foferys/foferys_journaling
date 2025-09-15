package com.foferys_journal.fofejournal.services;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.foferys_journal.fofejournal.models.Fusa;

import java.util.*;

/* useremo questa classe per leggere e scrivere i "prodotti" sul database grazie all'implementazione di jpaRepository 
 * attraverso il controller specofico dei prodotti
 * Questa interfaccia estende JpaRepository e si occupa di tutte le operazioni CRUD.
 * Può contenere query personalizzate, ma in modo centralizzato.
*/
public interface FusaRepository extends JpaRepository<Fusa, Integer>{


    List<Fusa> findByUserId(int userId);

    /* 
    questa classe può avere query personalizzate. Queste query possono essere scritte in JPQL o SQL 
    nativo utilizzando annotazioni come @Query oppure metodi derivati automaticamente da Spring Data JPA.
    */
    // Trova tutti i clienti con un determinato nome Query personalizzata con JPQL (@Query)
    @Query("SELECT f FROM Fusa f WHERE f.titolo = :titolo")
    List<Fusa> findByTitolo(@Param("titolo") String titolo);

    // Query con SQL nativo
    // Trova tutti i clienti con un determinato nome usando SQL nativo; nativeQuery = true permette di usare SQL puro. Utile quando JPQL non basta.
    @Query(value = "SELECT * FROM fusa WHERE titolo = :titolo", nativeQuery = true)
    List<Fusa> findByNameNative(@Param("titolo") String titolo);
}
