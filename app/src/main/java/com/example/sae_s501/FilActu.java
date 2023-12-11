package com.example.sae_s501;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class FilActu extends AppCompatActivity {

    private static final String TAG = "FilActu";
    private EditText editTextFiltre;
    private ImageView imageButtonFiltre;
    private Boolean filtreActif = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fil_actualiteresp);

        editTextFiltre = findViewById(R.id.edit_rechercher_actu);
        imageButtonFiltre = findViewById(R.id.filtre_fil_actu);

        imageButtonFiltre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Bouton Filtre cliqué");
                filtreActif = true;

                if (!editTextFiltre.getText().toString().equals("")) {

                    // Ajoutez le fragment filtre à l'activité
                    FilActuFragFiltre fragmentFiltre = new FilActuFragFiltre();
                    fragmentFiltre.setFilterValue(editTextFiltre.getText().toString());

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragmentFiltre)
                            .commit();
                    Log.d(TAG, "Filtre fragment fil Actu");
                } else {
                    // Si le filtre n'est pas actif ou le texte est vide, affichez le fragment FilActuFragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new FilActuFragment())
                            .commit();
                    Log.d(TAG, "FilActuFragment");
                }
            }
        });

        // Si le filtre n'est pas actif, affichez le fragment FilActuFragment
        if (!filtreActif || editTextFiltre.getText().toString().equals("")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FilActuFragment())
                    .commit();
            Log.d(TAG, "FilActuFragment");
        }
    }
}
