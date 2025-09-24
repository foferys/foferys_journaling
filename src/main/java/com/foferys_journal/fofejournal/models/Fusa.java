package com.foferys_journal.fofejournal.models;



import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "fusa")
public class Fusa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //per farlo autoincrement
    private int id;
    
    private String titolo;  
    
    @Column(columnDefinition = "TEXT") // permette testi lunghi SE NON VIENE DEFINITO si imposta su varchar!
    private String contenuto;

    private LocalDateTime dataCreazione;
    private LocalDateTime dataModifica;

    // private Date createdAt;
    private String imageFileName;

    /*La relazione è gestita da JPA e annotata con @ManyToOne, che indica che più prodotti possono essere associati a un singolo utente.
    L'annotazione @JoinColumn(name = "user_id") specifica che la colonna della tabella product che conterrà la chiave esterna dell'utente è user_id.
    !!!Quando chiami il metodo setUser(user);, JPA si occupa di impostare l'ID dell'utente nella colonna user_id nella tabella product durante il salvataggio.
    
    Differenza tra LAZY e EAGER
    LAZY (Pigro): L'entità correlata viene caricata quando è necessaria (cioè quando accedi esplicitamente a essa nel codice). 
    Questo comportamento evita il caricamento di dati inutili e migliora le prestazioni.

    EAGER (Avido): L'entità correlata viene caricata immediatamente. È utile quando sai che utilizzerai l'entità correlata ogni 
    volta che accedi all'entità principale.

    Esempio pratico di FetchType.LAZY in azione:
    Product product = productRepository.findById(1L).get();
    // Non viene ancora eseguita nessuna query per l'utente

    User user = product.getUser();  
    // Ora JPA esegue una query al database per recuperare l'utente associato,
    // perché il caricamento è "pigro" e si attiva solo quando richiesto
    */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
   
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
    // public Date getCreatedAt() {
    //     return createdAt;
    // }
    // public void setCreatedAt(Date createdAt) {
    //     this.createdAt = createdAt;
    // }
    public String getImageFileName() {
        return imageFileName;
    }
    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }


    
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }



}
