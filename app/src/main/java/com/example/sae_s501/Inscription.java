package com.example.sae_s501;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.example.sae_s501.model.UserRegistrationResponse;
import com.example.sae_s501.model.Utilisateur;
import com.example.sae_s501.retrofit.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Inscription extends AppCompatActivity {


    private EditText editTextPseudo;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmationPassword;
    private Button buttonEnvoyer;

    private UserService userService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscription);

        editTextPseudo = findViewById(R.id.editpseudo);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmationPassword = findViewById(R.id.editTextPasswordConfirm);
        buttonEnvoyer = findViewById(R.id.btnInscription);

        // Create a Retrofit client instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.6.2.252:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userService = retrofit.create(UserService.class);
        buttonEnvoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Récupération des données saisies dans les champs de texte
                String pseudo = editTextPseudo.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirmationPassword = editTextConfirmationPassword.getText().toString();

                // Encodez les valeurs des paramètres
                pseudo = Uri.encode(pseudo);
                email = Uri.encode(email);
                password = Uri.encode(password);
                confirmationPassword = Uri.encode(confirmationPassword);



                if (password.equals(confirmationPassword)) {
                    Utilisateur user = new Utilisateur(pseudo, email, password);


                    Call<UserRegistrationResponse> call = userService.registerUser(user);

                    call.enqueue(new Callback<UserRegistrationResponse>() {
                        @Override
                        public void onResponse(Call<UserRegistrationResponse> call, Response<UserRegistrationResponse> response) {
                                if (response.isSuccessful()) {
                                    // Enregistrement réussi, traitez la réponse
                                    UserRegistrationResponse registrationResponse = response.body();
                                    if (registrationResponse != null && registrationResponse.isSuccess()) {
                                        // Enregistrement réussi, effectuez les actions nécessaires (par exemple, redirigez vers l'écran de connexion)
                                        showToast("Inscription réussie !");
                                        // Ajoutez ici le code pour rediriger l'utilisateur vers la page de connexion, par exemple.
                                    } else {
                                        // Échec de l'enregistrement, affichez le message d'erreur de l'API.
                                        showToast("Échec de l'inscription : " + registrationResponse.getMessage());
                                    }
                                } else {
                                    // Réponse non réussie (code HTTP autre que 2xx)
                                    showToast("Erreur lors de la demande : " + response.code());
                                }
                            }

                        @Override
                        public void onFailure(Call<UserRegistrationResponse> call, Throwable t) {
                            // Erreur de réseau ou d'analyse de la réponse
                            showToast("Erreur : " + t.getMessage());
                        }
                    });
                }
            }
        });
    }

        private void showToast(String message) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
