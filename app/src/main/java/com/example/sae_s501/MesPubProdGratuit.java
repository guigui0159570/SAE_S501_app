package com.example.sae_s501;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
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

public class MesPubProdGratuit extends AppCompatActivity {
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
                Intent intent = new Intent(MesPubProdGratuit.this, MesPublications.class);
                startActivity(intent);
            }
        });
    }

    private void loadPublication(View view, long publicationId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Dictionnaire.getIpAddress())
                .addConverterFactory(GsonConverterFactory.create())
                .client(Authentification.createAuthenticatedClient(MesPubProdGratuit.this.getApplicationContext()))
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
                                        int note = 0;
                                        int nb = 0;
                                        for (AvisDTO avis : les_avis){
                                            note += avis.getEtoile();
                                            nb += 1;
                                            LinearLayout linearLayout = new LinearLayout(MesPubProdGratuit.this.getApplicationContext());
                                            LinearLayout.LayoutParams params_elt = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                            );
                                            params_elt.setMargins(0, 0, 0, 25);
                                            linearLayout.setLayoutParams(params_elt);
                                            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                                            TextView pseudo_avis = new TextView(MesPubProdGratuit.this.getApplicationContext());
                                            TextView commentaire = new TextView(MesPubProdGratuit.this.getApplicationContext());

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
                                        Log.d("Notation", "notation : " + note/nb);
                                        etoiles.setRating(Math.round(note/nb));
                                        etoiles.setIsIndicator(true);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<List<AvisDTO>> call, @NonNull Throwable t) {
                                Toast.makeText(MesPubProdGratuit.this.getApplicationContext(), "pas d'avis récupérés !", Toast.LENGTH_SHORT).show();
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
                                        Drawable drawable = new BitmapDrawable(MesPubProdGratuit.this.getResources(), resizedBitmap);
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

    }
}
