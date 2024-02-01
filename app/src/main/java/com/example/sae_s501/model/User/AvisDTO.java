package com.example.sae_s501.model.User;

public class AvisDTO {
    private Long id;
    private String commentaire;
    private int etoile;

    private long publication_id;

    private long utilisateur_id;

    public long getUtilisateur() {
        return utilisateur_id;
    }

    public void setUtilisateur(Long utilisateur) {
        this.utilisateur_id = utilisateur;
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

    public long getPublication() {
        return publication_id;
    }

    public void setPublication(Long publication) {
        this.publication_id = publication;
    }
}
