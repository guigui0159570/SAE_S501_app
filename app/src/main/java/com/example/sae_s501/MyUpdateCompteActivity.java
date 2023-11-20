package com.example.sae_s501;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.sae_s501.MonCompte.ConfigSpring;
import com.example.sae_s501.MonCompte.MonCompteViewModel;
import com.example.sae_s501.databinding.MoncompteBinding;
import com.example.sae_s501.databinding.UpdatemoncompteBinding;
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
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

public class MyUpdateCompteActivity extends AppCompatActivity {
    private UpdatemoncompteBinding binding;

    private static final int PICK_IMAGE_REQUEST = 1;

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

                uploadImage();
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
        integerCompletableFuture.thenAccept(resultat -> {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JsonParser parser = new JsonParser();
                            JsonObject jsonObject = parser.parse(resultat).getAsJsonObject();

                            //Partie photo
                            JsonElement photoElement = jsonObject.get("photo");

                            if (photoElement != null && !photoElement.isJsonNull()) {
                                String base64ImageData = photoElement.getAsString();

                                // Décodez la chaîne Base64 en un tableau de bytes
                                byte[] decodedImageData = Base64.getDecoder().decode(base64ImageData);

                                // Convertir le tableau de bytes en un Bitmap
                                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImageData, 0, decodedImageData.length);

                                // Afficher le Bitmap dans l'ImageView
                                ImageView imageViewPhoto = root.findViewById(R.id.photo);
                                imageViewPhoto.setImageBitmap(bitmap);
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
            Log.d("999999", "uploadImage: ");
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

                ConfigSpring configSpring = new ConfigSpring();
                URL url = new URL("http://"+configSpring.Adresse()+":8080/upload/102");
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
}

