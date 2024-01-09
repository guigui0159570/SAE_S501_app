package com.example.sae_s501;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
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
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.SessionManager;
import com.example.sae_s501.retrofit.UserService;
import com.example.sae_s501.visualisation.Visualiser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    private ImageView retour;
    private TextView telechargement;
    private UserService userService;
    private RetrofitService retrofitService;

    private String fichier;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.produit_gratuitresp);
        long publicationId = getIntent().getLongExtra("id", 0);
        View rootView = findViewById(android.R.id.content);
        loadPublication(rootView, publicationId);
        retour = findViewById(R.id.close);
        telechargement = findViewById(R.id.telecharger_img);
        retrofitService = new RetrofitService(this);

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProduitGratuit.this, FilActu.class);
                startActivity(intent);
            }
        });
        telechargement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserService userService1 = retrofitService.getRetrofit().create(UserService.class);

                // Effectuer la requête de téléchargement
                Call<ResponseBody> call = userService1.downloadFile(fichier);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            // Traitement réussi, enregistrez le fichier localement
                            saveFileLocally(response.body());
                        } else {
                            // Traitement en cas d'échec
                            showToast("Échec du téléchargement. Code : " + response.code());
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
    }

    private void saveFileLocally(ResponseBody body) {
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
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
                        fichier =  publication.getFichier();
                        Log.d("FICHIER", publication.getFichier());

                        View visulaiser = findViewById(R.id.visualiser_img);
                        visulaiser.setOnClickListener(view -> {
                            loadView(view, publication.getFichier(), new Intent(ProduitGratuit.this, Visualiser.class));
                        });

                        if(publication.getProprietaire() != null){
                            pseudo.setText(publication.getProprietaire().getPseudo());
                            pseudo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), CompteUtilisateur.class);
                                    intent.putExtra("userId",publication.getProprietaire().getId());
                                    startActivity(intent);
                                }
                            });
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
                                    Log.d("AVIS", "Nombre d'avis récupérés : " + (les_avis != null ? les_avis.size() : 0));
                                    LinearLayout commentaires = view.findViewById(R.id.layout_to_commentaire_gratuit);
                                    commentaires.setOrientation(LinearLayout.VERTICAL);
                                    assert les_avis != null;
                                    if(les_avis.size() != 0){
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
                                    }else{
                                        TextView textView = new TextView(ProduitGratuit.this.getApplicationContext());
                                        textView.setText("Cette publication ne possède pas d'avis...");
                                        textView.setTextColor(Color.parseColor("#FFA500"));
                                        textView.setTextSize(18);
                                        commentaires.addView(textView);
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
                                Call<Void> voidCall = filActuService.saveAvis(commentaire.getText().toString(), (int) etoiles.getRating(), publicationId, userId);
                                voidCall.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                        Log.d(TAG, "onResponse: "+response.code());
                                        if (response.isSuccessful()){
                                            Toast.makeText(ProduitGratuit.this.getApplicationContext(), "Commentaire ajouté", Toast.LENGTH_SHORT).show();
                                            etoiles.setRating(0);
                                            commentaire.setText("");
                                            recreate();
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
    public static void loadView(View view, String fichier, Intent intent){
        Log.d("Fichier", ""+fichier);
        intent.putExtra("fichier", fichier);
        view.getContext().startActivity(intent);
    }
}