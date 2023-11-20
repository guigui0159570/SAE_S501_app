package com.example.sae_s501;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sae_s501.model.Publication;
import com.example.sae_s501.model.Utilisateur;
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AjoutPublication extends AppCompatActivity {

    private EditText editTextTitle;
    private CheckBox editCheckbox;
    private EditText editTextPrix;
    private EditText editTextDescription;
    private Button buttonPublier;

    private UserService userService;
    private RetrofitService retrofitService = new RetrofitService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajout_publication);

        editTextTitle = findViewById(R.id.editTextTitle);
        editCheckbox = findViewById(R.id.checkbox_gratuit);
        editTextPrix = findViewById(R.id.checkbox_payant);
        editTextDescription = findViewById(R.id.EditTextDescription);
        buttonPublier = findViewById(R.id.bouton_publier);
        Log.d("BoutonPublier", "Button reference: " + buttonPublier);
        editTextPrix.setVisibility(View.VISIBLE);

        userService = retrofitService.getRetrofit().create(UserService.class);

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
                Log.d("CLICK BOUTTON", "onClick: TEST");
                // Récupération des données saisies dans les champs de texte
                String title = editTextTitle.getText().toString();
                String description = editTextDescription.getText().toString();
                float prix = 0;
                try {
                    prix = Float.parseFloat(editTextPrix.getText().toString());
                } catch (NumberFormatException e) {
                    // Gérer l'exception si la conversion en float échoue
                    showToast("Veuillez saisir une valeur numérique pour le prix.");
                    return; // Sortir de la méthode si la conversion échoue
                }


                if (title.isEmpty()) {
                    showToast("Veuillez saisir un titre pour votre publication.");
                    return;
                }

                if (description.isEmpty()) {
                    showToast("Veuillez saisir une description pour votre publication.");
                    return;
                }
                // Vérifier si la case à cocher est cochée
                if (editCheckbox.isChecked()) {
                    // Si la case à cocher est cochée, envoyer la requête avec le booléen à true
                    sendPublicationRequest(title, description, true, 0);
                } else {
                    // Si la case à cocher n'est pas cochée
                    // Envoyer la requête avec le booléen à false
                    sendPublicationRequest(title, description, false, prix);
                }
            }
        });
    }

    private void sendPublicationRequest(String title, String description, boolean gratuit, float prix) {

        // Envoi de la requête au serveur
        Call<Publication> call = userService.createPublication(title,description,gratuit,prix);

        call.enqueue(new Callback<Publication>() {
            @Override
            public void onResponse(Call<Publication> call, Response<Publication> response) {
                if (response.isSuccessful()) {
                    showToast("Publication réussie !");
                    // Réinitialiser les champs après une publication réussie
                    resetFields();
                } else {
                    showToast("Échec de la publication !");
                }
            }

            @Override
            public void onFailure(Call<Publication> call, Throwable t) {
                showToast("Erreur : " + t.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void resetFields() {
        editTextTitle.setText("");
        editTextPrix.setText("");
        editTextDescription.setText("");
        // Remettre la visibilité par défaut de l'editTextPrix
        editTextPrix.setVisibility(editCheckbox.isChecked() ? View.GONE : View.VISIBLE);
        // Décocher la CheckBox
        editCheckbox.setChecked(false);
    }
}
