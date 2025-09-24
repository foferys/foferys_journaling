package com.foferys_journal.fofejournal.models;


import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;

public class UserDto {

	private int id;

    private String nome;

    @NotEmpty(message = "The username is required")
	private String username;

    @NotEmpty(message = "The password is required")
	private String password;

	private MultipartFile img;





    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public MultipartFile getImg() {
        return img;
    }

    public void setImg(MultipartFile img) {
        this.img = img;
    }




    
    
}
