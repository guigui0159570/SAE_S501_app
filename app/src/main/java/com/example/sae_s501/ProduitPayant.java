package com.example.sae_s501;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sae_s501.authentification.Authentification;
import com.example.sae_s501.retrofit.FilActuService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProduitPayant extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.produit_payantresp);
        long publicationId = getIntent().getLongExtra("id", 0);
        loadPublication(this.getCurrentFocus(), publicationId);
    }

    //il faut modifier les pages pour bien afficher les données
    private void loadPublication(View view, long publicationId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Dictionnaire.getIpAddress())
                .addConverterFactory(GsonConverterFactory.create())
                .client(Authentification.createAuthenticatedClient(getApplicationContext()))
                .build();

        FilActuService filActuService = retrofit.create(FilActuService.class);

        Call<Publication> call = filActuService.getPublicationById(publicationId);
        call.enqueue(new Callback<Publication>() {
            @Override
            public void onResponse(Call<Publication> call, Response<Publication> response) {
                if (response.isSuccessful()) {
                    // Traitement des données de la publication ici
                    Publication publication = response.body();
                    if (publication != null) {
                        TextView titre = view.findViewById(R.id.ajout_pub_titre);titre.setText(publication.getTitre());
                        TextView description = view.findViewById(R.id.charger_img); description.setText(publication.getDescription());
                        //ImageView img_profil = view.findViewById(R.id.imageViewPub);img_profil.setImageDrawable(publication.getImage());
                    }
                } else {
                    return;
                }
            }

            @Override
            public void onFailure(Call<Publication> call, Throwable t) {
                // Gestion des erreurs de connexion ici
                t.printStackTrace();
            }
        });
    }
}
