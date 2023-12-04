package com.example.sae_s501;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sae_s501.MonCompte.AbonneCompte;
import com.example.sae_s501.MonCompte.AbonnementCompte;
import com.example.sae_s501.MonCompte.ConfigSpring;
import com.example.sae_s501.databinding.UpdatemoncompteBinding;
import com.example.sae_s501.retrofit.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.example.sae_s501.MonCompte.MonCompteViewModel;
import com.example.sae_s501.databinding.MoncompterespBinding;

import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class MyCompteActivity extends AppCompatActivity {
    private MoncompterespBinding binding;
    private UpdatemoncompteBinding bindingUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MoncompterespBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        MonCompteViewModel monCompteViewModel = new ViewModelProvider(this).get(MonCompteViewModel.class);

        //Partie information
        CompletableFuture<String> stringCompletableFuture = monCompteViewModel.RequestInformation();
        informationUser(stringCompletableFuture, root);

        //Partie fragment parametre
        View fragment = root.findViewById(R.id.parametreCompte);
        fragment.setVisibility(View.INVISIBLE);

        LinearLayout floutage = root.findViewById(R.id.floutage);
        floutage.setVisibility(View.INVISIBLE);

        //Style tool barre
        LinearLayout layoutProfil = root.findViewById(R.id.profilLayout);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#bdbdbd" ));
        layoutProfil.setBackground(colorDrawable);

        // ouverture animé du parametre
        ImageButton troisPoints = root.findViewById(R.id.troispoints);
        troisPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Créez une animation d'échelle
                Animation scaleInAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.fragment_parametre);
                Animation floutageAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.animfloutage);

                // Appliquez l'animation d'échelle aux éléments
                fragment.startAnimation(scaleInAnimation);
                floutage.startAnimation(floutageAnimation);

                fragment.setVisibility(View.VISIBLE);
                floutage.setVisibility(View.VISIBLE);
                floutage.setClickable(true);
            }
        });

        // fermeture animé du parametre
        Button closeParametre = root.findViewById(R.id.closeParametre);
        closeParametre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation scaleInAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.inverseanimfloutage);
                Animation fragmentAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.inversefragment_parametre);

                floutage.startAnimation(scaleInAnimation);
                fragment.startAnimation(fragmentAnim);

                fragment.setVisibility(View.INVISIBLE);
                floutage.setVisibility(View.INVISIBLE);
                floutage.setClickable(false);
            }
        });

        //Page modification profile
        Button updateProfile = root.findViewById(R.id.updateprofile);
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MyUpdateCompteActivity.class);
                startActivity(intent);
            }
        });

        //Deconnexion
        Button deconnexion = root.findViewById(R.id.deconnexion);
        deconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager.deleteToken(getApplicationContext());
                Toast.makeText(MyCompteActivity.this, "Vous êtes déconnecté !", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(view.getContext(), Connexion.class);
                startActivity(intent);

            }
        });
        Button langue = root.findViewById(R.id.langues);
        ImageView flag =  root.findViewById(R.id.flag_langue);
        Locale currentLocale = getResources().getConfiguration().locale;

        if (currentLocale.getLanguage().equals("en")) {
            flag.setImageResource(R.drawable.france_flag);
        } else {
            flag.setImageResource(R.drawable.greatbritain);
        }
        langue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Changer la langue de l'application
                if (currentLocale.getLanguage().equals("en")) {
                    setLocale("fr");
                } else {
                    setLocale("en");
                }
                recreate();
            }
        });

        TextView textViewAbonnementview = root.findViewById(R.id.AbonnementView);
        textViewAbonnementview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AbonnementCompte.class);
                startActivity(intent);
            }
        });

        TextView textViewAbonneview = root.findViewById(R.id.AbonneView);
        textViewAbonneview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AbonneCompte.class);
                startActivity(intent);
            }
        });
    }
    public void informationUser(CompletableFuture<String> integerCompletableFuture, View root){
        integerCompletableFuture.thenAccept(resultat -> {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JsonParser parser = new JsonParser();
                            JsonObject jsonObject = parser.parse(resultat).getAsJsonObject();
                            //Partie abonne
                            JsonElement countAbonneElement = jsonObject.get("countAbonne");
                            TextView textViewAbonne = root.findViewById(R.id.countAbonne);
                            textViewAbonne.setText(String.valueOf(countAbonneElement).replaceAll("^\"|\"$", ""));

                            //Partie abonnenement
                            JsonElement countAbonnementElement = jsonObject.get("countAbonnement");
                            TextView textViewAbonnement = root.findViewById(R.id.countAbonnement);
                            textViewAbonnement.setText(String.valueOf(countAbonnementElement).replaceAll("^\"|\"$", ""));

                            //Partie pseudo
                            JsonElement pseudoElement = jsonObject.get("pseudo");
                            TextView textViewPseudo = root.findViewById(R.id.mon_compte);
                            String pseudo = String.valueOf(pseudoElement).replaceAll("^\"|\"$", "");
                            textViewPseudo.setText(pseudo);

                            //Partie Description
                            JsonElement descriptionElement = jsonObject.get("description");
                            TextView textViewDescription = root.findViewById(R.id.description);
                            Gson gson = new Gson();
                            String extractedString = gson.fromJson(descriptionElement, String.class);
                            textViewDescription.setText(extractedString);

                            //Partie photo
                            ImageView imageViewPhoto = root.findViewById(R.id.photoProfil);
                            JsonElement photoElement = jsonObject.get("photo");

                            if (photoElement != null && !photoElement.isJsonNull()) {
                                String base64ImageData = photoElement.getAsString();

                                // Décodez la chaîne Base64 en un tableau de bytes
                                byte[] decodedImageData = Base64.getDecoder().decode(base64ImageData);

                                // Convertir le tableau de bytes en un Bitmap
                                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImageData, 0, decodedImageData.length);

                                // Afficher le Bitmap dans l'ImageView

                                imageViewPhoto.setImageBitmap(bitmap);
                            }else {
                                //creation image random
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
                            throw new RuntimeException(e);
                        }
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
    }


}