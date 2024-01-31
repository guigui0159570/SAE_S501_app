package com.example.sae_s501.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sae_s501.R;
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.SessionManager;
import com.example.sae_s501.retrofit.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AideActivity extends AppCompatActivity {
    private TextView retour;
    private Button valideAide;
    private EditText texte_aide;
    private UserService userService;
    private RetrofitService retrofitService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aide);

        retour = findViewById(R.id.retourMyAccount);
        valideAide = findViewById(R.id.envoieAide);
        texte_aide = findViewById(R.id.editTextAide);

        String jwtEmail = SessionManager.getUserEmail(this);

        // Initialisation des services Retrofit pour les requêtes réseau
        retrofitService = new RetrofitService(this);
        userService = retrofitService.getRetrofit().create(UserService.class);

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirection
                Intent intent = new Intent(AideActivity.this, MyCompteActivity.class);
                startActivity(intent);
            }
        });
        valideAide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String texte_aide_string = texte_aide.getText().toString();

                Call<Void> aideMail = userService.envoieAide(jwtEmail,texte_aide_string);
                Log.d("aideMail", aideMail.toString());
                Log.d("aideMail", jwtEmail);
                Log.d("aideMail", texte_aide_string);

                aideMail.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            showToast("Un admin a été informé de votre demande.");
                            texte_aide.setText("");// Effacement du texte après envoi
                        }
                        else{
                            Log.d("ERREUR REQUETE", "ERREUR REQUETE" + response);
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        showToast("Erreur lors de la communication avec le serveur");
                    }
                });
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
