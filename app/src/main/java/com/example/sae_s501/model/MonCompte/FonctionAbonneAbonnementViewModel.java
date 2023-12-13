package com.example.sae_s501.MonCompte;

import android.content.Context;
import android.util.Log;

import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.UserService;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FonctionAbonneAbonnementViewModel {

    private Context context;

    public FonctionAbonneAbonnementViewModel(Context context) {
        this.context = context;
    }

    public CompletableFuture<String> RequestInformationAbonne() {
        RetrofitService retrofitService = new RetrofitService(context);
        UserService userService = retrofitService.getRetrofit().create(UserService.class);


        ConfigSpring configSpring = new ConfigSpring();
        Call<List<Map>> call = userService.getAbonneUtilisateur(configSpring.userEnCour());
        CompletableFuture<String> futureInformation = new CompletableFuture<>();
        call.enqueue(new Callback<List<Map>>() {
            @Override
            public void onResponse(Call<List<Map>> call, Response<List<Map>> response) {
                if (response.isSuccessful()) {
                    String responseData = String.valueOf(response.body());
                    futureInformation.complete(responseData);
                } else {
                    futureInformation.completeExceptionally(new RuntimeException("Request failed"));
                }
            }

            @Override
            public void onFailure(Call<List<Map>> call, Throwable t) {
                if (t instanceof FileNotFoundException) {
                    futureInformation.completeExceptionally(t);
                } else {
                    futureInformation.completeExceptionally(new RuntimeException("Request failed"));
                }
            }
        });
        return futureInformation;
    }


    public CompletableFuture<String> RequestInformationAbonnement() {
        RetrofitService retrofitService = new RetrofitService(context);
        UserService userService = retrofitService.getRetrofit().create(UserService.class);


        ConfigSpring configSpring = new ConfigSpring();
        Call<List<Map>> call = userService.getAbonnementUtilisateur(configSpring.userEnCour());
        CompletableFuture<String> futureInformation = new CompletableFuture<>();
        call.enqueue(new Callback<List<Map>>() {
            @Override
            public void onResponse(Call<List<Map>> call, Response<List<Map>> response) {
                if (response.isSuccessful()) {
                    String responseData = String.valueOf(response.body());
                    futureInformation.complete(responseData);
                } else {
                    futureInformation.completeExceptionally(new RuntimeException("Request failed"));
                }
            }

            @Override
            public void onFailure(Call<List<Map>> call, Throwable t) {
                if (t instanceof FileNotFoundException) {
                    futureInformation.completeExceptionally(t);
                } else {
                    futureInformation.completeExceptionally(new RuntimeException("Request failed"));
                }
            }
        });
        return futureInformation;
    }


    public void deleteAbonneOrAbonnement(Long abonnementUserId) {
        RetrofitService retrofitService = new RetrofitService(context);
        UserService userService = retrofitService.getRetrofit().create(UserService.class);

        ConfigSpring configSpring = new ConfigSpring();
        Call<Void> call = userService.desabonnement(configSpring.userEnCour(),abonnementUserId);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    String responseData = response.message();
                } else {
                    throw new RuntimeException("Request failed");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                if (t instanceof FileNotFoundException) {
                    new Exception(t);
                } else {
                    throw new RuntimeException("Request failed");
                }
            }
        });
    }


    public void sabonner(Long abonnementUserId) {
        RetrofitService retrofitService = new RetrofitService(context);
        UserService userService = retrofitService.getRetrofit().create(UserService.class);

        ConfigSpring configSpring = new ConfigSpring();
        Call<Void> call = userService.abonnement(configSpring.userEnCour(),abonnementUserId);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    String responseData = response.message();
                } else {
                    throw new RuntimeException("Request failed");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                if (t instanceof FileNotFoundException) {
                    new Exception(t);
                } else {
                    throw new RuntimeException("Request failed");
                }
            }
        });
    }

    public CompletableFuture<Boolean> requestPresenceAbonnement(Long idAbonne) {

        RetrofitService retrofitService = new RetrofitService(context);
        UserService userService = retrofitService.getRetrofit().create(UserService.class);

        ConfigSpring configSpring = new ConfigSpring();
        Call<Boolean> call = userService.presenceAbonne(configSpring.userEnCour(),idAbonne);

        CompletableFuture<Boolean> futureInformation = new CompletableFuture<>();

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    String responseData = String.valueOf(response.body());
                    Boolean result = Boolean.parseBoolean(responseData);
                    futureInformation.complete(result);
                } else {
                    futureInformation.completeExceptionally(new RuntimeException("Request failed with code: " + response.code()));
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (t instanceof FileNotFoundException) {
                    futureInformation.completeExceptionally(t);
                } else {
                    futureInformation.completeExceptionally(new RuntimeException("Request failed", t));
                }
            }
        });

        return futureInformation;
    }
}
