package com.example.sae_s501;

import android.app.Notification;

import com.example.sae_s501.model.Utilisateur;

import java.util.List;

public class Publication {
    private Long id;
    private String titre;
    private String description;
    private Boolean gratuit;
    private Boolean publique;
    private String image;
    private String fichier;
    private int nb_telechargement;
    private List<Panier> paniers;
    private Notification notification;
    private Utilisateur proprietaire;

    public Publication(Long id, String titre, String description, Boolean gratuit, Boolean publique, String image, String fichier, int nb_telechargement, List<Panier> paniers, Notification notification, Utilisateur proprietaire) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.gratuit = gratuit;
        this.publique = publique;
        this.image = image;
        this.fichier = fichier;
        this.nb_telechargement = nb_telechargement;
        this.paniers = paniers;
        this.notification = notification;
        this.proprietaire = proprietaire;
    }

    public Publication() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getGratuit() {
        return gratuit;
    }

    public void setGratuit(Boolean gratuit) {
        this.gratuit = gratuit;
    }

    public Boolean getPublique() {
        return publique;
    }

    public void setPublique(Boolean publique) {
        this.publique = publique;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFichier() {
        return fichier;
    }

    public void setFichier(String fichier) {
        this.fichier = fichier;
    }

    public int getNb_telechargement() {
        return nb_telechargement;
    }

    public void setNb_telechargement(int nb_telechargement) {
        this.nb_telechargement = nb_telechargement;
    }

    public List<Panier> getPaniers() {
        return paniers;
    }

    public void setPaniers(List<Panier> paniers) {
        this.paniers = paniers;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Utilisateur getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(Utilisateur proprietaire) {
        this.proprietaire = proprietaire;
    }
}
