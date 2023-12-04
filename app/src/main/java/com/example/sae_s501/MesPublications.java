package com.example.sae_s501;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MesPublications extends AppCompatActivity {

    private ImageView ajout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mes_publications);

        // Ajoutez le fragment à l'activité
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_pub, new MesPublicationsFrag())
                .commit();

        ajout = findViewById(R.id.ajout_pub);

        ajout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MesPublications.this, AjoutPublication.class);
                startActivity(intent);
            }


            });

    }
}

