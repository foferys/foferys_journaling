package com.foferys_journal.fofejournal.models;

import java.time.LocalDate;

/**
 * DTO per risposte API REST – espone i campi della Fusa senza riferimenti all'entità User.
 */
public class FusaApiResponse {

    private int id;
    private String titolo;
    private String contenuto;
    private LocalDate dataCreazione;
    private LocalDate dataModifica;
    private String imageFileName;

    public FusaApiResponse() {
    }

    public static FusaApiResponse from(Fusa fusa) {
        FusaApiResponse r = new FusaApiResponse();
        r.setId(fusa.getId());
        r.setTitolo(fusa.getTitolo());
        r.setContenuto(fusa.getContenuto());
        r.setDataCreazione(fusa.getDataCreazione());
        r.setDataModifica(fusa.getDataModifica());
        r.setImageFileName(fusa.getImageFileName());
        return r;
    }

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

    public LocalDate getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDate dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public LocalDate getDataModifica() {
        return dataModifica;
    }

    public void setDataModifica(LocalDate dataModifica) {
        this.dataModifica = dataModifica;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }
}
