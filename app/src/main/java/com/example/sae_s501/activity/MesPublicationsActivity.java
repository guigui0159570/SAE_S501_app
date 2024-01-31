package com.example.sae_s501.activity;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sae_s501.MesPublicationsFrag;
import com.example.sae_s501.MesPublicationsFragFiltre;
import com.example.sae_s501.R;

public class MesPublicationsActivity extends AppCompatActivity {

    private ImageView ajout;
    private EditText editTextFiltre;
    private ImageView imageButtonFiltre;
    private Boolean filtreActif = false;
    private ImageView coeur;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mes_publications);
        editTextFiltre = findViewById(R.id.edit_filtre);
        imageButtonFiltre = findViewById(R.id.imageButton);
        coeur = findViewById(R.id.pubAchat);
        imageButtonFiltre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Bouton Filtre cliqué");
                filtreActif = true;

                if (!editTextFiltre.getText().toString().equals("")) {

                    // Ajoutez le fragment filtre à l'activité
                    MesPublicationsFragFiltre fragmentFiltre = new MesPublicationsFragFiltre();
                    fragmentFiltre.setFilterValue(editTextFiltre.getText().toString());

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_pub, fragmentFiltre)
                            .commit();
                    Log.d(TAG, "Filtre fragment Mes publications");
                } else {
                    // Si le filtre n'est pas actif ou le texte est vide, affichez le fragment FilActuFragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_pub, new MesPublicationsFrag())
                            .commit();
                    Log.d(TAG, "FilActuFragment");
                }
            }
        });

        // Si le filtre n'est pas actif, affichez le fragment FilActuFragment
        if (!filtreActif || editTextFiltre.getText().toString().equals("")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_pub, new MesPublicationsFrag())
                    .commit();
            Log.d(TAG, "FilActuFragment");
        }


        ajout = findViewById(R.id.ajout_pub);

        ajout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MesPublicationsActivity.this, AjoutPublicationActivity.class);
                startActivity(intent);
            }


            });
        coeur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MesPublicationsActivity.this, MesAchatsActivity.class);
                startActivity(intent);
            }


        });


    }
}

