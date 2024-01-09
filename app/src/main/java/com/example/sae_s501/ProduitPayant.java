package com.example.sae_s501;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.proto.ProtoOutputStream;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sae_s501.authentification.Authentification;
import com.example.sae_s501.retrofit.FilActuService;
import com.example.sae_s501.retrofit.PanierService;
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.SessionManager;
import com.example.sae_s501.retrofit.UserService;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProduitPayant extends AppCompatActivity {
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
                Intent intent = new Intent(ProduitPayant.this, FilActu.class);
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
                .client(Authentification.createAuthenticatedClient(ProduitPayant.this.getApplicationContext()))
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
                                        Intent intent = new Intent(getApplicationContext(), CompteUtilisateur.class);
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
                        Call<List<AvisDTO>> callAvis = filActuService.getAllAvisByPublication(publicationId);
                        callAvis.enqueue(new Callback<List<AvisDTO>>() {
                            @Override
                            public void onResponse(@NonNull Call<List<AvisDTO>> call, @NonNull Response<List<AvisDTO>> response) {
                                Log.d("CallAvis", "codeResponse: "+response.code());
                                if (response.isSuccessful()){
                                    Log.d("CallAvis", "dans le call des avis : "+response.body());
                                    List<AvisDTO> les_avis = response.body();
                                    LinearLayout commentaires = view.findViewById(R.id.layout_to_commentaire_payant);
                                    commentaires.setOrientation(LinearLayout.VERTICAL);
                                    if(les_avis != null){
                                        for (AvisDTO avis : les_avis){
                                            LinearLayout linearLayout = new LinearLayout(ProduitPayant.this.getApplicationContext());
                                            LinearLayout.LayoutParams params_elt = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                            );
                                            params_elt.setMargins(0, 0, 0, 25);
                                            linearLayout.setLayoutParams(params_elt);
                                            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                                            TextView pseudo_avis = new TextView(ProduitPayant.this.getApplicationContext());
                                            TextView commentaire = new TextView(ProduitPayant.this.getApplicationContext());

                                            Call<Utilisateur> utilisateurCall = filActuService.getUtilisateurById(avis.getUtilisateur());
                                            utilisateurCall.enqueue(new Callback<Utilisateur>() {
                                                @SuppressLint("SetTextI18n")
                                                @Override
                                                public void onResponse(@NonNull Call<Utilisateur> call, @NonNull Response<Utilisateur> response) {
                                                    if(response.isSuccessful()){
                                                        Utilisateur utilisateur = response.body();
                                                        assert utilisateur != null;
                                                        pseudo_avis.setText(utilisateur.getPseudo()+" : ");
                                                        commentaire.setText(avis.getCommentaire());
                                                        linearLayout.addView(pseudo_avis);linearLayout.addView(commentaire);
                                                        commentaires.addView(linearLayout);
                                                    }
                                                }

                                                @Override
                                                public void onFailure(@NonNull Call<Utilisateur> call, @NonNull Throwable t) {
                                                    Toast.makeText(ProduitPayant.this.getApplicationContext(), "onFailure", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<List<AvisDTO>> call, @NonNull Throwable t) {
                                Toast.makeText(ProduitPayant.this.getApplicationContext(), "pas d'avis récupérés !", Toast.LENGTH_SHORT).show();
                                Log.e("DEBUG", "Failed to get comments: " + t.getMessage());
                            }
                        });

                        Call<ResponseBody> callImage = filActuService.getImage(publication.getImage());
                        Log.d("IMAGE", publication.getImage());

                        ImageView img_produit = view.findViewById(R.id.imageViewPub);

                        //Call pour l'ajout de l'image produit
                        callImage.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    ResponseBody body = response.body();
                                    Log.d("IMAGE", String.valueOf(body));
                                    if (body != null) {
                                        InputStream inputStream = body.byteStream();
                                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                        int desiredWidth = img_produit.getWidth();
                                        int desiredHeight = img_produit.getHeight();
                                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, false);
                                        Drawable drawable = new BitmapDrawable(ProduitPayant.this.getResources(), resizedBitmap);
                                        img_produit.setImageDrawable(drawable);
                                    }
                                } else {
                                    Log.e("IMAGE", "Erreur lors de la récupération de l'image. Code de réponse : " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                                // Gestion des erreurs
                                Log.e("IMAGE", "Échec de la requête pour récupérer l'image : " + t.getMessage());
                            }
                        });
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

        String jwtEmail = SessionManager.getUserEmail(ProduitPayant.this.getApplicationContext());

        Call<Long> callUserId = filActuService.getUtilisateurIdByEmail(jwtEmail);
        callUserId.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(@NonNull Call<Long> call, @NonNull Response<Long> response) {
                if (response.isSuccessful()) {

                    Log.d(TAG, "user id : "+response.body());
                    Long userId = response.body();
                    if (userId != null) {
                        ajout_commentaire.setOnClickListener(view1 -> {
                            if(commentaire.getText() != null){
                                FilActuService.AvisRequestBody requestBody = new FilActuService.AvisRequestBody(
                                        commentaire.getText().toString(),
                                        etoiles.getNumStars(),
                                        publicationId,
                                        userId
                                );
                                Log.d("RequestBody", ""+requestBody.getCommentaire());
                                Log.d("RequestBody", ""+requestBody.getEtoile());
                                Log.d("RequestBody", ""+requestBody.getPublication());
                                Log.d("RequestBody", ""+requestBody.getUtilisateur());
                                Call<Void> voidCall = filActuService.saveAvis(commentaire.getText().toString(), (int) etoiles.getRating(), publicationId, userId);
                                voidCall.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                        Log.d(TAG, "onResponse: "+response.code());
                                        if (response.isSuccessful()){
                                            Toast.makeText(ProduitPayant.this.getApplicationContext(), "Commentaire ajouté", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

                                    }
                                });
                                commentaire.setText("");
                                etoiles.setRating(0);
                                recreate();
                            }else{
                                Toast.makeText(ProduitPayant.this.getApplicationContext(), "Commentaire manquant", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        });
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<Long> call, @NonNull Throwable t) {

            }
        });
    }
}