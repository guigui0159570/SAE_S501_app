package com.example.sae_s501;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sae_s501.model.MonCompte.MonCompteViewModel;
import com.example.sae_s501.databinding.ActivityCompteUtilisateurBinding;
import com.example.sae_s501.databinding.MoncompterespBinding;
import com.example.sae_s501.model.MonCompte.ConfigSpring;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompteUtilisateur extends AppCompatActivity {

    private ActivityCompteUtilisateurBinding binding;
    MonCompteViewModel monCompteViewModel = new MonCompteViewModel();

    public CompteUtilisateur() throws ExecutionException, InterruptedException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompteUtilisateurBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (intent != null) {
            long userId = intent.getLongExtra("userId", 0);
            if (userId != 0) {
                informationUser(monCompteViewModel.requestInformation(this,userId), binding.getRoot());
            }
        }
    }
    public void informationUser(CompletableFuture<String> integerCompletableFuture, View root) {
        integerCompletableFuture.thenAccept(resultat -> {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("8899989933333", String.valueOf(resultat));
                        try {
                            JsonParser parser = new JsonParser();
                            Log.d("JSON_BEFORE_PARSE", resultat);
                            JsonObject jsonObject = parser.parse(resultat).getAsJsonObject();
                            Log.d("JSON_AFTER_PARSE", jsonObject.toString());

                            // Partie abonne
                            try {
                                JsonElement countAbonneElement = jsonObject.get("countAbonne");
                                Log.d("7783333333", String.valueOf(countAbonneElement));
                                TextView textViewAbonne = root.findViewById(R.id.countAbonneUti);
                                textViewAbonne.setText(String.valueOf(countAbonneElement).replaceAll("^\"|\"$", ""));
                            } catch (Exception e) {
                                Log.e("Exception", "Erreur lors de la lecture de countAbonne", e);
                            }

                            // Partie abonnement
                            try {
                                JsonElement countAbonnementElement = jsonObject.get("countAbonnement");
                                TextView textViewAbonnement = root.findViewById(R.id.countAbonnementUti);
                                textViewAbonnement.setText(String.valueOf(countAbonnementElement).replaceAll("^\"|\"$", ""));
                            } catch (Exception e) {
                                Log.e("Exception", "Erreur lors de la lecture de countAbonnement", e);
                            }

                            // Partie pseudo

                            JsonElement pseudoElement = jsonObject.get("pseudo");
                            TextView textViewPseudo = root.findViewById(R.id.compteutilisateur);
                            String pseudo = String.valueOf(pseudoElement).replaceAll("^\"|\"$", "");
                            textViewPseudo.setText(pseudo);


                            // Partie Description
                            TextView textViewDescription = root.findViewById(R.id.descriptionUti);
                            JsonElement descriptionElement = jsonObject.get("description");
                            if (!descriptionElement.isJsonNull()) {
                                Log.d("TAG7", "run: ");
                                String Description = descriptionElement.getAsString();
                                String decodedDescription = URLDecoder.decode(Description, StandardCharsets.UTF_8.toString());
                                textViewDescription.setText(decodedDescription);
                            }


                            Log.d("TAG", "run: ");
                            // Partie photo
                            try {
                                ImageView imageViewPhoto = root.findViewById(R.id.photoProfilUti);
                                JsonElement photoElement = jsonObject.get("photo");
                                Log.d("ggggggggg", String.valueOf(photoElement));
                                if (photoElement != null && !photoElement.isJsonNull()) {
                                    Log.d("dddddd", "run: ");
                                    String photoElementAsString = photoElement.getAsString();
                                    MonCompteViewModel monCompteViewModel = new MonCompteViewModel();
                                    monCompteViewModel.Imageprofil(getBaseContext(),imageViewPhoto, photoElementAsString);
                                } else {
                                    Log.d("fffffff", "run: ");
                                    // Création image random
                                    String initials = String.valueOf(pseudo.charAt(0));
                                    int width = 200;
                                    int height = 200;

                                    int backgroundColor = ConfigSpring.couleurDefault;
                                    int textColor = Color.WHITE;

                                    MonCompteViewModel monCompteViewModel = new MonCompteViewModel();
                                    Bitmap generatedImage = monCompteViewModel.generateInitialsImage(initials, width, height, backgroundColor, textColor);
                                    imageViewPhoto.setImageBitmap(generatedImage);
                                }
                            } catch (Exception e) {
                                Log.e("Exception", "Erreur lors de la lecture de la photo", e);
                            }
                        } catch (Exception e) {
                            Log.e("Exception", "Erreur lors de l'analyse JSON", e);
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("Exception", "Une autre exception s'est produite", e);
            }
        });
    }
}