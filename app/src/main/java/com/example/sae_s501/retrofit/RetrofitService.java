package com.example.sae_s501.retrofit;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.sae_s501.authentification.Authentification;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private Retrofit retrofit;

    public void initializerRetrofit(Context context){
        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://10.6.3.229:8080")
                /*.baseUrl("http://192.168.1.21:8080")*/
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(Authentification.createAuthenticatedClient(context))
                .build();
    }

    public RetrofitService(Context context){
        initializerRetrofit(context);
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

}