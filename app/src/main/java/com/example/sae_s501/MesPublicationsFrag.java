package com.example.sae_s501;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.sae_s501.authentification.Authentification;
import com.example.sae_s501.model.Utilisateur;
import com.example.sae_s501.retrofit.FilActuService;
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.SessionManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MesPublicationsFrag extends Fragment {

    private static final String TAG = "MesPublicationsFrag";
    private static final String BASE_URL = Dictionnaire.getIpAddress();
    private RetrofitService retrofitService;

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
        retrofitService = new RetrofitService(getContext());



        FilActuService filActuService = retrofitService.getRetrofit().create(FilActuService.class);

        String jwtEmail = SessionManager.getUserEmail(requireActivity());

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
                                if(isAdded()){
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
                                                        loadView(view, layoutConteneur.getId(), new Intent(requireContext(), MesPubProdGratuit.class));
                                                    });
                                                }else {
                                                    layoutConteneur.setOnClickListener(view -> {
                                                        loadView(view, layoutConteneur.getId(), new Intent(requireContext(), MesPubProdPayant.class));
                                                    });
                                                }


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

                                                Call<ResponseBody> callImage = filActuService.getImage(p.getImage());
                                                Log.d("IMAGE", p.getImage());

                                                callImage.enqueue(new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        if (getActivity() != null && getContext() != null){
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
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                        // Gestion des erreurs
                                                        Log.e("IMAGE", "Échec de la requête pour récupérer l'image : " + t.getMessage());
                                                    }
                                                });
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
                                                    prixText.setText("    Gratuit      ");

                                                }
                                                else{
                                                    prixText.setText("    Prix : " + prix+"      ");

                                                }
                                                prixText.setLayoutParams(params_elt);

                                                int nbTelechargement = p.getNb_telechargement();
                                                TextView textnbTelechargement = new TextView(requireContext());
                                                textnbTelechargement.setId(View.generateViewId());
                                                textnbTelechargement.setText("Téléchargement : " + nbTelechargement+ "   ");
                                                textnbTelechargement.setLayoutParams(params_elt);

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
                                                    layoutPersonnel.addView(textnbTelechargement);


                                                }
                                                ImageView supp = new ImageView(requireContext());
                                                supp.setId(View.generateViewId());
                                                supp.setImageResource(R.drawable.poubelle);

                                                RelativeLayout.LayoutParams clickableImageParams = new RelativeLayout.LayoutParams(
                                                        dpToPx(32),
                                                        dpToPx(32)
                                                );
                                                clickableImageParams.setMargins(0, 0, dpToPx(20), dpToPx(20));
                                                supp.setLayoutParams(clickableImageParams);

                                                supp.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        DeleteConfirmation(p.getId());
                                                    }
                                                });
                                                layoutPersonnel.addView(supp);

                                                //Ajout des layout
                                                layoutConteneur.addView(layoutProduit);
                                                layoutConteneur.addView(layoutPersonnel);

                                                //Mise en place de bordures
                                                GradientDrawable border = new GradientDrawable();
                                                border.setColor(Color.TRANSPARENT);
                                                border.setCornerRadius(20f);
                                                layoutConteneur.setBackground(border);layoutConteneur.setBackgroundResource(R.color.white);
                                                layout.addView(layoutConteneur);

                                                //Creation de la rating bar
                                                Call<List<AvisDTO>> avisDTOCall = filActuService.getAllAvisByPublication(p.getId());
                                                avisDTOCall.enqueue(new Callback<List<AvisDTO>>() {
                                                    @SuppressLint("RtlHardcoded")
                                                    @RequiresApi(api = Build.VERSION_CODES.Q)
                                                    @Override
                                                    public void onResponse(@NonNull Call<List<AvisDTO>> call, @NonNull Response<List<AvisDTO>> response) {
                                                        if(response.isSuccessful()){
                                                            List<AvisDTO> les_avis = response.body();
                                                            RatingBar new_rating_bar = new RatingBar(MesPublicationsFrag.this.requireContext());
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
                                        } else {
                                            Log.e(TAG, "Error response: " + response.errorBody());
                                            Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
                                        }
                                        if (publications.isEmpty()) {
                                            // Aucune publication, afficher un message
                                            TextView emptyTextView = new TextView(requireContext());
                                            emptyTextView.setText("Vous n'avez pas de publication !");
                                            emptyTextView.setGravity(Gravity.CENTER);
                                            emptyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                                            emptyTextView.setTextColor(Color.parseColor("#FFA500"));
                                            layout.addView(emptyTextView);
                                        }
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
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
    private void DeleteConfirmation(long publicationId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirmation")
                .setMessage("Voulez-vous vraiment supprimer cette publication?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePublication(publicationId);
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

    private void deletePublication(long publicationId) {
        retrofitService = new RetrofitService(getContext());
        FilActuService filActuService = retrofitService.getRetrofit().create(FilActuService.class);
        Call<Void> callsupp = filActuService.deletePublication(publicationId);
        callsupp.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("REQUETE_SUPPRESSION", "Publication supprimée avec succès");
                    showToast("Publication supprimée avec succès");
                    Intent intent = new Intent(getActivity(), MesPublications.class);
                    startActivity(intent);
                } else {
                    Log.e("REQUETE_SUPPRESSION", "Échec de la suppression de la publication. Code de réponse : " + response.code());
                    showToast("Échec de la suppression de la publication. Veuillez réessayer.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("REQUETE_SUPPRESSION", "Échec de la suppression de la publication. Erreur : " + t.getMessage());
                showToast("Échec de la suppression de la publication. Veuillez vérifier votre connexion internet.");
            }
        });
    }

    public static void loadView(View view, long id, Intent intent){
        Log.d("ID de la pub", ""+id);
        intent.putExtra("id", id);
        view.getContext().startActivity(intent);
    }
}
