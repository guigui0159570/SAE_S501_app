package com.example.sae_s501;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sae_s501.authentification.Authentification;
import com.example.sae_s501.model.MonCompte.ConfigSpring;
import com.example.sae_s501.model.MonCompte.FonctionNotificationViewModel;
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.SessionManager;
import com.example.sae_s501.retrofit.UserService;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlinx.coroutines.channels.ActorKt;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AjoutPublication extends AppCompatActivity {

    private FonctionNotificationViewModel FNVM = new FonctionNotificationViewModel(this);
    private static final int PICK_IMAGE_REQUEST =1 ;
    private static final int PICK_FILE_REQUEST =2 ;

    private ConfigSpring configSpring = new ConfigSpring();
    private EditText editTextTitle;
    private CheckBox editCheckbox;
    private CheckBox editCheckboxPrive;
    private EditText editTextPrix;
    private EditText editTextDescription;
    private EditText editTextMotCle;
    private Button buttonPublier;
    private TextView TextViewImage;
    private TextView TextViewUpload;

    private ImageView retour;

    private boolean publiqueCheck = true;


    private boolean imageAdded = false;
    private boolean fileAdded = false;
    private UserService userService;
    private RetrofitService retrofitService;
    private String imagePath;
    private String filePath;
    private MultipartBody.Part filePart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajout_publicationresp);

        editTextTitle = findViewById(R.id.editTextTitle);
        editCheckbox = findViewById(R.id.checkbox_gratuit);
        editTextPrix = findViewById(R.id.checkbox_payant);
        editTextDescription = findViewById(R.id.EditTextDescription);
        buttonPublier = findViewById(R.id.bouton_publier);
        editTextPrix.setVisibility(View.VISIBLE);
        editCheckboxPrive = findViewById(R.id.prive);
        TextViewImage  = findViewById(R.id.charger_img);
        TextViewUpload  = findViewById(R.id.upload);
        editTextMotCle = findViewById(R.id.EditTextMotCle);
        retour = findViewById(R.id.close);

        retrofitService = new RetrofitService(this);


        userService = retrofitService.getRetrofit().create(UserService.class);


        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AjoutPublication.this, MesPublications.class);
                startActivity(intent);
            }
        });
        TextViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Appeler la fonction pour sélectionner une image
                openImage();
            }
        });
        TextViewUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
            }
        });
        // Ajouter un écouteur pour surveiller les changements d'état de la CheckBox
        editCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Mettre à jour la visibilité de l'editTextPrix lorsque l'état de la CheckBox change
                editTextPrix.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            }
        });

        buttonPublier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Récupération des données saisies dans les champs de texte
                String title = editTextTitle.getText().toString();
                String description = editTextDescription.getText().toString();
                String motcle = editTextMotCle.getText().toString();
                List<String> tags = extractTags(motcle);
                float prix = 0;

                if (title.isEmpty()) {
                    showToast("Veuillez saisir un titre pour votre publication.");
                    return;
                } else if (description.isEmpty()) {
                    showToast("Veuillez saisir une description pour votre publication.");
                    return;
                } else if (!(imageAdded)) {
                    showToast("Veuillez saisir une image pour votre publication.");
                    return;
                } else if (!(fileAdded)) {
                    showToast("Veuillez saisir un modèle 3D pour votre publication.");
                    return;
                }
                else if(motcle.isEmpty()){
                    showToast("Veuillez saisir au moins un mot clé avec un #");
                    return;
                }
                if (editCheckboxPrive.isChecked()){
                    publiqueCheck = false;
                }

                // Vérifier si la case à cocher est cochée
                if (editCheckbox.isChecked()) {
                    // Si la case à cocher est cochée, envoyer la requête avec le booléen à true
                    sendPublicationRequest(title, description, true,publiqueCheck, 0.0F,tags);
                } else {
                    // Si la case à cocher n'est pas cochée
                    prix = ConversionFloat(editTextPrix);
                    if (prix == Float.MIN_VALUE) {
                        // Gérer l'erreur de conversion
                        showToast("Veuillez saisir une valeur numérique pour le prix.");
                        return;
                    }
                    // Envoyer la requête avec le booléen à false
                    sendPublicationRequest(title, description, false,publiqueCheck,prix,tags);
                }
            }
        });
    }

    public static List<String> extractTags(String inputText) {
        List<String> tagList = new ArrayList<>();

        // Définir le motif de l'expression régulière pour trouver les tags
        Pattern pattern = Pattern.compile("#(\\w+)");

        // Créer un objet Matcher pour trouver les correspondances dans le texte
        Matcher matcher = pattern.matcher(inputText);

        // Parcourir toutes les correspondances et ajouter les tags à la liste
        while (matcher.find()) {
            String tag = matcher.group(1); // Capturer le contenu entre # et la fin du mot
            tagList.add(tag);
        }

        return tagList;
    }

    private void openImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");  // Tous les types de fichiers
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    // Modification de onActivityResult pour traiter l'image comme un Bitmap
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ImageView photo = findViewById(R.id.imageViewPub);
                photo.setImageBitmap(bitmap);
                imageAdded = true;

                // Utiliser la nouvelle méthode pour obtenir le chemin du fichier
                String filePath = getRealPathFromURI(selectedImage);
                Log.d("Request", "Image Path: " + filePath);
                // Utiliser filePath pour vos opérations ultérieures si nécessaire
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                TextView fichierAjoute = findViewById(R.id.fichierajoute);
                fichierAjoute.setText(R.string.fichierajoute);
                fileAdded = true;
                Bitmap fileBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                filePart = prepareFilePart("file", fileBitmap);
                File file = new File(Uri.parse(data.getDataString()).getPath());
                filePath = file.getAbsolutePath();
                Log.d("FICHIER CHEMIN", filePath);
            } catch (Exception e) {
                e.printStackTrace();
                showToast("Erreur lors de la récupération du chemin du fichier.");
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

    private void sendPublicationRequest(String title, String description, boolean gratuit, boolean publique, float prix,List<String> tags) {
        Bitmap imageBitmap = ((BitmapDrawable) ((ImageView) findViewById(R.id.imageViewPub)).getDrawable()).getBitmap();
        String jwtEmail = SessionManager.getUserEmail(this);

        Log.d("jwtEmail", jwtEmail);
        // Vérifiez si imageBitmap est null
        if (imageBitmap == null) {
            showToast("Erreur : Image non valide.");
            return;
        }

        MultipartBody.Part imagePart = prepareImagePart("image", imageBitmap);

        // Vérifiez si filePath est null
        if (filePath == null) {
            showToast("Erreur : Chemin du fichier non valide.");
            return;
        }

        // Vérifiez si les parties du fichier ne sont pas null
        if (imagePart != null && filePart != null) {

            // Envoi de la requête au serveur avec les données multipart et l'identifiant du propriétaire
            Call<Void> call = userService.createPublication(
                    createRequestBody(title),
                    createRequestBody(description),
                    createRequestBody(String.valueOf(gratuit)),
                    createRequestBody(String.valueOf(publique)),
                    createRequestBody(String.valueOf(prix)),
                    imagePart,
                    filePart,
                    tags,
                    jwtEmail
            );
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        showToast("Publication réussie !");
                        Intent intent = new Intent(AjoutPublication.this, MesPublications.class);
                        startActivity(intent);
                        // Réinitialiser les champs après une publication réussie
                        resetFields();

                        FNVM.allsendNotification(configSpring.userEnCour(AjoutPublication.this.getBaseContext()));
                    } else {
                        showToast("Échec de la publication !");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    if (t instanceof FileNotFoundException) {
                        showToast("Erreur : Le fichier n'a pas pu être ouvert par le serveur.");
                        Log.d("Erreur serv", "onFailure: "+ t.getMessage());
                    } else {
                        showToast("Erreur : " + t.getMessage());
                    }
                }
            });

        } else {
            showToast("Erreur : Une des parties du fichier est null.");
        }
    }


    private float ConversionFloat(EditText editText) {
        try {
            return Float.parseFloat(editText.getText().toString());
        } catch (NumberFormatException e) {
            return Float.MIN_VALUE;
        }
    }



    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void resetFields() {
        editTextTitle.setText("");
        editTextPrix.setText("");
        editTextDescription.setText("");
        editTextPrix.setVisibility(editCheckbox.isChecked() ? View.GONE : View.VISIBLE);
        editCheckbox.setChecked(false);
        editTextMotCle.setText("");
        // Réinitialiser l'affichage de l'image
        ImageView photo = findViewById(R.id.imageViewPub);
        photo.setImageDrawable(null); // Effacer l'image
        imageAdded = false;

        // Réinitialiser l'affichage du fichier 3D
        TextView fichierAjoute = findViewById(R.id.fichierajoute);
        fichierAjoute.setText("");  // Effacer le texte du fichier ajouté
        fileAdded = false;

        // Réinitialiser les variables de chemin de fichier
        filePath = null;
    }

    // Modification de la méthode prepareFilePart pour accepter Bitmap pour les images
    private MultipartBody.Part prepareImagePart(String partName, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageBytes);

        return MultipartBody.Part.createFormData(partName, "image.jpg", requestFile);
    }

    // Modification of the method prepareFilePart to accept Bitmap for files
    private MultipartBody.Part prepareFilePart(String partName, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] fileBytes = byteArrayOutputStream.toByteArray();

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), fileBytes);

        return MultipartBody.Part.createFormData(partName, "file.png", requestFile);
    }
