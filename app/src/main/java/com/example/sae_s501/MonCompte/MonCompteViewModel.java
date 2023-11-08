package com.example.sae_s501.MonCompte;

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


    public MonCompteViewModel() throws ExecutionException, InterruptedException {
    }
    public CompletableFuture<Integer> RequeteCountAbonnement() {
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        SSLSocketFactory sslSocketFactory = null;
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new X509TrustManager[]{trustManager}, null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }

// Créez un client OkHttpClient avec la configuration personnalisée
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustManager) // Ignorer la vérification du certificat
                .hostnameVerifier((hostname, session) -> true) // Ignorer la vérification du nom d'hôte
                .build();

        Request request = new Request.Builder()
                .url("http://10.6.4.184:8080/countAbonnement/102")
                .build();

        CompletableFuture<Integer> futureCount = new CompletableFuture<>();

        Log.d("lamerde777", String.valueOf(request));
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                futureCount.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Integer responseData = Integer.parseInt(response.body().string());
                    Log.d("response", String.valueOf(responseData));
                    futureCount.complete(responseData);
                    Log.d("888888888", String.valueOf(futureCount));
                } else {
                    futureCount.completeExceptionally(new RuntimeException("Request failed"));
                }
            }
        });

        return futureCount;
    }

}
