package com.example.sae_s501;

import java.util.HashSet;
import java.util.Set;

public class Utilisateur {

    private Long id;
    private String email;
    private String pseudo;
    private String password;

    private Set<Utilisateur> abonnements = new HashSet<>();
    private Set<Utilisateur> abonnes = new HashSet<>();

    private Profil profil;

    private Set<Publication> publications;

    private Set<Avis> avis;

    private Panier panier;

    public Set<Avis> getAvis() {
        return avis;
    }

    public void setAvis(Set<Avis> avis) {
        this.avis = avis;
    }

    public Set<Publication> getPublications() {
        return publications;
    }

    public void setPublications(Set<Publication> publications) {
        this.publications = publications;
    }

    public Profil getProfil() {
        return profil;
    }

    public void setProfil(Profil profil) {
        this.profil = profil;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Set<Utilisateur> getAbonnements() {
        return abonnements;
    }

    public void setAbonnements(Set<Utilisateur> abonnements) {
        this.abonnements = abonnements;
    }

    public Set<Utilisateur> getAbonnes() {
        return abonnes;
    }

    public void setAbonnes(Set<Utilisateur> abonnes) {
        this.abonnes = abonnes;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Panier getPanier() {
        return panier;
    }

    public void setPanier(Panier panier) {
        this.panier = panier;
    }
}

