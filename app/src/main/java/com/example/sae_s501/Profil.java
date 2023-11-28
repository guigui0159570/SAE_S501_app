package com.example.sae_s501;

public class Profil {

    private Long id;
    private String description;
    private String photo;
    private Utilisateur utilisateur;

    public Profil(String description, String photo, Utilisateur utilisateur) {
        this.description = description;
        this.photo = photo;
        this.utilisateur = utilisateur;
    }

    public Profil() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
}
