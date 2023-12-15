package com.example.sae_s501;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
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
import com.example.sae_s501.retrofit.SessionManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProduitGratuit extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.produit_gratuitresp);
        long publicationId = getIntent().getLongExtra("id", 0);
        View rootView = findViewById(android.R.id.content);
        loadPublication(rootView, publicationId);
    }

    private void loadPublication(View view, long publicationId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Dictionnaire.getIpAddress())
                .addConverterFactory(GsonConverterFactory.create())
                .client(Authentification.createAuthenticatedClient(ProduitGratuit.this.getApplicationContext()))
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

                        if(publication.getProprietaire() != null){
                            pseudo.setText(publication.getProprietaire().getPseudo());
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
                                    LinearLayout commentaires = view.findViewById(R.id.layout_to_commentaire_gratuit);
                                    commentaires.setOrientation(LinearLayout.VERTICAL);
                                    if(les_avis != null){
                                        for (AvisDTO avis : les_avis){
                                            LinearLayout linearLayout = new LinearLayout(ProduitGratuit.this.getApplicationContext());
                                            LinearLayout.LayoutParams params_elt = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                            );
                                            params_elt.setMargins(0, 0, 0, 25);
                                            linearLayout.setLayoutParams(params_elt);
                                            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                                            TextView pseudo_avis = new TextView(ProduitGratuit.this.getApplicationContext());
                                            TextView commentaire = new TextView(ProduitGratuit.this.getApplicationContext());

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

                                                }
                                            });
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<List<AvisDTO>> call, @NonNull Throwable t) {
                                Toast.makeText(ProduitGratuit.this.getApplicationContext(), "pas d'avis récupérés !", Toast.LENGTH_SHORT).show();
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
                                        Drawable drawable = new BitmapDrawable(ProduitGratuit.this.getResources(), resizedBitmap);
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
        EditText commentaire = view.findViewById(R.id.editTextCommentaire);
        RatingBar etoiles = view.findViewById(R.id.notation_gratuit);
        Button ajout_commentaire = view.findViewById(R.id.button2);
        Log.d(TAG, "loadPublication: fin recuperation des objets");

        String jwtEmail = SessionManager.getUserEmail(ProduitGratuit.this.getApplicationContext());

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
                                Call<Void> voidCall = filActuService.saveAvis(commentaire.getText().toString(), etoiles.getNumStars(), publicationId, userId);
                                voidCall.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                        Log.d(TAG, "onResponse: "+response.code());
                                        if (response.isSuccessful()){
                                            Toast.makeText(ProduitGratuit.this.getApplicationContext(), "Commentaire ajouté", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

                                    }
                                });
                                commentaire.setText("");
                                etoiles.setRating(0);
                            }else{
                                Toast.makeText(ProduitGratuit.this.getApplicationContext(), "Commentaire manquant", Toast.LENGTH_SHORT).show();
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