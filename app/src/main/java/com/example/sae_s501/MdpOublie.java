package com.example.sae_s501;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sae_s501.retrofit.AuthService;
import com.example.sae_s501.retrofit.RetrofitServiceRegister;
import com.example.sae_s501.retrofit.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MdpOublie  extends AppCompatActivity {
    private RetrofitServiceRegister retrofitService;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdp_oublier);
        TextView retourLogin = findViewById(R.id.retour);
        EditText email = findViewById(R.id.editTextEmail);
        EditText emailConfirm = findViewById(R.id.editTextEmail2);


        retourLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Connexion.class);
                startActivity(intent);
            }
        });
        Button sendEmailButton = findViewById(R.id.sendEmail);

        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailVerif = email.getText().toString();
                String emailVerif2 = emailConfirm.getText().toString();

                Log.d("Email", emailVerif + " " + emailVerif2);
                    if (emailVerif.equals(emailVerif2)) {
                        sendEmail(emailVerif);

                    } else {
                        showToast("Les adresses e-mail ne correspondent pas ou sont vides !");
                    }

                }
        });
    }

    private void sendEmail(String email) {
        retrofitService=new RetrofitServiceRegister();
        authService = retrofitService.getRetrofit().create(AuthService.class);
        Call<Void> call = authService.resetPassword(email);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showToast("Réinitialisation réussie. Vérifiez votre e-mail pour les instructions.");
                    Intent intent = new Intent(getBaseContext(), Connexion.class);
                    startActivity(intent);
                } else {
                    if (response.code() == 404) {
                        showToast("L'utilisateur avec cet e-mail n'existe pas.");
                    } else {
                        showToast("Échec de la réinitialisation. Veuillez réessayer plus tard.");
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showToast("Erreur lors de la connexion au serveur. Veuillez vérifier votre connexion Internet.");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
