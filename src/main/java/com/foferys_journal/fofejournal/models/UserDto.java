package com.foferys_journal.fofejournal.models;


import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;

public class UserDto {


    private String nome;

    @NotEmpty(message = "The username is required")
	private String username;

    @NotEmpty(message = "The password is required")
	private String password;

	private MultipartFile img;

    private String oldPassword;
	private String confermaPass;





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

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getConfermaPass() {
        return confermaPass;
    }

    public void setConfermaPass(String confermaPass) {
        this.confermaPass = confermaPass;
    }


    
    
}
