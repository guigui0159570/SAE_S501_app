package com.example.sae_s501;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sae_s501.model.MonCompte.FonctionAbonneAbonnementViewModel;
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
    private MonCompteViewModel monCompteViewModel = new MonCompteViewModel();
    private FonctionAbonneAbonnementViewModel FAAVM = new FonctionAbonneAbonnementViewModel(this);


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
            //Frag publication autre compte
            PubCompteUti pubCompteUti = new PubCompteUti();
            pubCompteUti.setFilterValue(userId);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_autrecompte_pub, pubCompteUti)
                    .commit();

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


                            //Partie id

                            JsonElement idElement = jsonObject.get("idUtilisateur");

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
                                RelativeLayout.LayoutParams clickableImageParams = new RelativeLayout.LayoutParams(
                                        dpToPx(42),
                                        dpToPx(42)
                                );
                                final boolean[] isCoeurBlanc = {true};
                                FAAVM.requestPresenceAbonnement(idElement.getAsLong()).thenAccept(resultat -> {
                                    try {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {

                                                    // Créez le deuxième ImageView
                                                    ImageView imageView2 = new ImageView(getBaseContext());
//                                                    imageView2.setLayoutParams(new LinearLayout.LayoutParams(
//                                                            200,
//                                                            200,
//                                                            1)); // Poids 1
                                                    if (resultat) {
                                                        imageView2.setImageResource(R.drawable.coeurnoir);
                                                        isCoeurBlanc[0] = false;
                                                    }else {
                                                        imageView2.setImageResource(R.drawable.coeurblanc);
                                                        isCoeurBlanc[0] = true;
                                                    }
                                                    imageView2.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            if (isCoeurBlanc[0]){
                                                                imageView2.setImageResource(R.drawable.coeurnoir);
                                                                FAAVM.sabonner(idElement.getAsLong());
                                                                isCoeurBlanc[0] = false;

                                                            }else {
                                                                imageView2.setImageResource(R.drawable.coeurblanc);
                                                                FAAVM.deleteAbonneOrAbonnement(idElement.getAsLong());
                                                                isCoeurBlanc[0] = true;
                                                            }
                                                        }
                                                    });

                                                    // Appliquer les paramètres de layout à l'ImageView

                                                    LinearLayout layoutCoeur = findViewById(R.id.coeurAbonnement);


                                                    imageView2.setId(View.generateViewId());

                                                    clickableImageParams.setMargins(0, 0, dpToPx(20), dpToPx(20));
                                                    imageView2.setLayoutParams(clickableImageParams);

                                                    // Ajoutez le LinearLayout enfant au LinearLayout principal
                                                    layoutCoeur.addView(imageView2);

                                                } catch (Exception e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                        });
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                });

                                final boolean[] isClocheBlanche = {true};
                                FAAVM.requestPresenceAbonnement(idElement.getAsLong()).thenAccept(resultat -> {
                                    try {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {

                                                    // Créez le deuxième ImageView
                                                    ImageView cloche = new ImageView(getBaseContext());
                                                    if (resultat) {
                                                        cloche.setImageResource(R.drawable.clochejaune);
                                                        isClocheBlanche[0] = false;
                                                    }else {
                                                        cloche.setImageResource(R.drawable.cloche);
                                                        isClocheBlanche[0] = true;
                                                    }
                                                    cloche.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            if (isCoeurBlanc[0]){
                                                                cloche.setImageResource(R.drawable.clochejaune);
                                                                FAAVM.sabonner(idElement.getAsLong());
                                                                isCoeurBlanc[0] = false;

                                                            }else {
                                                                cloche.setImageResource(R.drawable.cloche);
                                                                FAAVM.deleteAbonneOrAbonnement(idElement.getAsLong());
                                                                isCoeurBlanc[0] = true;
                                                            }
                                                        }
                                                    });

                                                    // Appliquer les paramètres de layout à l'ImageView

                                                    LinearLayout layoutCloche = findViewById(R.id.clochNotif);


                                                    cloche.setId(View.generateViewId());

                                                    clickableImageParams.setMargins(0, 0, dpToPx(20), dpToPx(20));
                                                    cloche.setLayoutParams(clickableImageParams);



                                                    // Ajoutez le LinearLayout enfant au LinearLayout principal
                                                    layoutCloche.addView(cloche);

                                                } catch (Exception e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                        });
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                });





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
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
