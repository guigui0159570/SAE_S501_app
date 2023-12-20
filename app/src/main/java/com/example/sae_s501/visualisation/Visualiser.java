package com.example.sae_s501.visualisation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sae_s501.R;
import com.example.sae_s501.retrofit.FilActuService;
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.visualisation.MyGLSurfaceView;

import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Visualiser extends AppCompatActivity {
    private MyGLSurfaceView gLView;
    private RetrofitService retrofitService;
    private float zoomFactor = 1.0f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visualiser);
        retrofitService = new RetrofitService(Visualiser.this);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        FilActuService filActuService = retrofitService.getRetrofit().create(FilActuService.class);
        String fichier = getIntent().getStringExtra("fichier");
        Log.d("Fichier", fichier);
        Call<ResponseBody> callFichier = filActuService.getFichier(fichier);

        callFichier.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        InputStream inputStream = body.byteStream();
                        Log.d("MODEL", "onResponse - Successful response");
                        ViewGroup layout = findViewById(R.id.modelLayout);
                        gLView = new MyGLSurfaceView(Visualiser.this, inputStream);
                        gLView.setLayoutParams(new FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                                ));
                        layout.addView(gLView);

                        Button buttonZoomIn = findViewById(R.id.buttonplus);
                        Button buttonZoomOut = findViewById(R.id.buttonmoins);

                        // Ajoutez des écouteurs d'événements pour les boutons de zoom
                        buttonZoomIn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                zoomFactor *= 1.2f; // Augmentez le facteur de zoom
                                gLView.setZoomFactor(zoomFactor);
                                gLView.requestRender();
                            }
                        });

                        buttonZoomOut.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                zoomFactor /= 1.2f; // Diminuez le facteur de zoom
                                gLView.setZoomFactor(zoomFactor);
                                gLView.requestRender();
                            }
                        });
                    }
                } else {
                    Log.e("MODEL", "Erreur lors de la récupération du modèle 3D. Code de réponse : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Gestion des erreurs
                Log.e("MODEL", "Échec de la requête pour récupérer le modèle 3D : " + t.getMessage());
            }
        });
    }

    private void render3DModel(InputStream modelInputStream) {
        // Initialize and configure your OpenGL ES view
        gLView.setEGLContextClientVersion(2); // Version OpenGL ES 2.0
        gLView.setRenderer(new MyGLRenderer(modelInputStream)); // Use your own OpenGL ES rendering class
    }
}
