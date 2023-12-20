package com.example.sae_s501;

import android.content.Intent;
import android.database.Cursor;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.sae_s501.model.MonCompte.ConfigSpring;

import com.example.sae_s501.model.MonCompte.MonCompteViewModel;
import com.example.sae_s501.databinding.UpdatemoncompteBinding;
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.SessionManager;
import com.example.sae_s501.retrofit.UserService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyUpdateCompteActivity extends AppCompatActivity {


    private static final int PICK_IMAGE_REQUEST =1 ;

    private boolean publiqueCheck = true;

    private UserService userService;
    private boolean imageAdded = false;
    private boolean fileAdded = false;

    private RetrofitService retrofitService;
    private String imagePath;
    private String filePath;

    private UpdatemoncompteBinding binding;
    private ConfigSpring configSpring = new ConfigSpring();

    private Map<Bitmap, String> bitmapTags = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UpdatemoncompteBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        MonCompteViewModel monCompteViewModel = new ViewModelProvider(this).get(MonCompteViewModel.class);
        CompletableFuture<String> stringCompletableFuture = monCompteViewModel.requestInformation(this, configSpring.userEnCour(this));
        informationUserForUpdate(stringCompletableFuture, root);
        Button updatePhoto = root.findViewById(R.id.galerie);

        retrofitService = new RetrofitService(this);


        userService = retrofitService.getRetrofit().create(UserService.class);
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
                Log.d("959565659", "salut1");
                if (!(bitmapTags.containsKey(bitmap) && bitmapTags.get(bitmap).equals("GeneratedByMyApp"))) {
                    sendPhoto();
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
    // Modification de onActivityResult pour traiter l'image comme un Bitmap
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("10100000000", "onActivityResult: ");
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImage);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // Maintenant, vous avez le bitmap original sans compression
                ImageView photo = findViewById(R.id.photo);
                photo.setImageBitmap(bitmap);
                imageAdded = true;

                // Utiliser la nouvelle méthode pour obtenir le chemin du fichier
                String filePath = getRealPathFromURI(selectedImage);
                Log.d("image", "Image Path: " + filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return null;
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(column_index);
        cursor.close();
        return filePath;
    }

    private void sendPhoto() {
        // Récupérer l'image du composant ImageView
        Bitmap imageBitmap = ((BitmapDrawable) ((ImageView) findViewById(R.id.photo)).getDrawable()).getBitmap();

        // Vérifier si l'imageBitmap est null
        if (imageBitmap == null) {
            showToast("Erreur : Image non valide.");
            return;
        }

        // Créer la partie Multipart à partir du Bitmap
        MultipartBody.Part imagePart = prepareImagePart("file", imageBitmap);

        // Vérifier si les parties du fichier ne sont pas null
        if (imagePart != null) {
            // Envoi de la requête au serveur avec les données multipart et l'identifiant du propriétaire
            Call<Void> call = userService.handleFileUpload(imagePart, configSpring.userEnCour(this));
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        showToast("Publication réussie !");
                        Intent intent = new Intent(MyUpdateCompteActivity.this, MyCompteActivity.class);
                        startActivity(intent);
                        // Réinitialiser les champs après une publication réussie
                        resetFields();
                    } else {
                        showToast("Échec de la publication ! Réponse serveur : " + response.message());
                        Log.d("Erreur serveur", "onResponse: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    showToast("Erreur : " + t.getMessage());
                    Log.e("Erreur", "onFailure: " + t.getMessage(), t);
                }
            });
        } else {
            showToast("Erreur : Une des parties du fichier est null.");
        }
    }

// ...

    // Modification de la méthode prepareImagePart pour accepter Bitmap pour les images
    private MultipartBody.Part prepareImagePart(String partName, Bitmap bitmap) {
        // Convertir le Bitmap en tableau d'octets
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Réduisez la qualité de la compression si nécessaire (par exemple, 80 au lieu de 100)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);

        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Créer la RequestBody avec le tableau d'octets
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageBytes);

        // Définir le nom du fichier en ajoutant le préfixe et l'identifiant de l'utilisateur
        String fileName = "imagephotoprofil" + configSpring.userEnCour(this) + ".jpg";

        // Créer la partie Multipart avec le nom du fichier
        return MultipartBody.Part.createFormData(partName, fileName, requestFile);
    }

    private void resetFields() {

        // Réinitialiser l'affichage de l'image
        ImageView photo = findViewById(R.id.photo);
        photo.setImageDrawable(null); // Effacer l'image
        imageAdded = false;
        fileAdded = false;

        // Réinitialiser les variables de chemin de fichier
        filePath = null;
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


                            // Partie Description
                            JsonElement descriptionElement = jsonObject.get("description");
                            TextView textViewDescription = root.findViewById(R.id.description_update);
                            if (!descriptionElement.isJsonNull()) {
                                Log.d("TAG7", "run: ");
                                String Description = descriptionElement.getAsString();
                                String decodedDescription = URLDecoder.decode(Description, StandardCharsets.UTF_8.toString());
                                textViewDescription.setText(decodedDescription);
                            }

                            //Partie photo
                            JsonElement photoElement = jsonObject.get("photo");
                            ImageView imageViewPhoto = root.findViewById(R.id.photo);

                            if (photoElement != null && !photoElement.isJsonNull()) {
                                String photoElementAsString = photoElement.getAsString();
                                MonCompteViewModel monCompteViewModel = new MonCompteViewModel();
                                monCompteViewModel.Imageprofil(getBaseContext(),imageViewPhoto, photoElementAsString);
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



    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }



    public void changeProfilText(String description, String pseudo){
        RetrofitService retrofitService = new RetrofitService(this);
        UserService userService = retrofitService.getRetrofit().create(UserService.class);


        ConfigSpring configSpring = new ConfigSpring();
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("description", description);
        requestBody.put("pseudo", pseudo);
        // Créer la requête
        Call<Void> call = userService.envoyerString(configSpring.userEnCour(this),requestBody);
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

