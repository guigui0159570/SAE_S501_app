package com.example.sae_s501;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
public class FilActu extends AppCompatActivity {

    private static final String TAG = "FilActu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fil_actualiteresp);
        Log.d(TAG, "onCreate: la page est utilisée");

        // Ajoutez le fragment à l'activité
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new FilActuFragment())
                .commit();
        Log.d(TAG, "Transaction fragment");
    }
}
