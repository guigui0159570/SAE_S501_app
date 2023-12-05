package com.example.sae_s501;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.sae_s501.MonCompte.ConfigSpring;
import com.example.sae_s501.MonCompte.MonCompteViewModel;
import com.example.sae_s501.MonCompte.UpdateUserService;
import com.example.sae_s501.databinding.AjoutPublicationBinding;
import com.example.sae_s501.databinding.MoncompteBinding;
import com.example.sae_s501.databinding.UpdatemoncompteBinding;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyUpdateCompteActivity extends AppCompatActivity {
    private UpdatemoncompteBinding binding;
    private ConfigSpring configSpring = new ConfigSpring();

    private static final int PICK_IMAGE_REQUEST = 1;
    private Map<Bitmap, String> bitmapTags = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UpdatemoncompteBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        MonCompteViewModel monCompteViewModel = new ViewModelProvider(this).get(MonCompteViewModel.class);
        CompletableFuture<String> stringCompletableFuture = monCompteViewModel.RequestInformation();
        informationUserForUpdate(stringCompletableFuture, root);
        Button updatePhoto = root.findViewById(R.id.galerie);
        updatePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        Button validation = root.findViewById(R.id.Enregistrer);
        validation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView photo = binding.getRoot().findViewById(R.id.photo);
                Drawable drawable = photo.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (!(bitmapTags.containsKey(bitmap) && bitmapTags.get(bitmap).equals("GeneratedByMyApp"))) {
                    uploadImage();
                    Log.d("959565659", "salut");
                }
                EditText editTextdescription = root.findViewById(R.id.description_update);
                EditText editTextpseudo = root.findViewById(R.id.mon_compteupdate);
                changeProfilText(String.valueOf(editTextdescription.getText()),String.valueOf(editTextpseudo.getText()));
                Handler handler = new Handler();

                long delayMillis = 1000;

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent profilview = new Intent(view.getContext(), MyCompteActivity.class);
                        startActivity(profilview);
                        showToast("Votre profil à bien été mis à jour");
                    }
                }, delayMillis);
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            ImageView photo = binding.getRoot().findViewById(R.id.photo);
            photo.setImageURI(selectedImage);
        }
    }

    public void informationUserForUpdate(CompletableFuture<String> integerCompletableFuture, View root){
        Log.d("33333", "run: ");
        integerCompletableFuture.thenAccept(resultat -> {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("2222222", "run: ");
                            JsonParser parser = new JsonParser();
                            JsonObject jsonObject = parser.parse(resultat).getAsJsonObject();

                            //Partie photo
                            JsonElement photoElement = jsonObject.get("photo");
                            ImageView imageViewPhoto = root.findViewById(R.id.photo);


                            //Partie pseudo
                            JsonElement pseudoElement = jsonObject.get("pseudo");
                            EditText textViewPseudo = root.findViewById(R.id.mon_compteupdate);
                            String pseudo = String.valueOf(pseudoElement).replaceAll("^\"|\"$", "");
                            textViewPseudo.setText(pseudo);
                            TooltipCompat.setTooltipText(textViewPseudo, pseudo);
                            textViewPseudo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            });

                            //Partie description
                            JsonElement descriptionElement = jsonObject.get("description");
                            EditText editTextdescription = root.findViewById(R.id.description_update);
                            String description = String.valueOf(descriptionElement).replaceAll("^\"|\"$", "");
                            editTextdescription.setText(description);

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
                                bitmapTags.put(generatedImage, "GeneratedByMyApp");
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


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void uploadImage() {
        ImageView photo = binding.getRoot().findViewById(R.id.photo);
        Drawable drawable = photo.getDrawable();
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        }

        if (bitmap != null) {
            UploadImageTask uploadTask = new UploadImageTask();
            uploadTask.execute(bitmap);
        }else {
            Log.d("77777", "uploadImage: ");
        }
    }

    private class UploadImageTask extends AsyncTask<Bitmap, Void, String> {

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            if (bitmaps.length == 0 || bitmaps[0] == null) {
                return null;
            }

            Bitmap bitmap = bitmaps[0];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] byteArray = baos.toByteArray();
            try {
                URL url = new URL("http://"+configSpring.Adresse()+":8080/upload/"+configSpring.userEnCour()+"");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                    outputStream.writeBytes("--" + boundary + "\r\n");
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"image.png\"\r\n");
                    outputStream.writeBytes("Content-Type: image/png\r\n\r\n");
                    outputStream.write(byteArray);
                    outputStream.writeBytes("\r\n");
                    outputStream.writeBytes("--" + boundary + "--\r\n");
                }
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // La requête a été réussie
                    // Vous pouvez lire la réponse du serveur ici si nécessaire
                    try (InputStream inputStream = connection.getInputStream();
                         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        return response.toString();
                    }
                } else {
                    // La requête a échoué
                    return "Erreur lors du téléchargement de l'image";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Erreur lors du téléchargement de l'image";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("UploadImageTask", result);
        }
    }

    public void changeProfilText(String description, String pseudo){
        Log.d("d7d7d7d7d7d7d", description);

        // Initialiser Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://"+configSpring.Adresse()+":8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Créer le service Retrofit
        UpdateUserService updateUserService = retrofit.create(UpdateUserService.class);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("description", description);
        requestBody.put("pseudo", pseudo);
        // Créer la requête
        Call<Void> call = updateUserService.envoyerString(configSpring.userEnCour(),requestBody);

        // Effectuer la requête
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Gérer la réponse du serveur ici
                if (response.isSuccessful()) {
                    Log.d("888", "OK");
                } else {
                    Log.d("777", "erreur");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Gérer l'échec de la requête ici
            }
        });
    }
}

