package com.example.sae_s501.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Publication implements Serializable {
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("gratuit")
    private boolean gratuit;
    @SerializedName("prix")
    private float prix;

    public Publication() {
    }

    public Publication(String title, String description, boolean gratuit, float prix) {
        this.title = title;
        this.description = description;
        this.gratuit = gratuit;
        this.prix = prix;
    }

    // Les getters et setters peuvent être générés automatiquement par votre IDE

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isGratuit() {
        return gratuit;
    }

    public void setGratuit(boolean gratuit) {
        this.gratuit = gratuit;
    }

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }
}
