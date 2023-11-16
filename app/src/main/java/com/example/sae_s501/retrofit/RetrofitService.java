package com.example.sae_s501.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private Retrofit retrofit;

    public void initializerRetrofit(){
        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://10.6.2.252:8080")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }

    public RetrofitService(){
        initializerRetrofit();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
