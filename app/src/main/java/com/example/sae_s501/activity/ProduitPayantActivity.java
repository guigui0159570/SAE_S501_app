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

import com.example.sae_s501.model.Dictionnaire;
import com.example.sae_s501.model.GlobalFunctionsPublication;
import com.example.sae_s501.model.User.Publication;
import com.example.sae_s501.R;
import com.example.sae_s501.authentification.Authentification;
import com.example.sae_s501.retrofit.FilActuService;
import com.example.sae_s501.retrofit.PanierService;
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.SessionManager;
import com.example.sae_s501.retrofit.UserService;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProduitPayantActivity extends AppCompatActivity {
    private TextView panier;
    private RetrofitService retrofitService;
    private PanierService panierService;
    private ImageView retour;
    private ImageView signaler;
    private UserService userService;
    private long id;
    private String jwtEmail;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.produit_payantresp);
        panier = findViewById(R.id.ajouter_panier);
        signaler = findViewById(R.id.signaler);
        retrofitService = new RetrofitService(this);
        jwtEmail = SessionManager.getUserEmail(this);
        retour = findViewById(R.id.close);

        long publicationId = getIntent().getLongExtra("id", 0);
        Log.d("publicationId", String.valueOf(publicationId));
        View rootView = findViewById(android.R.id.content);
        if(rootView != null){
            loadPublication(rootView, publicationId);
        }else{
            Toast.makeText(getApplicationContext(), "La view est null !!!", Toast.LENGTH_SHORT).show();
        }
        signaler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignalerConfirmation(jwtEmail,id);
            }
        });
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProduitPayantActivity.this, FilActuActivity.class);
                startActivity(intent);
            }
        });
        panier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    ajoutPanier(jwtEmail,publicationId);
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
    private void ajoutPanier(String email, Long idPub) {
        retrofitService = new RetrofitService(this);
        panierService = retrofitService.getRetrofit().create(PanierService.class);

        Call<Void> call = panierService.ajoutPublicationPanier(email, idPub);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showToast("Ce produit a été ajouté au panier");
                } else {
                    if (response.code() == 404) {
                        showToast("Utilisateur non trouvé");
                    } else if (response.code() == 400) {
                        showToast("La publication est déjà dans le panier ou vous êtes le propriétaire");
                    } else {
                        showToast("Erreur de requête: " + response.code());
                        Log.d("PANIER", String.valueOf(response.code()));
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showToast("Erreur de réseau: " + t.getMessage());
                Log.d("PANIER", t.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void loadPublication(View view, long publicationId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Dictionnaire.getIpAddress())
                .addConverterFactory(GsonConverterFactory.create())
                .client(Authentification.createAuthenticatedClient(ProduitPayantActivity.this.getApplicationContext()))
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
                        TextView titre = view.findViewById(R.id.ajout_pub_titre_payant);titre.setText(publication.getTitre());
                        TextView description = view.findViewById(R.id.description_payant); description.setText(publication.getDescription());
                        TextView pseudo = view.findViewById(R.id.pseudo_pub_payant);
                        id =  publication.getId();

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
                                Intent intent = new Intent(getApplicationContext(), MyCompteActivity.class);
                                startActivity(intent);
                            }

                        }else{
                            pseudo.setText("Propriétaire non répertorié...");
                        }
                        Log.d("Id pub", ""+publicationId);
                        GlobalFunctionsPublication.callAvisProd(filActuService,publicationId,view,false);
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
        EditText commentaire = view.findViewById(R.id.editText_commentaire_payant);
        RatingBar etoiles = view.findViewById(R.id.notation_payant);
        Button ajout_commentaire = view.findViewById(R.id.button_commentaire_payant);
        Log.d(TAG, "loadPublication: fin recuperation des objets");

        String jwtEmail = SessionManager.getUserEmail(ProduitPayantActivity.this.getApplicationContext());

        GlobalFunctionsPublication.callUserIdProd(filActuService,jwtEmail,ajout_commentaire,commentaire,
                etoiles,publicationId,this);
    }
}