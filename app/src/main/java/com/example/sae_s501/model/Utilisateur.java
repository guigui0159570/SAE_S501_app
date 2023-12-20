package com.example.sae_s501.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Utilisateur implements Serializable {
    @SerializedName("id")
    private Long id;
    @SerializedName("pseudo")
    private String pseudo;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public Utilisateur(Long id,String pseudo, String email, String password) {
        this.id = id;
        this.pseudo = pseudo;
        this.email = email;
        this.password = password;
    }

    public Utilisateur(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
