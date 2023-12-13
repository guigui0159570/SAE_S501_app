package com.example.sae_s501.retrofit;

import android.content.Context;

import com.example.sae_s501.Dictionnaire;
import com.example.sae_s501.authentification.Authentification;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitServiceRegister {
    private Retrofit retrofit;
    private static final String BASE_URL = Dictionnaire.getIpAddress();


    public void initializerRetrofit(){
        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                /*.baseUrl("http://192.168.1.21:8080")*/
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }

    public RetrofitServiceRegister(){
        initializerRetrofit();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder().build();
    }


}
