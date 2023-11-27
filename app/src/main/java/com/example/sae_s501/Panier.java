package com.example.sae_s501;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sae_s501.MonCompte.MonCompteViewModel;

public class Panier extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.panierresp);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Button retour_panier = findViewById(R.id.btn_retour_panier);

        retour_panier.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MonCompteViewModel.class); //ne fonctionne pas
                startActivity(intent);
            }
        });
    }
}
