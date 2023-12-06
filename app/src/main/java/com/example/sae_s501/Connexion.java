package com.example.sae_s501;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sae_s501.authentification.Authentification;
import java.util.Locale;


public class Connexion extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexionresp);
        EditText editTextEmail = findViewById(R.id.edit_connexion_mail);
        EditText editTextPassword = findViewById(R.id.edit_connexion_mdp);
        TextView mdpOublie = findViewById(R.id.mdp_oublie);
        Button button = findViewById(R.id.btn_connexion);

        ImageView langue = findViewById(R.id.langueco);
        Locale currentLocale = getResources().getConfiguration().locale;

        if (currentLocale.getLanguage().equals("en")) {
            langue.setImageResource(R.drawable.france_flag);
        } else {
            langue.setImageResource(R.drawable.greatbritain);
        }
        langue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtenez la locale actuelle de l'application

                // Changer la langue de l'application
                if (currentLocale.getLanguage().equals("en")) {
                    setLocale("fr");
                } else {
                    setLocale("en");
                }
                recreate();
            }
        });
        mdpOublie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MdpOublie.class);
                startActivity(intent);
            }
        });
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
                                  Intent intent = new Intent(getBaseContext(), MesPublications.class);
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

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
    }

    public void onNouveauInscriptionClick(View view) {
        Intent intent = new Intent(this, Inscription.class);
        startActivity(intent);
    }
}
