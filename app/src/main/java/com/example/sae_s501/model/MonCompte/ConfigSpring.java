package com.example.sae_s501.model.MonCompte;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.example.sae_s501.retrofit.FilActuService;
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.SessionManager;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfigSpring {

    public static int couleurDefault;

    public ConfigSpring() {
        couleurDefault = couleurProfilPhoto();
    }

    public String Adresse(){
        return "10.6.2.252";
    }


    public Long userEnCour(Context context) {
        Log.d("USER ID", SessionManager.getUserId(context).toString());
        return SessionManager.getUserId(context);
    }


        public int couleurProfilPhoto (){



        int[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.GRAY, Color.CYAN};

        // Choix aléatoire d'une couleur
        int randomIndex = (int) (Math.random() * colors.length);
        int backgroundColor = colors[randomIndex];
        return backgroundColor;

    }

    public OkHttpClient creationClientSansSSL(){
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
        return client;
    }

}
