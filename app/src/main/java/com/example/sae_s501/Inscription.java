package com.example.sae_s501;


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
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.UserService;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.internal.Util;
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
    private RetrofitService retrofitService = new RetrofitService();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscription);
        editTextPseudo = findViewById(R.id.editpseudo);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmationPassword = findViewById(R.id.editTextPasswordConfirm);
        buttonEnvoyer = findViewById(R.id.btnInscription);



        userService = retrofitService.getRetrofit().create(UserService.class);
        buttonEnvoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Récupération des données saisies dans les champs de texte
                String pseudo = editTextPseudo.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirmationPassword = editTextConfirmationPassword.getText().toString();



                if (password.equals(confirmationPassword)) {
                    Utilisateur user = new Utilisateur(pseudo, email, password);


                    Call<Utilisateur> call = userService.registerUser(user);

                    call.enqueue(new Callback<Utilisateur>() {
                        @Override
                        public void onResponse(Call<Utilisateur> call, Response<Utilisateur> response) {
                            if (response.isSuccessful()) {
                                showToast("Inscription réussie !");
                            } else {
                                showToast("Échec de l'inscription : ");
                            }
                        }
                        @Override
                        public void onFailure(Call<Utilisateur> call, Throwable t) {
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
