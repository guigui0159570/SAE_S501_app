package com.example.sae_s501;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sae_s501.authentification.Authentification;
import com.example.sae_s501.retrofit.AuthService;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Connexion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexion);
        EditText editTextEmail = findViewById(R.id.edit_connexion_mail);
        EditText editTextPassword = findViewById(R.id.edit_connexion_mdp);
        Button button = findViewById(R.id.btn_connexion);
        button.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  // Créez un thread pour effectuer l'opération réseau
                  Authentification.authenticateWithCredentials(
                          editTextEmail.getText().toString(),
                          editTextPassword.getText().toString(),
                          getBaseContext(),
                          new Authentification.AuthCallback() {
                              @Override
                              public void onAuthSuccess() {
                                  // L'authentification a réussi, vous pouvez effectuer des actions ici
                                  Intent intent = new Intent(getBaseContext(), Inscription.class);
                                  startActivity(intent);
                              }

                              @Override
                              public void onAuthError(String errorMessage) {
                                  // Gérer les erreurs d'authentification ici
                                  Toast.makeText(Connexion.this, "Erreur d'authentification: " + errorMessage, Toast.LENGTH_SHORT).show();
                              }
                          }
                  );

              }
        });

    }


}
