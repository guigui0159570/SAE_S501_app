package com.example.sae_s501.model.MonCompte;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sae_s501.R;
import com.example.sae_s501.databinding.ActivityAbonnementCompteBinding;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AbonnementCompte extends AppCompatActivity {

    private ActivityAbonnementCompteBinding binding;
    private FonctionAbonneAbonnementViewModel FAAVM = new FonctionAbonneAbonnementViewModel(this);

    private ConfigSpring configSpring =  new ConfigSpring();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAbonnementCompteBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        CompletableFuture<String> completableFuture = FAAVM.RequestInformationAbonnement();
        Create_Layout_Abonnement(completableFuture);

    }

    public void Create_Layout_Abonnement(CompletableFuture<String> integerCompletableFuture){

        integerCompletableFuture.thenAccept(resultat -> {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JsonElement jsonElement = JsonParser.parseString(resultat);
                            JsonArray jsonArray = jsonElement.getAsJsonArray();
                            LinearLayout layoutPrincipal = findViewById(R.id.comptenuAbonnement);

                            Log.d("123123123", String.valueOf(jsonArray.size()));
                            for (int i = 0 ; i< jsonArray.size() ; i ++){

                                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();

                                //Partie id

                                JsonElement idElement = jsonObject.get("idUtilisateur");

                                //Partie pseudo
                                JsonElement pseudoElement = jsonObject.get("pseudo");
                                TextView textViewPseudo = new TextView(getBaseContext());
                                String pseudo = String.valueOf(pseudoElement).replaceAll("^\"|\"$", "");
                                textViewPseudo.setText(pseudo);




                                // Création d'un LinearLayout enfant
                                LinearLayout layoutEnfant = new LinearLayout(getBaseContext());
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        300);
                                layoutParams.setMargins(10, 10, 10, 30);
                                layoutEnfant.setLayoutParams(layoutParams);

                                layoutEnfant.setBackgroundColor(Color.WHITE);
                                layoutEnfant.setOrientation(LinearLayout.HORIZONTAL);
                                layoutEnfant.setGravity(Gravity.CENTER);
                                layoutEnfant.setWeightSum(4);


                                //Partie photo
                                ImageView imageView = new ImageView(getBaseContext());
                                try {

                                    JsonElement photoElement = jsonObject.get("photo");
                                    imageView.setLayoutParams(new LinearLayout.LayoutParams(
                                            250, 250, 1));

                                    if (photoElement != null && !photoElement.isJsonNull()) {
                                        String photoElementAsString = photoElement.getAsString();
                                        MonCompteViewModel monCompteViewModel = new MonCompteViewModel();
                                        monCompteViewModel.Imageprofil(getBaseContext(),imageView, photoElementAsString);
                                    } else {
                                        // Création image random
                                        String initials = String.valueOf(pseudo.charAt(0));
                                        int width = 200;
                                        int height = 200;

                                        int backgroundColor = ConfigSpring.couleurDefault;
                                        int textColor = Color.WHITE;

                                        MonCompteViewModel monCompteViewModel = new MonCompteViewModel();
                                        Bitmap generatedImage = monCompteViewModel.generateInitialsImage(initials, width, height, backgroundColor, textColor);
                                        imageView.setImageBitmap(generatedImage);
                                    }
                                } catch (Exception e) {
                                    Log.e("Exception", "Erreur lors de la lecture de la photo", e);
                                }



                                // Créez le premier TextView
                                TextView textView = new TextView(getBaseContext());
                                textView.setLayoutParams(new LinearLayout.LayoutParams(
                                        0,
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        2)); // Poids 2
                                textView.setText(pseudo);
                                textView.setTextSize(20);
                                textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                                final boolean[] isCoeurNoir = {true};
                                // Créez le deuxième ImageView
                                ImageView imageView2 = new ImageView(getBaseContext());
                                imageView2.setLayoutParams(new LinearLayout.LayoutParams(
                                        200,
                                        200,
                                        1)); // Poids 1
                                imageView2.setImageResource(R.drawable.coeurnoir);

                                imageView2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (isCoeurNoir[0]){
                                            imageView2.setImageResource(R.drawable.coeurblanc);
                                            FAAVM.deleteAbonneOrAbonnement(idElement.getAsLong());
                                            isCoeurNoir[0] = false;
                                        }else {
                                            imageView2.setImageResource(R.drawable.coeurnoir);
                                            FAAVM.sabonner(idElement.getAsLong());
                                            isCoeurNoir[0] = true;
                                        }
                                    }
                                });

                                // Ajoutez les éléments au LinearLayout enfant
                                layoutEnfant.addView(imageView);
                                layoutEnfant.addView(textView);
                                layoutEnfant.addView(imageView2);

                                // Ajoutez le LinearLayout enfant au LinearLayout principal
                                layoutPrincipal.addView(layoutEnfant);
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


}