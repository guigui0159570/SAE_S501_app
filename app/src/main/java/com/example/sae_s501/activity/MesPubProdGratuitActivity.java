package com.example.sae_s501.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sae_s501.Dictionnaire;
import com.example.sae_s501.GlobalFunctionsPublication;
import com.example.sae_s501.Publication;
import com.example.sae_s501.R;
import com.example.sae_s501.authentification.Authentification;
import com.example.sae_s501.retrofit.FilActuService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MesPubProdGratuitActivity extends AppCompatActivity {
    private ImageView retour;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mes_pub_prod_gratuit);
        long publicationId = getIntent().getLongExtra("id", 0);
        View rootView = findViewById(android.R.id.content);
        loadPublication(rootView, publicationId);
        retour = findViewById(R.id.close);
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MesPubProdGratuitActivity.this, MesPublicationsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadPublication(View view, long publicationId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Dictionnaire.getIpAddress())
                .addConverterFactory(GsonConverterFactory.create())
                .client(Authentification.createAuthenticatedClient(MesPubProdGratuitActivity.this.getApplicationContext()))
                .build();

        FilActuService filActuService = retrofit.create(FilActuService.class);
        Log.d(TAG, "loadPublication: "+publicationId);
        Call<Publication> call = filActuService.getPublicationById(publicationId);
        call.enqueue(new Callback<Publication>() {
            @Override
            public void onResponse(@NonNull Call<Publication> call, @NonNull Response<Publication> response) {
                if (response.isSuccessful()) {
                    // Traitement des données de la publication ici
                    Publication publication = response.body();
                    if (publication != null) {
                        TextView titre = view.findViewById(R.id.ajout_pub_titre);titre.setText(publication.getTitre());
                        TextView description = view.findViewById(R.id.charger_img); description.setText(publication.getDescription());
                        TextView pseudo = view.findViewById(R.id.pseudo_pub_gratuit);
                        Log.d(TAG, "loadPublication: debut recuperation des objets");
                        RatingBar etoiles = view.findViewById(R.id.notation_gratuit);
                        Log.d(TAG, "loadPublication: fin recuperation des objets");


                        if(publication.getProprietaire() != null){
                            pseudo.setText(publication.getProprietaire().getPseudo());
                        }else{
                            pseudo.setText("Propriétaire non répertorié...");
                        }
                        Log.d("Id pub", ""+publicationId);
                        //récupération des avis de notre publication
                        GlobalFunctionsPublication.callAvisProdMesPub(filActuService,publicationId,
                                view,etoiles,true);

                        Call<ResponseBody> callImage = filActuService.getImage(publication.getImage());
                        Log.d("IMAGE", publication.getImage());

                        ImageView img_produit = view.findViewById(R.id.imageViewPub);

                        //Call pour l'ajout de l'image produit
                        GlobalFunctionsPublication.callImageProd(filActuService,publication,view);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Publication> call, @NonNull Throwable t) {
                // Gestion des erreurs de connexion ici
                t.printStackTrace();
            }
        });

    }
}
