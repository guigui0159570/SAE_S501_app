package com.example.sae_s501.model;

import com.example.sae_s501.model.User.Avis;
import com.example.sae_s501.model.User.Publication;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class APIClient {
    private static final String BASE_URL = Dictionnaire.getIpAddress();

    public APIClient createService(String username, String password) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(username, password))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(APIClient.class);
    }

    @GET("/publication/getAll")
    Call<List<Publication>> getAllPublication() {
        return null;
    }

    @GET("/avis/get/pub/{id}")
    Call<List<Avis>> getAllAvisByPublication(@Query(("publication_id")) Long publication) {
        return null;
    }
}
