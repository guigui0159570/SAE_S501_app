package com.example.sae_s501;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sae_s501.retrofit.PanierService;
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.SessionManager;
import com.example.sae_s501.retrofit.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Panier extends AppCompatActivity {
    private PanierService panierService;
    private RetrofitService retrofitService;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panierresp);
        String jwtEmail = SessionManager.getUserEmail(this);


        getPrixByUtiId(jwtEmail); /* recupere prix du panier */

        // Ajoutez le fragment publication
        PublicationPanier panier = new PublicationPanier();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_panier, panier)
                .commit();
        Log.d(TAG, "fragment publications panier");

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ImageView retour_panier = findViewById(R.id.close);

        retour_panier.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Panier.this, MyCompteActivity.class);
                startActivity(intent);
            }
        });
    }

    // Fonction pour récupérer le prix par ID utilisateur
    private void getPrixByUtiId(String email) {

        retrofitService = new RetrofitService(this);
        panierService = retrofitService.getRetrofit().create(PanierService.class);

        Call<Float> call = panierService.getPrixByUtiId(email);

        call.enqueue(new Callback<Float>() {
            @Override
            public void onResponse(Call<Float> call, Response<Float> response) {
                if (response.isSuccessful()) {
                    Float prix = response.body();
                    if (prix != null) {
                        updateTextView(prix);
                    } else {
                        showToast("Erreur: Réponse nulle");                    }
                } else {
                    showToast("Erreur de requête: " + response.code());                }
            }

            @Override
            public void onFailure(Call<Float> call, Throwable t) {
                showToast("Erreur de réseau: " + t.getMessage());            }
        });
    }

    // Fonction pour mettre à jour la valeur de TextView du prix
    private void updateTextView(Float prix) {
        TextView coutTotalTextView = findViewById(R.id.cout_total);
        if (prix != null) {
            String prixString = String.format("%.2f", prix);
            coutTotalTextView.setText(prixString + " €");
        } else {
            coutTotalTextView.setText("Erreur: Prix nul");
            showToast("Erreur: Prix nul");
        }
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
