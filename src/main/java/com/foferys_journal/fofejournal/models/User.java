package com.foferys_journal.fofejournal.models;


import java.util.*;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String githubId;
    private String nome;
    private String username;
	private String password;
	
    private String img;
    
   

    // lista di prodotti con relazione uno a molti
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fusa> lista_fusa = new ArrayList<>();


    // Aggiungi prodotto all'utente
    public void addFusa(Fusa fusa) {
        lista_fusa.add(fusa);
        fusa.setUser(this);
    }

    // Rimuovi prodotto dall'utente
    public void removeFusa(Fusa fusa) {
        lista_fusa.remove(fusa);
        fusa.setUser(null);
    }



	public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getGithubId() {
        return githubId;
    }

    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }
    
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
   

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }

    public List<Fusa> getLista_fusa() {
        return lista_fusa;
    }

    public void setLista_fusa(List<Fusa> lista_fusa) {
        this.lista_fusa = lista_fusa;
    }
 

    
    
}
