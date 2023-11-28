package com.example.sae_s501.authentification;


import com.example.sae_s501.Dictionnaire;
import com.example.sae_s501.model.UserEmailResponse;
import com.example.sae_s501.retrofit.AuthService;
import com.example.sae_s501.retrofit.SessionManager;
import com.example.sae_s501.retrofit.TokenValidationCallback;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Authentification {
    private static final String BASE_URL = Dictionnaire.getIpAddress();

    // Méthode pour effectuer l'authentification avec identifiants
    public static void authenticateWithCredentials(String username, String password, Context context, AuthCallback authCallback) {
        AuthService authService = createAuthService();
        Call<JwtResponse> call = authService.authenticate(new LoginRequest(username, password));

        call.enqueue(new Callback<JwtResponse>() {
            @Override
            public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                if (response.isSuccessful()) {
                    JwtResponse jwtResponse = response.body();

                    // Stocker le jeton dans SharedPreferences
                    SessionManager.saveJwtToken(context, jwtResponse.getToken());
                    SessionManager.saveUserEmail(context, jwtResponse.getEmail());
                    Toast.makeText(context, "Connexion réussie !", Toast.LENGTH_SHORT).show();
                    Log.d("Connexion token", jwtResponse.getToken());
                    Log.d("Connexion token shared", SessionManager.getJwtToken(context) + "");

                    // Utiliser le jeton pour les futures requêtes
                    if (authCallback != null) {
                        authCallback.onAuthSuccess();
                    }
                } else {
                    // Gérer les erreurs d'authentification
                    if (authCallback != null) {
                        authCallback.onAuthError(response.message());
                        Toast.makeText(context, "Erreur d'authentification: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<JwtResponse> call, Throwable t) {
                // Gérer les échecs de connexion
                if (authCallback != null) {
                    authCallback.onAuthError(t.getMessage());
                }
            }
        });
    }

    // Méthode pour effectuer des requêtes authentifiées avec le jeton
    public static OkHttpClient createAuthenticatedClient(Context context) {
        String jwtToken = SessionManager.getJwtToken(context);
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request originalRequest = chain.request();

                    // Ajouter le jeton dans l'en-tête "Authorization"
                    Request newRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer " + jwtToken)
                            .build();

                    return chain.proceed(newRequest);
                })
                .build();

    }

    // Méthode pour créer le service Retrofit pour l'authentification
    private static AuthService createAuthService() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(AuthService.class);
    }
    public static AuthService createAuthServiceToken(Context context) {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(Authentification.createAuthenticatedClient(context))
                .build();

        return retrofit.create(AuthService.class);
    }

    // Interface pour gérer les callbacks d'authentification
    public interface AuthCallback {
        void onAuthSuccess();
        void onAuthError(String errorMessage);
    }

    public static void validateToken(Context context, String token, TokenValidationCallback callback) {
        AuthService authService = createAuthServiceToken(context);
        Call<ResponseBody> call = authService.validateToken();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Le token est valide, gestion ici
                    callback.onTokenValidated(true);
                } else {
                    // Le token n'est pas valide, gestion ici
                    Log.e("TokenValidation", "Invalid token");
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            // Gérer l'objet JSON d'erreur ici
                            Log.e("ErrorBody", errorBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    callback.onTokenValidated(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Gérer l'échec de la requête
                Log.e("TokenValidation", "Request failed", t);
                callback.onTokenValidated(false);
            }
        });
    }

}
