package com.example.sae_s501;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import com.example.sae_s501.model.Utilisateur;
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.RetrofitServiceRegister;
import com.example.sae_s501.retrofit.UserService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Inscription extends AppCompatActivity {
    private EditText editTextPseudo;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmationPassword;
    private Button buttonEnvoyer;
    private UserService userService;
    private RetrofitServiceRegister retrofitService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscription);
        editTextPseudo = findViewById(R.id.editpseudo);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmationPassword = findViewById(R.id.editTextPasswordConfirm);
        buttonEnvoyer = findViewById(R.id.btnInscription);
        retrofitService = new RetrofitServiceRegister();

        /* creation requete */
        userService = retrofitService.getRetrofit().create(UserService.class);
        buttonEnvoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Récupération des données saisies dans les champs de texte
                String pseudo = editTextPseudo.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirmationPassword = editTextConfirmationPassword.getText().toString();

                // mot de passe critères

                // Vérifier si le mot de passe et le mot de passe de confirmation correspondent
                if (!password.equals(confirmationPassword)) {
                    showToast("Les mots de passe ne correspondent pas.");
                    return;
                }

                if (password.length() < 8) {
                    showToast("Le mot de passe doit contenir au moins 8 caractères.");
                    return;
                }

                if (!MDPCharacterSpe(password)) {
                    showToast("Le mot de passe doit contenir au moins un caractère spécial.");
                    return;
                }

                if (!MotDePasseMaj(password)) {
                    showToast("Le mot de passe doit contenir au moins une majuscule.");
                    return;
                }
                if (!MDPChiffre(password)) {
                    showToast("Le mot de passe doit contenir au moins un chiffre.");
                    return;
                }



                /* envoie requete */
                registerUser(pseudo,email,password);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Vérifier si le mot de passe contient au moins un caractère spécial
    private boolean MDPCharacterSpe(String password) {
        String specialCharacters = "!@#$%^&*()-_=+[]{}|;:'\",.<>/?";
        for (char specialChar : specialCharacters.toCharArray()) {
            if (password.contains(String.valueOf(specialChar))) {
                return true;
            }
        }
        return false;
    }

    // Vérifier si le mot de passe contient une majuscule
    private boolean MotDePasseMaj(String password) {
        for (char character : password.toCharArray()) {
            if (Character.isUpperCase(character)) {
                return true;
            }
        }
        return false;
    }
    // Vérifier si le mot de passe contient au moins un chiffre
    private boolean MDPChiffre(String password) {
        for (char character : password.toCharArray()) {
            if (Character.isDigit(character)) {
                return true;
            }
        }
        return false;
    }

    private void registerUser(String pseudo, String email, String password){
        Call<Utilisateur> call = userService.registerUser(pseudo, email, password);
        call.enqueue(new Callback<Utilisateur>() {
            /* resultat de la requete */
            @Override
            public void onResponse(@NonNull Call<Utilisateur> call, @NonNull Response<Utilisateur> response) {
                if (response.isSuccessful()) {
                    showToast("Inscription réussie !");
                    Intent intent = new Intent(Inscription.this, Connexion.class);
                    startActivity(intent);
                    // Réinitialiser les champs, que l'inscription soit réussie ou non
                    editTextPseudo.setText("");
                    editTextEmail.setText("");
                    editTextPassword.setText("");
                    editTextConfirmationPassword.setText("");
                } else {
                    // Handle different HTTP error codes with appropriate error messages
                    if (response.code() == 400) {
                        showToast("Erreur de requête : Vérifiez les données saisies.");
                    } else if (response.code() == 401) {
                        showToast("Erreur d'authentification : Accès non autorisé.");
                    }else if (response.code() == 404) {
                        showToast("Erreur : Cette adresse mail est déjà utilisée !");
                    } else if (response.code() == 409) {
                        showToast("Conflit : L'utilisateur existe déjà.");
                    } else if (response.code() == 500) {
                        showToast("Erreur interne du serveur : Réessayez plus tard.");
                    } else {
                        try {
                            // Extract the error response body
                            String errorBody = response.errorBody().string();
                            showToast("Erreur inattendue : " + errorBody);
                            Log.d("ERREUR INSCRIPTION", errorBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                            showToast("Erreur inattendue : Impossible de lire la réponse d'erreur.");
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Utilisateur> call, @NonNull Throwable t) {
                showToast("Erreur : " + t.getMessage());
            }
        });
    }
}
