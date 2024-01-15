package com.example.sae_s501;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MesAchats extends AppCompatActivity {

    private ImageView close;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mes_achats);
        close = findViewById(R.id.closeMesAchats);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_pub_achats, new MesPubAchete())
                .commit();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MesAchats.this, MesPublications.class);
                startActivity(intent);
            }


        });


    }
}

