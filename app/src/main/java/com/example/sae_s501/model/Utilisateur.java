package com.example.sae_s501.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Utilisateur implements Serializable {
    @SerializedName("pseudo")
    private String pseudo;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public Utilisateur(String pseudo, String email, String password) {
        this.pseudo = pseudo;
        this.email = email;
        this.password = password;
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
