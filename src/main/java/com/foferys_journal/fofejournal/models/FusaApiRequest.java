package com.foferys_journal.fofejournal.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * DTO per richieste API REST (POST/PUT) – solo titolo e contenuto in JSON.
 */
public class FusaApiRequest {

    @NotEmpty(message = "Il titolo è obbligatorio")
    private String titolo;

    @Size(min = 10, max = 2000, message = "Il contenuto deve essere tra 10 e 2000 caratteri")
    private String contenuto;

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
}
