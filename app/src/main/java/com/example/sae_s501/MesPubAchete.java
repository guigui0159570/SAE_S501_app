package com.example.sae_s501;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.sae_s501.activity.MesPubProdGratuitActivity;
import com.example.sae_s501.activity.MesPubProdPayantActivity;
import com.example.sae_s501.activity.MesPublicationsActivity;
import com.example.sae_s501.model.Dictionnaire;
import com.example.sae_s501.model.GlobalFunctionsPublication;
import com.example.sae_s501.model.User.Publication;
import com.example.sae_s501.model.Utilisateur;
import com.example.sae_s501.retrofit.FilActuService;
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.SessionManager;
import com.example.sae_s501.retrofit.UserService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MesPubAchete extends Fragment {

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
                        Call<List<Publication>> callPublications = filActuService.getPubAcheteById(userId);
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
                                                Log.d(TAG, "requireContext: "+requireContext().toString());
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
                                                        loadView(view, layoutConteneur.getId(), new Intent(requireContext(), MesPubProdGratuitActivity.class));
                                                    });
                                                }else {
                                                    layoutConteneur.setOnClickListener(view -> {
                                                        loadView(view, layoutConteneur.getId(), new Intent(requireContext(), MesPubProdPayantActivity.class));
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
                                                GlobalFunctionsPublication.callAvis(filActuService,p,layoutConteneur);


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
                                                supp.setImageResource(R.drawable.download);

                                                RelativeLayout.LayoutParams clickableImageParams = new RelativeLayout.LayoutParams(
                                                        dpToPx(32),
                                                        dpToPx(32)
                                                );
                                                clickableImageParams.setMargins(0, 0, dpToPx(20), dpToPx(20));
                                                supp.setLayoutParams(clickableImageParams);
                                                supp.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        UserService userService1 = retrofitService.getRetrofit().create(UserService.class);

                                                        // Effectuer la requête de téléchargement
                                                        Call<ResponseBody> call = userService1.downloadFile(p.getFichier());

                                                        call.enqueue(new Callback<ResponseBody>() {
                                                            @Override
                                                            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                                                                if (response.isSuccessful()) {
                                                                    // Traitement réussi, enregistrez le fichier localement
                                                                    Log.d("FICHIER", "dans cal");

                                                                    saveFileLocally(response.body(),p.getFichier());
                                                                } else {
                                                                    // Traitement en cas d'échec
                                                                    showToast("Échec du téléchargement. Code : " + response.code());
                                                                    Log.d("FICHIER", "dans cal echec");

                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                                                                // Traitement en cas d'échec de la requête
                                                                showToast("Erreur lors de la requête : " + t.getMessage());

                                                            }
                                                        });
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


    private void saveFileLocally(ResponseBody body,String fichier) {
        try {
            // Vérifier si le stockage externe est disponible
            if (isExternalStorageWritable()) {
                // Obtenez le répertoire de téléchargement externe
                File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                // Créez le fichier local dans le répertoire de téléchargement
                String fileName = fichier;
                File file = new File(downloadFolder, fileName);

                // Vérifiez si le fichier existe déjà
                if (file.exists()) {
                    showToast("Le fichier est déjà enregistré localement : " + file.getAbsolutePath());
                    return;
                }


                // Créez le flux de sortie pour écrire dans le fichier local
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(body.bytes());
                outputStream.close();

                // Le fichier a été enregistré localement avec succès
                showToast("Fichier enregistré localement : " + file.getAbsolutePath());
            } else {
                // Le stockage externe n'est pas disponible
                showToast("Le stockage externe n'est pas disponible.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Une exception s'est produite lors de l'enregistrement du fichier
            showToast("Erreur lors de l'enregistrement du fichier localement.");
        }
    }
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
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
                    Intent intent = new Intent(getActivity(), MesPublicationsActivity.class);
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
