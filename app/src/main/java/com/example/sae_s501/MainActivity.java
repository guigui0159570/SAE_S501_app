package com.example.sae_s501;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.example.sae_s501.retrofit.SessionManager;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexionresp);
        // SessionManager.deleteToken(this);
        SessionManager.isSessionValid(this);
    }

    @Override
    protected void onDestroy() {
        // Supprimer les préférences partagées au moment de la fermeture de l'application
        clearSessionCheckPreferences();

        super.onDestroy();
    }

    private void redirectToLoginScreen() {
        Intent intent = new Intent(this, Connexion.class);
        startActivity(intent);
    }



    private boolean isSessionCheckPerformed() {
        // Utiliser les préférences partagées pour vérifier si la vérification de session a déjà été effectuée
        SharedPreferences preferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        return preferences.getBoolean("isSessionCheckPerformed", false);
    }

    private void setSessionCheckPerformed(boolean performed) {
        // Mettre à jour l'état de la vérification de session dans les préférences partagées
        SharedPreferences preferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isSessionCheckPerformed", performed);
        editor.apply();
    }



    private void clearSessionCheckPreferences() {
        // Utiliser le contexte de l'activité pour accéder aux préférences partagées
        SharedPreferences preferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Supprimer les préférences liées à la vérification de session
        editor.remove("isSessionCheckPerformed");

        // Appliquer les changements
        editor.apply();
    }
}