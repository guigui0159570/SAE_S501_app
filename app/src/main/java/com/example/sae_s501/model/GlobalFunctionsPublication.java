package com.example.sae_s501.model;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sae_s501.R;
import com.example.sae_s501.model.User.AvisDTO;
import com.example.sae_s501.model.User.Publication;
import com.example.sae_s501.model.User.Utilisateur;
import com.example.sae_s501.retrofit.FilActuService;

import java.io.InputStream;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GlobalFunctionsPublication {


    public static void callAvis(FilActuService filActuService, Publication p, LinearLayout layoutConteneur){
        /*
            Cette fonction permet de faire un appel à la BDD en passant par une requête
            HTTP pour récupérer les avis d'une publication en fonction de son ID
        */
        Call<List<AvisDTO>> avisDTOCall = filActuService.getAllAvisByPublication(p.getId());
        avisDTOCall.enqueue(new Callback<List<AvisDTO>>() {
            @SuppressLint("RtlHardcoded")
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onResponse(@NonNull Call<List<AvisDTO>> call, @NonNull Response<List<AvisDTO>> response) {
                if(response.isSuccessful()){
                    List<AvisDTO> les_avis = response.body();
                    RatingBar new_rating_bar = new RatingBar(layoutConteneur.getContext());
                    int widthInPixels = 850;
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthInPixels, LinearLayout.LayoutParams.WRAP_CONTENT);
                    assert les_avis != null;
                    if(les_avis.size() != 0){
                        int note = 0;
                        int nb = 0;
                        for(AvisDTO avisDTO : les_avis){
                            note += avisDTO.getEtoile();
                            nb += 1;
                        }

                        new_rating_bar.setNumStars(5);
                        new_rating_bar.setStepSize(1);
                        new_rating_bar.setScaleX(0.75f);
                        new_rating_bar.setScaleY(0.75f);
                        float roundedRating = Math.round((float) note/nb);
                        new_rating_bar.setRating(roundedRating);
                        new_rating_bar.setIsIndicator(true);
                        layoutConteneur.addView(new_rating_bar, layoutParams);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AvisDTO>> call, @NonNull Throwable t) {

            }
        });
    }

    public static void callImage(FilActuService filActuService, Publication p, ImageView img_produit){
        /*
            Cette fonction permet de faire un appel à la BDD en passant par une requête
            HTTP pour récupérer l'image d'une publication en fonction de son ID
        */
        Call<ResponseBody> callImage = filActuService.getImage(p.getImage());
        callImage.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (img_produit.getContext() != null){
                    if (response.isSuccessful()) {
                        ResponseBody body = response.body();
                        Log.d("IMAGE", String.valueOf(body));
                        if (body != null) {
                            InputStream inputStream = body.byteStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            int desiredWidth = 400;
                            int desiredHeight = 400;
                            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, false);
                            Drawable drawable = new BitmapDrawable(img_produit.getResources(), resizedBitmap);
                            img_produit.setImageDrawable(drawable);

                        }
                    } else {
                        Log.e("IMAGE", "Erreur lors de la récupération de l'image. Code de réponse : " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                // Gestion des erreurs
                Log.e("IMAGE", "Échec de la requête pour récupérer l'image : " + t.getMessage());
            }
        });
    }

    //Partie page produit
    public static void callAvisProd(FilActuService filActuService, Long publicationId, View view, Boolean gratuit){
        /*
            Cette fonction permet de faire un appel à la BDD en passant par une requête
            HTTP pour récupérer les avis d'une publication en fonction de son ID pour les
            produits payant/gratuit
        */
        Call<List<AvisDTO>> callAvis = filActuService.getAllAvisByPublication(publicationId);
        callAvis.enqueue(new Callback<List<AvisDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<AvisDTO>> call, @NonNull Response<List<AvisDTO>> response) {
                Log.d("CallAvis", "codeResponse: "+response.code());
                LinearLayout commentaires = null;
                if (response.isSuccessful()){
                    Log.d("CallAvis", "dans le call des avis : "+response.body());
                    List<AvisDTO> les_avis = response.body();
                    Log.d("AVIS", "Nombre d'avis récupérés : " + (les_avis != null ? les_avis.size() : 0));

                    if (!gratuit) {
                        commentaires = view.findViewById(R.id.layout_to_commentaire_payant);
                        commentaires.setOrientation(LinearLayout.VERTICAL);
                    } else {
                        commentaires = view.findViewById(R.id.layout_to_commentaire_gratuit);
                        commentaires.setOrientation(LinearLayout.VERTICAL);
                    }

                    assert les_avis != null;
                    if(les_avis.size() != 0){
                        for (AvisDTO avis : les_avis){
                            LinearLayout linearLayout = new LinearLayout(view.getContext());
                            LinearLayout.LayoutParams params_elt = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            params_elt.setMargins(0, 0, 0, 25);
                            linearLayout.setLayoutParams(params_elt);
                            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                            TextView pseudo_avis = new TextView(view.getContext());
                            TextView commentaire = new TextView(view.getContext());

                            Call<Utilisateur> utilisateurCall = filActuService.getUtilisateurById(avis.getUtilisateur());
                            LinearLayout finalCommentaires = commentaires;
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
                                        finalCommentaires.addView(linearLayout);

                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Utilisateur> call, @NonNull Throwable t) {

                                }
                            });
                        }
                    }else{
                        TextView textView = new TextView(view.getContext());
                        textView.setText("Cette publication ne possède pas d'avis...");
                        textView.setTextColor(Color.parseColor("#FFA500"));
                        textView.setTextSize(18);
                        commentaires.addView(textView);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AvisDTO>> call, @NonNull Throwable t) {
                Toast.makeText(view.getContext(), "pas d'avis récupérés !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void callImageProd(FilActuService filActuService, Publication publication, View view){
        /*
            Cette fonction permet de faire un appel à la BDD en passant par une requête
            HTTP pour récupérer l'image d'une publication en fonction de son ID pour les
            produits payant/gratuit
        */
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
                        Drawable drawable = new BitmapDrawable(view.getResources(), resizedBitmap);
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

    public static void callUserIdProd(FilActuService filActuService, String jwtEmail, Button ajout_commentaire,
                                      EditText commentaire, RatingBar etoiles, Long publicationId, AppCompatActivity appCompatActivity){
        /*
            Cette fonction permet de faire un appel à la BDD en passant par une requête
            HTTP pour récupérer l'ID d'un utilisateur en fonction de son adresse mail
        */
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
                                            Toast.makeText(view1.getContext(), "Commentaire ajouté", Toast.LENGTH_SHORT).show();
                                            etoiles.setRating(0);
                                            commentaire.setText("");
                                            appCompatActivity.recreate();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

                                    }
                                });
                                commentaire.setText("");
                                etoiles.setRating(0);
                                appCompatActivity.recreate();
                            }else{
                                Toast.makeText(appCompatActivity.getApplicationContext(), "Commentaire manquant", Toast.LENGTH_SHORT).show();
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

    //Partie page produit de "mes publications"*
    public static void callAvisProdMesPub(FilActuService filActuService, Long publicationId,
                                          View view, RatingBar etoiles, Boolean gratuit){
        /*
            Cette fonction permet de faire un appel à la BDD en passant par une requête
            HTTP pour récupérer les avis d'une publication en fonction de son ID pour les
            mes produits payant/gratuit
        */
        Call<List<AvisDTO>> callAvis = filActuService.getAllAvisByPublication(publicationId);
        callAvis.enqueue(new Callback<List<AvisDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<AvisDTO>> call, @NonNull Response<List<AvisDTO>> response) {
                Log.d("CallAvis", "codeResponse: "+response.code());
                LinearLayout commentaires = null;
                if (response.isSuccessful()){
                    Log.d("CallAvis", "dans le call des avis : "+response.body());
                    List<AvisDTO> les_avis = response.body();
                    if(gratuit){
                        commentaires = view.findViewById(R.id.layout_to_commentaire_gratuit);
                        commentaires.setOrientation(LinearLayout.VERTICAL);
                    }else{
                        commentaires = view.findViewById(R.id.layout_to_commentaire_payant);
                        commentaires.setOrientation(LinearLayout.VERTICAL);
                    }
                    assert les_avis != null;
                    if(les_avis.size() != 0){
                        int note = 0;
                        int nb = 0;
                        for (AvisDTO avis : les_avis){
                            note += avis.getEtoile();
                            nb += 1;
                            LinearLayout linearLayout = new LinearLayout(view.getContext());
                            LinearLayout.LayoutParams params_elt = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            params_elt.setMargins(0, 0, 0, 25);
                            linearLayout.setLayoutParams(params_elt);
                            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                            TextView pseudo_avis = new TextView(view.getContext());
                            TextView commentaire = new TextView(view.getContext());

                            Call<Utilisateur> utilisateurCall = filActuService.getUtilisateurById(avis.getUtilisateur());
                            LinearLayout finalCommentaires = commentaires;
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
                                        finalCommentaires.addView(linearLayout);
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
                    }else{
                        TextView textView = new TextView(view.getContext());
                        textView.setText("Cette publication ne possède pas d'avis...");
                        textView.setTextColor(Color.parseColor("#FFA500"));
                        textView.setTextSize(18);
                        commentaires.addView(textView);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AvisDTO>> call, @NonNull Throwable t) {
                Toast.makeText(view.getContext(), "pas d'avis récupérés !", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
