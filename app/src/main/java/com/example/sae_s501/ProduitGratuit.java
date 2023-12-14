package com.example.sae_s501;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sae_s501.authentification.Authentification;
import com.example.sae_s501.retrofit.FilActuService;

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
        View rootView = findViewById(android.R.id.content); // Use the root view of the layout
        loadPublication(rootView, publicationId);
    }

    private void loadPublication(View view, long publicationId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Dictionnaire.getIpAddress())
                .addConverterFactory(GsonConverterFactory.create())
                .client(Authentification.createAuthenticatedClient(getApplicationContext()))
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
                                    LinearLayout linearLayout = new LinearLayout(getApplicationContext());
                                    if(les_avis != null){
                                        for (AvisDTO avis : les_avis){
                                            Log.d("LogAvis", "avis utilisateur : "+avis.getUtilisateur());
                                            Log.d("LogAvis", "avis commentaire : "+avis.getCommentaire());
                                            Log.d(TAG, "onResponse: "+avis.getId());
                                            Log.d("LogAvis", "avis etoiles : "+avis.getEtoile());
                                            Log.d("LogAvis", "avis publication : "+avis.getPublication());

                                            TextView pseudo_avis = new TextView(getApplicationContext());
                                            TextView commentaire = new TextView(getApplicationContext());

                                            Call<Utilisateur> utilisateurCall = filActuService.getUtilisateurById(avis.getUtilisateur());
                                            utilisateurCall.enqueue(new Callback<Utilisateur>() {
                                                @Override
                                                public void onResponse(@NonNull Call<Utilisateur> call, @NonNull Response<Utilisateur> response) {
                                                    if(response.isSuccessful()){
                                                        Utilisateur utilisateur = response.body();
                                                        assert utilisateur != null;
                                                        pseudo_avis.setText(utilisateur.getPseudo());
                                                    }
                                                }

                                                @Override
                                                public void onFailure(@NonNull Call<Utilisateur> call, @NonNull Throwable t) {

                                                }
                                            });
                                            commentaire.setText(avis.getCommentaire());
                                            linearLayout.addView(pseudo_avis);linearLayout.addView(commentaire);
                                            commentaires.addView(linearLayout);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<List<AvisDTO>> call, @NonNull Throwable t) {
                                Toast.makeText(getApplicationContext(), "pas d'avis récupérés !", Toast.LENGTH_SHORT).show();
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
                                        Drawable drawable = new BitmapDrawable(getResources(), resizedBitmap);
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
