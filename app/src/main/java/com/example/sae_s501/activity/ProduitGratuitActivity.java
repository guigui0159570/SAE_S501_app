package com.example.sae_s501.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sae_s501.Dictionnaire;
import com.example.sae_s501.GlobalFunctionsPublication;
import com.example.sae_s501.Publication;
import com.example.sae_s501.R;
import com.example.sae_s501.authentification.Authentification;
import com.example.sae_s501.retrofit.FilActuService;
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.SessionManager;
import com.example.sae_s501.retrofit.UserService;
import com.example.sae_s501.visualisation.Visualiser;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProduitGratuitActivity extends AppCompatActivity {

    private ImageView retour;
    private ImageView signaler;
    private UserService userService;
    private RetrofitService retrofitService;

    private long id;
    private String jwtEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.produit_gratuitresp);
        long publicationId = getIntent().getLongExtra("id", 0);
        View rootView = findViewById(android.R.id.content);
        loadPublication(rootView, publicationId);
        retour = findViewById(R.id.close);
        retrofitService = new RetrofitService(this);
        signaler = findViewById(R.id.signaler);


        jwtEmail = SessionManager.getUserEmail(this);

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProduitGratuitActivity.this, FilActuActivity.class);
                startActivity(intent);
            }
        });

        signaler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignalerConfirmation(jwtEmail,id);
            }
        });
    }


    private void SignalerConfirmation(String jwt,Long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation")
                .setMessage("Voulez-vous vraiment signaler cette publication ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        userService = retrofitService.getRetrofit().create(UserService.class);

                        Call<Void> aideMail = userService.signalement(jwt,id);

                        aideMail.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    showToast("Cette publication a été signalé auprès d'un admin.");
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
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void loadPublication(View view, long publicationId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Dictionnaire.getIpAddress())
                .addConverterFactory(GsonConverterFactory.create())
                .client(Authentification.createAuthenticatedClient(ProduitGratuitActivity.this.getApplicationContext()))
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
                        id =  publication.getId();

                        View visulaiser = findViewById(R.id.visualiser_img);
                        visulaiser.setOnClickListener(view -> {
                            loadView(view, publication.getFichier(), new Intent(ProduitGratuitActivity.this, Visualiser.class));
                        });

                        if(publication.getProprietaire() != null){
                            pseudo.setText(publication.getProprietaire().getPseudo());
                            if(!Objects.equals(publication.getProprietaire().getEmail(), jwtEmail)) {
                                pseudo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(), CompteUtilisateurActivity.class);
                                        intent.putExtra("userId", publication.getProprietaire().getId());
                                        startActivity(intent);
                                    }
                                });
                            }
                            else {
                                pseudo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(), MyCompteActivity.class);
                                        startActivity(intent);
                                    }
                                });

                            }
                        }else{
                            pseudo.setText("Propriétaire non répertorié...");
                        }
                        Log.d("Id pub", ""+publicationId);
                        //Récupération des avis
                        GlobalFunctionsPublication.callAvisProd(filActuService,publicationId,view,true);
                        //Récupération de l'image
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

        Log.d(TAG, "loadPublication: debut recuperation des objets");
        EditText commentaire = view.findViewById(R.id.editTextCommentaire);
        RatingBar etoiles = view.findViewById(R.id.notation_gratuit);
        Button ajout_commentaire = view.findViewById(R.id.button2);
        Log.d(TAG, "loadPublication: fin recuperation des objets");

        String jwtEmail = SessionManager.getUserEmail(ProduitGratuitActivity.this.getApplicationContext());

        GlobalFunctionsPublication.callUserIdProd(filActuService,jwtEmail,ajout_commentaire,
                commentaire,etoiles,publicationId,this);
    }
    public static void loadView(View view, String fichier, Intent intent){
        Log.d("Fichier", ""+fichier);
        intent.putExtra("fichier", fichier);
        view.getContext().startActivity(intent);
    }
}