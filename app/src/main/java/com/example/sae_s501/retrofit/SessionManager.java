package com.example.sae_s501.retrofit;

import android.content.AttributionSource;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.sae_s501.AjoutPublication;
import com.example.sae_s501.Connexion;
import com.example.sae_s501.Inscription;
import com.example.sae_s501.authentification.Authentification;
import com.example.sae_s501.authentification.JwtResponse;
import com.example.sae_s501.authentification.LoginRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SessionManager {

    private static final String SESSION_PREFS_NAME = "session_prefs";
    private static final String KEY_JWT_TOKEN = "jwt_token";

    public static void isSessionValid(Context context) {
        String jwtToken = SessionManager.getJwtToken(context);
        if (jwtToken != null && !jwtToken.isEmpty()) {
            Log.d("Verification token", jwtToken);
            // Le token JWT existe, vérifier sa validité côté serveur
            Authentification.validateToken(context, jwtToken, new TokenValidationCallback() {
                @Override
                public void onTokenValidated(boolean isValid) {
                    // Utiliser la valeur de validation ici
                    if (isValid) {
                        // Le token est valide
                        // Faites ce que vous devez faire ici
                        Intent intent = new Intent(context, AjoutPublication.class);
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, Connexion.class);
                        context.startActivity(intent);
                    }
                }
            });
        } else {
            // Aucun token JWT n'est présent
            Intent intent = new Intent(context, Connexion.class);
            context.startActivity(intent);
        }
    }

    // Vous pouvez ajouter d'autres méthodes pour gérer le stockage et la récupération du JWT token au besoin
    public static void saveJwtToken(Context context, String jwtToken) {
        SharedPreferences preferences = context.getSharedPreferences(SESSION_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_JWT_TOKEN, jwtToken);
        editor.apply();
    }

    public static void deleteToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SESSION_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_JWT_TOKEN, null);
        editor.apply();
    }

    public static String getJwtToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SESSION_PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_JWT_TOKEN, null);
    }
}
