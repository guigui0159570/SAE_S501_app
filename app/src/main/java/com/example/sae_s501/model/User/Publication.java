package com.example.sae_s501.model.User;

import android.app.Notification;

import com.example.sae_s501.model.User.Avis;
import com.example.sae_s501.model.Utilisateur;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Publication {
    private Long id;
    @SerializedName("titre")
    private String titre;
    @SerializedName("description")
    private String description;
    private Boolean gratuit;
    private Boolean publique;
    private Float prix;
    @SerializedName("image")
    private String image;
    @SerializedName("fichier")
    private String fichier;
    private int nb_telechargement;
    private List<Object> paniers;
    private Notification notification;
    private com.example.sae_s501.model.Utilisateur proprietaire;
    private List<Avis> avis = new ArrayList<>();

    public Publication(Long id, String titre, String description, Boolean gratuit, Boolean publique, Float prix, String image, String fichier, int nb_telechargement, List<Object> paniers, Notification notification, com.example.sae_s501.model.Utilisateur proprietaire, List<Avis> avis) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.gratuit = gratuit;
        this.publique = publique;
        this.prix = prix;
        this.image = image;
        this.fichier = fichier;
        this.nb_telechargement = nb_telechargement;
        this.paniers = paniers;
        this.notification = notification;
        this.proprietaire = proprietaire;
        this.avis = avis;
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

    public List<Object> getPaniers() {
        return paniers;
    }

    public void setPaniers(List<Object> paniers) {
        this.paniers = paniers;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public com.example.sae_s501.model.Utilisateur getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(Utilisateur proprietaire) {
        this.proprietaire = proprietaire;
    }

    public Float getPrix() {
        return prix;
    }

    public void setPrix(Float prix) {
        this.prix = prix;
    }

    public List<Avis> getAvis() {
        return avis;
    }

    public void setAvis(List<Avis> avis) {
        this.avis = avis;
    }
}
