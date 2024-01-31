package com.example.sae_s501;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.sae_s501.activity.ProduitGratuitActivity;
import com.example.sae_s501.activity.ProduitPayantActivity;
import com.example.sae_s501.authentification.Authentification;
import com.example.sae_s501.model.Utilisateur;
import com.example.sae_s501.retrofit.FilActuService;


import java.io.InputStream;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FilActuFragment extends Fragment {

    //Définitions des paramètres
    private static final String TAG = "FilActuFragment";
    private static final String BASE_URL = Dictionnaire.getIpAddress();



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateFragment : creation du fragment");
        View view = inflater.inflate(R.layout.fragment_fil_actu, container, false);
        Log.d(TAG, "onCreateFragment : attachement de fragment_fil_actu");
        loadData(view);
        Log.d(TAG, "onCreateFragment : loaded datas");
        return view;

    }

    private void loadData(View view) {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(Authentification.createAuthenticatedClient(getActivity()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FilActuService filActuService = retrofit.create(FilActuService.class);

        Call<List<Publication>> call = filActuService.getAllPublication();
        call.enqueue(new Callback<List<Publication>>() {

            @Override
            public void onResponse(@NonNull Call<List<Publication>> call, @NonNull Response<List<Publication>> response) {
                if (response.isSuccessful()) {
                    LinearLayout layout = view.findViewById(R.id.container_pub_fil_actu);
                    List<Publication> publications = response.body();
                    if (publications != null) {
                        layout.removeAllViews();

                        for (Publication p : publications) {
                            //Layout qui va contenir les autres layout
                            LinearLayout layoutConteneur = new LinearLayout(getContext());
                            //Layout qui contient l'image du produit ainsi le titre et la description
                            LinearLayout layoutProduit = new LinearLayout(getContext());
                            //Layout qui contient le titre et la description
                            LinearLayout layoutTitreDes = new LinearLayout(getContext());
                            //Layout qui contient la partie personne ainsi que les étoiles de notation
                            LinearLayout layoutPersonnel = new LinearLayout(getContext());

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            layoutParams.setMargins(50,10,50,20);

                            //Params pour mettre un margin bas de l'élément
                            LinearLayout.LayoutParams params_elt = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            params_elt.setMargins(0, 0, 0, 25);

                            layoutConteneur.setLayoutParams(layoutParams);
                            layoutProduit.setLayoutParams(layoutParams);
                            layoutTitreDes.setLayoutParams(layoutParams);
                            layoutPersonnel.setLayoutParams(layoutParams);

                            //Param layoutConteneur
                            layoutConteneur.setId(p.getId().intValue());
                            layoutConteneur.setOrientation(LinearLayout.VERTICAL);
                            layoutConteneur.setVisibility(View.VISIBLE);


                            if(p.getGratuit()){
                                layoutConteneur.setOnClickListener(view -> {
                                    loadView(view, layoutConteneur.getId(), new Intent(requireContext(), ProduitGratuitActivity.class));
                                });
                            }else {
                                layoutConteneur.setOnClickListener(view -> {
                                    loadView(view, layoutConteneur.getId(), new Intent(requireContext(), ProduitPayantActivity.class));
                                });
                            }



                            //Param layoutProduit
                            layoutProduit.setOrientation(LinearLayout.HORIZONTAL);
                            layoutProduit.setId(View.generateViewId());

                            //Param layoutTitreDes
                            layoutTitreDes.setOrientation(LinearLayout.VERTICAL);
                            layoutTitreDes.setId(View.generateViewId());
                            //mettre l'element image produit
                            ImageView img_produit = new ImageView(getContext());

                            Call<ResponseBody> callImage = filActuService.getImage(p.getImage());
                            Log.d("IMAGE", p.getImage());

                            callImage.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        ResponseBody body = response.body();
                                        Log.d("IMAGE", String.valueOf(body));
                                        if (body != null) {
                                            InputStream inputStream = body.byteStream();
                                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                            int desiredWidth = 400;
                                            int desiredHeight = 400;
                                            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, false);
                                            Drawable drawable = new BitmapDrawable(getResources(), resizedBitmap);
                                            img_produit.setImageDrawable(drawable);

                                        }
                                    } else {
                                        Log.e("IMAGE", "Erreur lors de la récupération de l'image. Code de réponse : " + response.code());
                                    }
                                }
                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    // Gestion des erreurs
                                    Log.e("IMAGE", "Échec de la requête pour récupérer l'image : " + t.getMessage());
                                }
                            });
                            layoutProduit.addView(img_produit);
                            layoutProduit.addView(layoutTitreDes);


                            int nbTelechargement = p.getNb_telechargement();
                            TextView textnbTelechargement = new TextView(requireContext());
                            textnbTelechargement.setId(View.generateViewId());
                            textnbTelechargement.setText("Téléchargement : " + nbTelechargement+ "   ");
                            textnbTelechargement.setLayoutParams(params_elt);

                            String titre = p.getTitre();
                            TextView titreText = new TextView(getContext());
                            titreText.setId(View.generateViewId());titreText.setText(titre);
                            titreText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                            titreText.setTextColor(Color.parseColor("#00BA8D"));
                            titreText.setLayoutParams(params_elt);

                            String description = p.getDescription();
                            TextView desText = new TextView(getContext());
                            desText.setId(View.generateViewId());
                            desText.setText(description);

                            layoutTitreDes.addView(titreText);
                            layoutTitreDes.addView(desText);

                            Call<List<AvisDTO>> avisDTOCall = filActuService.getAllAvisByPublication(p.getId());
                            avisDTOCall.enqueue(new Callback<List<AvisDTO>>() {
                                @SuppressLint("RtlHardcoded")
                                @RequiresApi(api = Build.VERSION_CODES.Q)
                                @Override
                                public void onResponse(@NonNull Call<List<AvisDTO>> call, @NonNull Response<List<AvisDTO>> response) {
                                    if(response.isSuccessful()){
                                        List<AvisDTO> les_avis = response.body();
                                        RatingBar new_rating_bar = new RatingBar(FilActuFragment.this.requireContext());
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
                                            layoutParams.gravity = Gravity.RIGHT;
                                            layoutConteneur.addView(new_rating_bar, layoutParams);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<List<AvisDTO>> call, @NonNull Throwable t) {

                                }
                            });

                            Boolean gratuit = p.getGratuit();
                            String prix = String.valueOf(p.getPrix());
                            TextView prixText = new TextView(getContext());
                            prixText.setId(View.generateViewId());
                            if(gratuit) {
                                prixText.setText("    Gratuit      ");

                            }
                            else{
                                prixText.setText("    Prix : " + prix+"      ");

                            }
                            prixText.setLayoutParams(params_elt);

                            //Param layoutPersonnel
                            layoutPersonnel.setOrientation(LinearLayout.HORIZONTAL);
                            layoutPersonnel.setGravity(LinearLayout.TEXT_ALIGNMENT_TEXT_START);
                            layoutPersonnel.setId(View.generateViewId());


                            // Accéder aux valeurs de l'objet
                            //mettre image utilisateur
                            if(p.getProprietaire() != null){
                                Utilisateur pseudo = p.getProprietaire();
                                TextView pseudoText = new TextView(getContext());
                                pseudoText.setId(View.generateViewId());
                                pseudoText.setText(pseudo.getPseudo());
                                pseudoText.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue));
                                layoutPersonnel.addView(pseudoText);
                                layoutPersonnel.addView(prixText);
                                layoutPersonnel.addView(textnbTelechargement);

                            }

                            //Ajout des layout
                            layoutConteneur.addView(layoutProduit); layoutConteneur.addView(layoutPersonnel);

                            //Mise en place de bordures
                            GradientDrawable border = new GradientDrawable();
                            border.setColor(Color.TRANSPARENT);
                            border.setCornerRadius(20f);
                            layoutConteneur.setBackground(border);layoutConteneur.setBackgroundResource(R.color.white);
                            layout.addView(layoutConteneur);
                        }
                    } else {
                        Log.e(TAG, "Error response: " + response.errorBody());
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Publication>> call, @NonNull Throwable t) {
                Log.e(TAG, "Failure: " + t.getMessage());
                Toast.makeText(getContext(), "Erreur lors de la communication avec le serveur", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void loadView(View view, long id, Intent intent){
        Log.d("ID de la pub", ""+id);
        intent.putExtra("id", id);
        view.getContext().startActivity(intent);
    }
}
