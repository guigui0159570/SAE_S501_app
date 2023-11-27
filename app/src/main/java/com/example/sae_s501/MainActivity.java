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
        setContentView(R.layout.connexion);
        // SessionManager.deleteToken(this);
        SessionManager.isSessionValid(this);
    }

    private void redirectToLoginScreen() {
        Intent intent = new Intent(this, Connexion.class);
        startActivity(intent);
    }

    public void onNouveauInscriptionClick(View view) {
        Intent intent = new Intent(this, Inscription.class);
        startActivity(intent);
    }
    public void OnClickConnexion(View view) {
        Intent intent = new Intent(this, AjoutPublication.class);
        startActivity(intent);
    }

}