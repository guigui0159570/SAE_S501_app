package com.example.sae_s501;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.sae_s501.model.Utilisateur;
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.SessionManager;
import com.example.sae_s501.retrofit.UserService;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MesPublicationsFrag extends Fragment {

    private static final String TAG = "MesPublicationsFrag";
    private RetrofitService retrofitService;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fil_actu, container, false);
        loadData(view);
        return view;
    }

    private void loadData(View view) {
        String jwtEmail = SessionManager.getUserEmail(getActivity());

        retrofitService = new RetrofitService(getActivity());


        UserService userService = retrofitService.getRetrofit().create(UserService.class);

        Call<Long> getUserIdCall = userService.getUtilisateurIdByEmail(jwtEmail);
        getUserIdCall.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(@NonNull Call<Long> call, @NonNull Response<Long> response) {
                if (response.isSuccessful()) {
                    Long userId = response.body();
                    if (userId != null) {
                        Call<List<Publication>> getPublicationCall = userService.getPublicationByUtilisateurId(userId);
                        getPublicationCall.enqueue(new Callback<List<Publication>>() {
                            @Override
                            public void onResponse(@NonNull Call<List<Publication>> call, @NonNull Response<List<Publication>> response) {
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


                                            String titre = p.getTitre(); TextView titreText = new TextView(requireContext());titreText.setId(View.generateViewId());titreText.setText(titre);
                                            titreText.setLayoutParams(params_elt);
                                            String description = p.getDescription(); TextView desText = new TextView(requireContext());desText.setId(View.generateViewId());desText.setText(description);
                                            layoutTitreDes.addView(titreText); layoutTitreDes.addView(desText);

                                            //Param layoutPersonnel
                                            layoutPersonnel.setOrientation(LinearLayout.HORIZONTAL);
                                            layoutPersonnel.setGravity(LinearLayout.TEXT_ALIGNMENT_TEXT_START);
                                            layoutPersonnel.setId(View.generateViewId());
                                            layoutPersonnel.setBackgroundResource(R.color.blue);

                                            // Accéder aux valeurs de l'objet
                                            //mettre image utilisateur
                                            if(p.getProprietaire() != null){
                                                Utilisateur pseudo = p.getProprietaire(); TextView pseudoText = new TextView(requireContext());pseudoText.setId(View.generateViewId());pseudoText.setText(pseudo.getPseudo());
                                                layoutPersonnel.addView(pseudoText);
                                            }

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
                        Log.e(TAG, "User id is null");
                        Toast.makeText(requireContext(), "Error getting user id", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Long> call, @NonNull Throwable t) {
                Log.e(TAG, "Failure: " + t.getMessage());
                Toast.makeText(requireContext(), "Error getting user id", Toast.LENGTH_SHORT).show();
            }
        });
    }
}