package com.example.sae_s501;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.sae_s501.databinding.MoncompterespBinding;
import com.example.sae_s501.databinding.NotificationsrespBinding;
import com.example.sae_s501.databinding.UpdatemoncompteBinding;
import com.example.sae_s501.model.MonCompte.ConfigSpring;
import com.example.sae_s501.model.MonCompte.FonctionNotificationViewModel;
import com.example.sae_s501.model.MonCompte.MonCompteViewModel;
import com.example.sae_s501.retrofit.FilActuService;
import com.example.sae_s501.retrofit.PanierService;
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.SessionManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class VisuNotification  extends AppCompatActivity {

    private ConfigSpring configSpring = new ConfigSpring();
    private NotificationsrespBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = NotificationsrespBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        FonctionNotificationViewModel FNVM = new FonctionNotificationViewModel(this);
        CompletableFuture<String> completableFuture= FNVM.requestInformationNotification(this) ;
        informationUserForNotification(completableFuture,root);
    }

    public void informationUserForNotification(CompletableFuture<String> integerCompletableFuture, View root){
        Log.d("33333", "run: ");
        integerCompletableFuture.thenAccept(resultat -> {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("77777", String.valueOf(resultat));
                            JsonElement jsonElement = JsonParser.parseString(resultat);
                            JsonArray jsonArray = jsonElement.getAsJsonArray();
                            LinearLayout linearLayout = findViewById(R.id.notificationsList);
                            for (int i = 0 ; i< jsonArray.size() ; i ++) {

                                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();

                                //Partie message
                                JsonElement messageJson = jsonObject.get("notifMessage");
                                Log.d("8888888", String.valueOf(messageJson));

                                String messageNotif = String.valueOf(messageJson).replaceAll("^\"|\"$", "");
                                TextView textView = new TextView(getBaseContext());
                                textView.setText(messageNotif);



                                // Création d'un LinearLayout enfant
                                LinearLayout layoutEnfant = new LinearLayout(getBaseContext());
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        300);
                                layoutParams.setMargins(10, 10, 10, 30);
                                layoutEnfant.setLayoutParams(layoutParams);

                                layoutEnfant.setBackgroundColor(Color.WHITE);
                                layoutEnfant.setOrientation(LinearLayout.HORIZONTAL);
                                layoutEnfant.setWeightSum(4);


                                //Création d'un LinearLayout image
                                LinearLayout layoutImage = new LinearLayout(getBaseContext());
                                LinearLayout.LayoutParams layoutParamsImage = new LinearLayout.LayoutParams(
                                        0,
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        1);
                                layoutImage.setLayoutParams(layoutParamsImage);
                                layoutImage.setGravity(Gravity.CENTER_VERTICAL);

                                //Création d'un LinearLayout text
                                LinearLayout layoutText = new LinearLayout(getBaseContext());
                                LinearLayout.LayoutParams layoutParamsText = new LinearLayout.LayoutParams(
                                        0,
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        3);
                                layoutText.setLayoutParams(layoutParamsText);
                                layoutText.setOrientation(LinearLayout.VERTICAL);

                                //Création d'un LinearLayout haut bas
                                LinearLayout linearlayoutHaut = new LinearLayout(getBaseContext());
                                LinearLayout linearlayoutBas = new LinearLayout(getBaseContext());
                                LinearLayout.LayoutParams layoutParamsHautBas = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        0,
                                        1);
                                linearlayoutHaut.setLayoutParams(layoutParamsHautBas);
                                linearlayoutHaut.setOrientation(LinearLayout.HORIZONTAL);
                                linearlayoutBas.setLayoutParams(layoutParamsHautBas);
                                linearlayoutBas.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);

                                LinearLayout linearPseudo = new LinearLayout(getBaseContext());
                                LinearLayout linearDate = new LinearLayout(getBaseContext());
                                LinearLayout.LayoutParams layoutParamsGaucheDroite = new LinearLayout.LayoutParams(
                                        0,
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        1);
                                linearPseudo.setLayoutParams(layoutParamsGaucheDroite);
                                linearPseudo.setGravity(Gravity.CENTER_VERTICAL);
                                linearDate.setLayoutParams(layoutParamsGaucheDroite);
                                linearDate.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);


                                //Partie pseudo
                                JsonElement pseudoElement = jsonObject.get("pseudo");
                                String pseudo = String.valueOf(pseudoElement).replaceAll("^\"|\"$", "");
                                TextView textViewpseudo = new TextView(getBaseContext());
                                textViewpseudo.setText(pseudo);

                                //Partie Date
                                JsonElement DateElement = jsonObject.get("Date");
                                String Date = String.valueOf(DateElement).replaceAll("^\"|\"$", "");
                                TextView textViewdate = new TextView(getBaseContext());
                                textViewdate.setText(Date);

                                //Partie message
                                JsonElement MessageElement = jsonObject.get("notifMessage");
                                String message = String.valueOf(MessageElement).replaceAll("^\"|\"$", "");
                                TextView textViewmessage = new TextView(getBaseContext());
                                textViewmessage.setText(message);
                                textViewmessage.setTextSize(20);

                                // Partie photo
                                ImageView imageView = new ImageView(getBaseContext());
                                try {
                                    //Partie photo

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


                                layoutImage.addView(imageView);
                                layoutEnfant.addView(layoutImage);
                                linearPseudo.addView(textViewpseudo);
                                linearDate.addView(textViewdate);
                                linearlayoutHaut.addView(linearPseudo);
                                linearlayoutHaut.addView(linearDate);
                                linearlayoutBas.addView(textViewmessage);
                                layoutText.addView(linearlayoutHaut);
                                layoutText.addView(linearlayoutBas);
                                layoutEnfant.addView(layoutText);
                                linearLayout.addView(layoutEnfant);
                                Log.d("10010101010", String.valueOf(jsonArray.size()));

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
