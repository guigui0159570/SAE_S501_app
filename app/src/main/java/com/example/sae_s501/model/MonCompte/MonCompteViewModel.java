package com.example.sae_s501.MonCompte;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.lifecycle.ViewModel;

import com.example.sae_s501.model.Utilisateur;
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.UserService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MonCompteViewModel extends ViewModel {

    private ConfigSpring configSpring = new ConfigSpring();

    public MonCompteViewModel() throws ExecutionException, InterruptedException {
    }

    public CompletableFuture<String> requestInformation(Context context, Long userId) {
        RetrofitService retrofitService = new RetrofitService(context);
        UserService userService = retrofitService.getRetrofit().create(UserService.class);

        ConfigSpring configSpring = new ConfigSpring();
        Call<Map<String, String>> call = userService.RequestInformationUser(userId);

        CompletableFuture<String> futureInformation = new CompletableFuture<>();
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    String responseData = String.valueOf(response.body());
                    futureInformation.complete(responseData);
                } else {
                    futureInformation.completeExceptionally(new RuntimeException("Request failed with code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                if (t instanceof FileNotFoundException) {
                    futureInformation.completeExceptionally(t);
                } else {
                    futureInformation.completeExceptionally(new RuntimeException("Request failed", t));
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

    public void Imageprofil(Context context, ImageView view, String photoElement) {
        RetrofitService retrofitService = new RetrofitService(context);
        UserService userService = retrofitService.getRetrofit().create(UserService.class);

        Call<ResponseBody> callImage = userService.getImageProfil(photoElement);

        callImage.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    Log.d("IMAGE", String.valueOf(body));
                    if (body != null) {
                        try {
                            InputStream inputStream = body.byteStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            // Afficher l'image sans redimensionnement
                            view.setImageBitmap(bitmap);

                        } catch (Exception e) {
                            Log.e("IMAGE", "Erreur lors de la manipulation de l'image : " + e.getMessage());
                        }
                    }
                } else {
                    Log.e("IMAGE", "Erreur lors de la récupération de l'image. Code de réponse : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Gestion des erreurs
                Log.e("IMAGE", "Échec de la requête pour récupérer l'image : " + t.getMessage());
            }
        });
    }

}
