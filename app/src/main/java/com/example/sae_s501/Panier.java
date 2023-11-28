package com.example.sae_s501;

import java.util.HashSet;
import java.util.Set;

public class Panier {

    private Long idPanier;
    private float prixTT;
    private boolean etat;

    private Utilisateur proprietaire;


    private Set<Publication> publications = new HashSet<>();


    public Panier(float prixTT, boolean etat, Utilisateur proprietaire, Set<Publication> publications) {
        this.prixTT = prixTT;
        this.etat = etat;
        this.proprietaire = proprietaire;
        this.publications = publications;
    }

    public Panier() {
    }

    public Long getIdPanier() {
        return idPanier;
    }

    public void setIdPanier(Long idPanier) {
        this.idPanier = idPanier;
    }

    public float getPrixTT() {
        return prixTT;
    }

    public void setPrixTT(float prixTT) {
        this.prixTT = prixTT;
    }

    public boolean isEtat() {
        return etat;
    }

    public void setEtat(boolean etat) {
        this.etat = etat;
    }

    public Set<Publication> getPublications() {
        return publications;
    }

    public void setPublications(Set<Publication> publications) {
        this.publications = publications;
    }

    public Utilisateur getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(Utilisateur proprietaire) {
        this.proprietaire = proprietaire;
    }
}

