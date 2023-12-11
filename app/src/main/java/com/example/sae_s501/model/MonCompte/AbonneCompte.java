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

public class AbonneCompte extends AppCompatActivity {
    private ConfigSpring configSpring = new ConfigSpring();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abonne_compte);
        CompletableFuture<String> completableFuture = RequestInformationAbonne();
        Create_Layout_Abonne(completableFuture);

    }

    public void Create_Layout_Abonne(CompletableFuture<String> integerCompletableFuture){

        integerCompletableFuture.thenAccept(resultat -> {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JsonElement jsonElement = JsonParser.parseString(resultat);
                            JsonArray jsonArray = jsonElement.getAsJsonArray();
                            LinearLayout layoutPrincipal = findViewById(R.id.comptenuAbonne);

                            Log.d("123123123", String.valueOf(jsonArray.size()));
                            for (int i = 0 ; i< jsonArray.size() ; i ++){

                                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();

                                //Partie id

                                JsonElement idElement = jsonObject.get("idUtilisateur");

                                //Partie pseudo
                                JsonElement pseudoElement = jsonObject.get("pseudo");
                                Log.d("456456", String.valueOf(pseudoElement));
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
                                JsonElement photoElement = jsonObject.get("photo");
                                imageView.setLayoutParams(new LinearLayout.LayoutParams(
                                        250, 250, 1));
                                if (photoElement != null && !photoElement.isJsonNull()) {
                                    String base64ImageData = photoElement.getAsString();

                                    // Décodez la chaîne Base64 en un tableau de bytes
                                    byte[] decodedImageData = Base64.getDecoder().decode(base64ImageData);

                                    // Convertir le tableau de bytes en un Bitmap
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImageData, 0, decodedImageData.length);

                                    // Afficher le Bitmap dans l'ImageView

                                    imageView.setImageBitmap(bitmap);
                                }else {
                                    //creation image random
                                    String initials = String.valueOf(pseudo.charAt(0));
                                    int width = 200;
                                    int height = 200;

                                    int backgroundColor = ConfigSpring.couleurDefault;
                                    int textColor = Color.WHITE;

                                    MonCompteViewModel monCompteViewModel = new MonCompteViewModel();
                                    Bitmap generatedImage = monCompteViewModel.generateInitialsImage(initials, width, height, backgroundColor, textColor);
                                    imageView.setImageBitmap(generatedImage);
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
                                final boolean[] isCoeurBlanc = {true};
                                requestPresenceAbonnement(idElement.getAsLong()).thenAccept(resultat -> {
                                    try {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {

                                                    Log.d("99654123", String.valueOf(resultat));
                                                    // Créez le deuxième ImageView
                                                    ImageView imageView2 = new ImageView(getBaseContext());
                                                    imageView2.setLayoutParams(new LinearLayout.LayoutParams(
                                                            200,
                                                            200,
                                                            1)); // Poids 1
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
                                                                sabonner(idElement.getAsLong());
                                                                isCoeurBlanc[0] = false;

                                                            }else {
                                                                imageView2.setImageResource(R.drawable.coeurblanc);
                                                                deleteAbonnement(idElement.getAsLong());
                                                                isCoeurBlanc[0] = true;
                                                            }
                                                        }
                                                    });

                                                    // Ajoutez les éléments au LinearLayout enfant
                                                    layoutEnfant.addView(imageView);
                                                    layoutEnfant.addView(textView);
                                                    layoutEnfant.addView(imageView2);

                                                    // Ajoutez le LinearLayout enfant au LinearLayout principal
                                                    layoutPrincipal.addView(layoutEnfant);
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

    public CompletableFuture<String> RequestInformationAbonne() {
        OkHttpClient client = configSpring.creationClientSansSSL();
        ConfigSpring configSpring = new ConfigSpring();
        Request request = new Request.Builder()
                .url("http://"+configSpring.Adresse()+":8080/abonneUser/"+configSpring.userEnCour()+"")
                .build();
        CompletableFuture<String> futureInformation = new CompletableFuture<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                futureInformation.completeExceptionally(e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    futureInformation.complete(responseData);
                } else {
                    futureInformation.completeExceptionally(new RuntimeException("Request failed"));
                }
            }
        });
        return futureInformation;
    }

    public void deleteAbonnement(Long abonnementUserId) {
        OkHttpClient client = new OkHttpClient();
        ConfigSpring configSpring = new ConfigSpring();

        Request request = new Request.Builder()
                .url("http://" + configSpring.Adresse() + ":8080/desabonnemnt/" + configSpring.userEnCour() + "/" + abonnementUserId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    System.out.println("Response Data: " + responseData);
                } else {
                    throw new RuntimeException("Request failed");
                }
            }
        });
    }


    public void sabonner(Long abonnementUserId) {
        OkHttpClient client = new OkHttpClient();
        ConfigSpring configSpring = new ConfigSpring();

        Request request = new Request.Builder()
                .url("http://" + configSpring.Adresse() + ":8080/abonnenement/" + configSpring.userEnCour() + "/" + abonnementUserId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    System.out.println("Response Data: " + responseData);
                } else {
                    throw new RuntimeException("Request failed");
                }
            }
        });
    }


    public CompletableFuture<Boolean> requestPresenceAbonnement(Long idAbonne) {
        ConfigSpring configSpring = new ConfigSpring();
        OkHttpClient client = configSpring.creationClientSansSSL();

        CompletableFuture<Boolean> futureInformation = new CompletableFuture<>();

        Request request = new Request.Builder()
                .url("http://" + configSpring.Adresse() + ":8080/presenceAbonne/" + configSpring.userEnCour() + "/" + idAbonne)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                futureInformation.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Boolean result = Boolean.parseBoolean(responseData);
                    futureInformation.complete(result);
                } else {
                    futureInformation.completeExceptionally(new RuntimeException("Request failed"));
                }
            }
        });

        return futureInformation;
    }
}