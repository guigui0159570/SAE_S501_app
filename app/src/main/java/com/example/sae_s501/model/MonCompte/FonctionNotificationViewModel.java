package com.example.sae_s501.model.MonCompte;

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

public class FonctionNotificationViewModel {

    private Context context;

    public FonctionNotificationViewModel(Context context) {
        this.context = context;
    }

    public void seNotifier(Long abonnementUserId) {
        RetrofitService retrofitService = new RetrofitService(context);
        UserService userService = retrofitService.getRetrofit().create(UserService.class);

        ConfigSpring configSpring = new ConfigSpring();
        Call<Void> call = userService.senotifie(configSpring.userEnCour(context),abonnementUserId);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    String responseData = response.message();
                    Log.d("gooooooooood", "OK");
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


    public void seDenotifier(Long abonnementUserId) {
        RetrofitService retrofitService = new RetrofitService(context);
        UserService userService = retrofitService.getRetrofit().create(UserService.class);

        ConfigSpring configSpring = new ConfigSpring();
        Call<Void> call = userService.sedenotifie(configSpring.userEnCour(context),abonnementUserId);
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

    public CompletableFuture<Boolean> requestPresenceUserNotifier(Long idAbonnement) {

        RetrofitService retrofitService = new RetrofitService(context);
        UserService userService = retrofitService.getRetrofit().create(UserService.class);

        ConfigSpring configSpring = new ConfigSpring();
        Call<Boolean> call = userService.presenceUserNotifie(configSpring.userEnCour(context),idAbonnement);

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

    public void allsendNotification(Long user) {
        RetrofitService retrofitService = new RetrofitService(context);
        UserService userService = retrofitService.getRetrofit().create(UserService.class);
        Call<Void> call = userService.allsendnotification(user);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    String responseData = response.message();
                    Log.d("zzzzzzzzzzzzz", "onResponse: ");
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
    public CompletableFuture<String> requestInformationNotification(Context context) {
        RetrofitService retrofitService = new RetrofitService(context);
        UserService userService = retrofitService.getRetrofit().create(UserService.class);

        ConfigSpring configSpring = new ConfigSpring();
        Call<List<Map>> call = userService.notificationUtilisateur(configSpring.userEnCour(context));

        CompletableFuture<String> futureInformation = new CompletableFuture<>();
        call.enqueue(new Callback<List<Map>>() {
            @Override
            public void onResponse(Call<List<Map>> call, Response<List<Map>> response) {
                if (response.isSuccessful()) {
                    String responseData = String.valueOf(response.body());
                    Log.d("7878787" , responseData);
                    futureInformation.complete(responseData);
                } else {
                    futureInformation.completeExceptionally(new RuntimeException("Request failed with code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Map>> call, Throwable t) {
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
