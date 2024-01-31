package com.example.sae_s501.retrofit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.sae_s501.activity.ConnexionActivity;
import com.example.sae_s501.activity.MesPublicationsActivity;
import com.example.sae_s501.authentification.Authentification;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionManager {

    private static final String SESSION_PREFS_NAME = "session_prefs";
    private static final String KEY_JWT_TOKEN = "jwt_token";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ID = "user_id";

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
                        retrieveUserId(context);
                        Intent intent = new Intent(context, MesPublicationsActivity.class);
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, ConnexionActivity.class);
                        context.startActivity(intent);
                    }
                }
            });
        } else {
            // Aucun token JWT n'est présent
            Log.e("JWT Token", "Non présent");
            Intent intent = new Intent(context, ConnexionActivity.class);
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
        editor.putString(KEY_USER_ID, null);
        editor.putString(KEY_JWT_TOKEN, null);
        editor.apply();
    }

    public static String getJwtToken(Context context) {
        if (context != null){
            SharedPreferences preferences = context.getSharedPreferences(SESSION_PREFS_NAME, Context.MODE_PRIVATE);
            return preferences.getString(KEY_JWT_TOKEN, null);
        }else {
            return null;
        }
    }
    public static void saveUserEmail(Context context, String userEmail) {
        SharedPreferences preferences = context.getSharedPreferences(SESSION_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.apply();
    }
    public static String getUserEmail(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SESSION_PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_USER_EMAIL, null);
    }

    public static void retrieveUserId(Context context) {
        RetrofitService retrofitService = new RetrofitService(context);
        FilActuService filActuService = retrofitService.getRetrofit().create(FilActuService.class);

        String jwtEmail = SessionManager.getUserEmail(context);

        Call<Long> callUserId = filActuService.getUtilisateurIdByEmail(jwtEmail);
        callUserId.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful()) {
                    Long userId = response.body();
                    if (userId != null) {
                        Log.d("ID UTILISATEUR", userId.toString());
                        saveUserId(context, userId);
                    } else {
                        Log.d("ID UTILISATEUR", "L'ID utilisateur reçu est null");
                    }
                } else {
                    Log.d("ID UTILISATEUR", "Échec de la requête : " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.d("ID UTILISATEUR", "onFailure: pas récupéré ");
                Log.d("ID UTILISATEUR", "Erreur de réseau : " + t.getMessage());
            }
        });
    }

    public static void saveUserId(Context context, Long userId) {
        SharedPreferences preferences = context.getSharedPreferences(SESSION_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(KEY_USER_ID, userId);
        editor.apply();
    }

    public static Long getUserId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SESSION_PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getLong(KEY_USER_ID, -1); // Return -1 if not found (you can choose another default value)
    }

}
