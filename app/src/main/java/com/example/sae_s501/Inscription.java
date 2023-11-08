package com.example.sae_s501;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class Inscription extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscription);

    }

    public void onInscriptionClick(View view) {
        Intent intent = new Intent(this, Connexion.class); // LoginActivity est l'activité de connexion, adaptez-le à votre projet.
        startActivity(intent);
    }
}
