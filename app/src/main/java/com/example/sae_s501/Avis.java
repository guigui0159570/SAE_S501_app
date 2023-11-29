package com.example.sae_s501;


import com.example.sae_s501.Publication;
import com.example.sae_s501.Utilisateur;

public class Avis {

    private Long id;
    private String commentaire;
    private int etoile;

    private com.example.sae_s501.Publication publication;

    private com.example.sae_s501.Utilisateur utilisateur;

    public com.example.sae_s501.Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public int getEtoile() {
        return etoile;
    }

    public void setEtoile(int etoile) {
        this.etoile = etoile;
    }

    public com.example.sae_s501.Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }
}

