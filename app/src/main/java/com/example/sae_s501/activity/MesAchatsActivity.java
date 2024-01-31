package com.example.sae_s501.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sae_s501.MesPubAchete;
import com.example.sae_s501.R;

public class MesAchatsActivity extends AppCompatActivity {

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
                Intent intent = new Intent(MesAchatsActivity.this, MesPublicationsActivity.class);
                startActivity(intent);
            }


        });


    }
}

