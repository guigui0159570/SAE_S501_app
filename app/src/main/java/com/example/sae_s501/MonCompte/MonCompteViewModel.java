package com.example.sae_s501.MonCompte;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MonCompteViewModel extends ViewModel {

    private ConfigSpring configSpring = new ConfigSpring();

    public MonCompteViewModel() throws ExecutionException, InterruptedException {
    }



    public CompletableFuture<String> RequestInformation() {
        OkHttpClient client = configSpring.creationClientSansSSL();
        ConfigSpring configSpring = new ConfigSpring();
        Request request = new Request.Builder()
                .url("http://"+configSpring.Adresse()+":8080/userInformation/"+configSpring.userEnCour()+"")
                .build();
        CompletableFuture<String> futureInformation = new CompletableFuture<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                futureInformation.completeExceptionally(e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    futureInformation.complete(responseData);
                } else {
                    futureInformation.completeExceptionally(new RuntimeException("Request failed"));
                }
            }
        });
        return futureInformation;
    }

    public Bitmap generateInitialsImage(String initials, int width, int height, int backgroundColor, int textColor) {
        // Créer une image vide avec le fond coloré
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawColor(backgroundColor);

        // Configurer la peinture pour le texte
        Paint paint = new Paint();
        paint.setColor(textColor);
        paint.setTextSize(height * 0.6f); // Taille du texte ajustée

        // Centrer le texte sur l'image
        paint.setTextAlign(Paint.Align.CENTER);
        float x = width / 2.0f;
        float y = height / 2.0f - (paint.descent() + paint.ascent()) / 2.0f;

        // Dessiner les initiales sur l'image
        canvas.drawText(initials, x, y, paint);

        return image;
    }

}
