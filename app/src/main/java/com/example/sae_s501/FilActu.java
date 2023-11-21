package com.example.sae_s501;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sae_s501.retrofit.FilActuService;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FilActu extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fil_actualite);
        Log.d(TAG, "page créée");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.21:8080")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();

        Log.d(TAG, "retrofit configuré");

        // Instance de l'interface
        FilActuService filActuService = retrofit.create(FilActuService.class);

        Call<List<Object>> call = filActuService.getAllPublication();
        Log.d(TAG, call.toString());
        call.enqueue(new Callback<List<Object>>() {

            @Override
            public void onResponse(@NonNull Call<List<Object>> call, @NonNull Response<List<Object>> response) {
                Log.d(TAG, "HTTP Code: " + response.code());

                LinearLayout layout = findViewById(R.id.container_pub_fil_actu);
                if (response.isSuccessful()) {
                    List<Object> publications = response.body();
                    if (publications != null) {
                        layout.removeAllViews();
                        for (Object publication : publications) {
                            String publicationString = publication.toString();
                            TextView textView = new TextView(getApplicationContext());
                            textView.setText(publicationString);
                            layout.addView(textView);
                        }
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Error response: " + response.errorBody());
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<List<Object>> call, @NonNull Throwable t) {
                Log.e(TAG, "Failure: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "Erreur lors de la communication avec le serveur", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