/*
    private MultipartBody.Part prepareFilePart2(String partName, File file) {
        try {
            // Charger le modèle 3D en utilisant les fonctionnalités natives de Java
            byte[] fileBytes = Files.readAllBytes(file.toPath());

            // Déterminer le type de média en fonction de l'extension du fichier
            String mediaType = getMediaType(file.getName());

            // Créer le RequestBody
            RequestBody requestFile = RequestBody.create(MediaType.parse(mediaType), fileBytes);

            // Utiliser le nom du fichier original comme le dernier argument
            return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Gérer les erreurs de chargement de fichier
        }
    }

    private String getMediaType(String fileName) {
        // Logique pour déterminer le type de média en fonction de l'extension du fichier
        // Exemple simple : renvoyer "model/obj" si l'extension est ".obj"
        return "model/obj";
    }*/

  /*  // Créez une MultipartBody.Part à partir d'un fichier
    private MultipartBody.Part prepareFilePart(String partName, File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }
*/


    // Créez un RequestBody à partir d'une chaîne
    private RequestBody createRequestBody(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value);
    }

    // Créez un RequestBody à partir d'une liste de chaînes
    private RequestBody createRequestBodyList(List<String> values) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        for (String value : values) {
            builder.addFormDataPart("tags", value);
            // Remplacez "key" par la clé que vous souhaitez utiliser pour chaque élément de la liste
        }

        return builder.build();
    }
}
