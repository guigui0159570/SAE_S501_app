package com.example.sae_s501;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.sae_s501.authentification.Authentification;
import com.example.sae_s501.model.Utilisateur;
import com.example.sae_s501.retrofit.FilActuService;
import com.example.sae_s501.retrofit.SessionManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MesPublicationsFrag extends Fragment {

    private static final String TAG = "MesPublicationsFrag";
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

    // Ajoutez cette méthode pour effectuer l'appel réseau depuis votre fragment
    private void loadData(View view) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(Authentification.createAuthenticatedClient(getActivity()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FilActuService filActuService = retrofit.create(FilActuService.class);

        String jwtEmail = SessionManager.getUserEmail(getActivity());


        Call<Long> callUserId = filActuService.getUtilisateurIdByEmail(jwtEmail);
        callUserId.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful()) {
                    Long userId = response.body();
                    if (userId != null) {
                        Call<List<Publication>> callPublications = filActuService.getPublicationByUtilisateurId(userId);
                        callPublications.enqueue(new Callback<List<Publication>>() {

                            @Override
                            public void onResponse(@NonNull Call<List<Publication>> call, @NonNull Response<List<Publication>> response) {
                                Log.d(TAG, "HTTP Code: " + response.code());
                                if (response.isSuccessful()) {
                                    LinearLayout layout = view.findViewById(R.id.container_pub_fil_actu);
                                    List<Publication> publications = response.body();
                                    if (publications != null) {
                                        layout.removeAllViews();

                                        for (Publication p : publications) {
                                            //Layout qui va contenir les autres layout
                                            LinearLayout layoutConteneur = new LinearLayout(requireContext());
                                            //Layout qui contient l'image du produit ainsi le titre et la description
                                            LinearLayout layoutProduit = new LinearLayout(requireContext());
                                            //Layout qui contient le titre et la description
                                            LinearLayout layoutTitreDes = new LinearLayout(requireContext());
                                            //Layout qui contient la partie personne ainsi que les étoiles de notation
                                            LinearLayout layoutPersonnel = new LinearLayout(requireContext());

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
                                            layoutConteneur.setId(View.generateViewId());
                                            layoutConteneur.setOrientation(LinearLayout.VERTICAL);
                                            layoutConteneur.setVisibility(View.VISIBLE);

                                            layoutConteneur.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent;
                                                    if(p.getGratuit()){
                                                        // ne fonctionne pas, à voir via le conteneur
                                                        //Toast.makeText(requireContext(), p.getGratuit().toString(), Toast.LENGTH_SHORT).show();
                                                        intent = new Intent(getActivity(), ProduitGratuit.class);
                                                    }else{
                                                        intent = new Intent(getActivity(), ProduitPayant.class);
                                                    }
                                                    intent.putExtra("id", layoutConteneur.getId());
                                                    startActivity(intent);
                                                }
                                            });


                                            //Param layoutProduit
                                            layoutProduit.setOrientation(LinearLayout.HORIZONTAL);
                                            layoutProduit.setGravity(LinearLayout.TEXT_ALIGNMENT_CENTER);
                                            layoutProduit.setId(View.generateViewId());

                                            //Param layoutTitreDes
                                            layoutTitreDes.setOrientation(LinearLayout.VERTICAL);
                                            layoutTitreDes.setGravity(LinearLayout.TEXT_ALIGNMENT_CENTER);
                                            layoutTitreDes.setId(View.generateViewId());
                                            //mettre l'element image produit
                                            ImageView img_produit = new ImageView(requireContext());

                                            Drawable drawable = ContextCompat.getDrawable(requireContext(),R.drawable.greatbritain);
                                            img_produit.setImageDrawable(drawable);
                                            layoutProduit.addView(img_produit);
                                            layoutProduit.addView(layoutTitreDes);


                                            String titre = p.getTitre();
                                            TextView titreText = new TextView(requireContext());
                                            titreText.setId(View.generateViewId());titreText.setText(titre);
                                            titreText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                                            titreText.setTextColor(Color.parseColor("#00BA8D"));
                                            titreText.setLayoutParams(params_elt);

                                            String description = p.getDescription();
                                            TextView desText = new TextView(requireContext());
                                            desText.setId(View.generateViewId());
                                            desText.setText(description);

                                            layoutTitreDes.addView(titreText);
                                            layoutTitreDes.addView(desText);

                                            Boolean gratuit = p.getGratuit();
                                            String prix = String.valueOf(p.getPrix());
                                            TextView prixText = new TextView(requireContext());
                                            prixText.setId(View.generateViewId());
                                            if(gratuit) {
                                                prixText.setText("    Gratuit");

                                            }
                                            else{
                                                prixText.setText("    Prix : " + prix);

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
                                                TextView pseudoText = new TextView(requireContext());
                                                pseudoText.setId(View.generateViewId());
                                                pseudoText.setText(pseudo.getPseudo());
                                                pseudoText.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue));
                                                layoutPersonnel.addView(pseudoText);
                                                layoutPersonnel.addView(prixText);

                                            }

                                            //Ajout de la notation
                                            Call<List<Avis>> callAvis = filActuService.getAllAvisByPublication(p.getId()) ;
                                            callAvis.enqueue(new Callback<List<Avis>>() {
                                                @Override
                                                public void onResponse(Call<List<Avis>> call, Response<List<Avis>> response) {
                                                    if(response.isSuccessful()){
                                                        List<Avis> avis = response.body();
                                                        int sum = 0;
                                                        assert avis != null;
                                                        for (Avis avis1 : avis){
                                                            sum += 1;
                                                        }
                                                        int notation_publication = Math.round(sum/avis.size());
                                                        Log.d(TAG, "NB notation : "+p.notation_publication().toString());
                                                        for(int i=0; i<notation_publication; i++){
                                                            InputStream inputStream = getResources().openRawResource(R.raw.star);
                                                            try {
                                                                SVG svg = SVG.getFromInputStream(inputStream);
                                                                // Créer un ImageButton
                                                                ImageButton imageButton = new ImageButton(requireContext());
                                                                imageButton.setId(View.generateViewId());

                                                                // Convertir le SVG en PictureDrawable
                                                                PictureDrawable pictureDrawable = new PictureDrawable(svg.renderToPicture());

                                                                // Définir le fond de l'ImageButton avec le PictureDrawable
                                                                imageButton.setImageDrawable(pictureDrawable);
                                                                layoutPersonnel.addView(imageButton);
                                                            } catch (SVGParseException e) {
                                                                throw new RuntimeException(e);
                                                            }
                                                            try {
                                                                inputStream.close();
                                                            } catch (IOException e) {
                                                                throw new RuntimeException(e);
                                                            }
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<List<Avis>> call, Throwable t) {
                                                    Log.e(TAG, "Failure: " + t.getMessage());
                                                    Toast.makeText(requireContext(), "Erreur lors de la communication avec le serveur pour la partie avis", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            //Ajout des layout
                                            layoutConteneur.addView(layoutProduit); layoutConteneur.addView(layoutPersonnel);

                                            //Mise en place de bordures
                                            GradientDrawable border = new GradientDrawable();
                                            border.setColor(Color.TRANSPARENT);
                                            border.setCornerRadius(20f);
                                            layoutConteneur.setBackground(border);layoutConteneur.setBackgroundResource(R.color.white);
                                            layout.addView(layoutConteneur);
                                        }
                                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e(TAG, "Error response: " + response.errorBody());
                                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<List<Publication>> call, @NonNull Throwable t) {
                                Log.e(TAG, "Failure: " + t.getMessage());
                                Toast.makeText(requireContext(), "Erreur lors de la communication avec le serveur", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Log.e(TAG, "Error: User ID is null");
                    }
                } else {
                    Log.e(TAG, "Error response: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.e(TAG, "Failure: " + t.getMessage());
            }
        });
    }
}