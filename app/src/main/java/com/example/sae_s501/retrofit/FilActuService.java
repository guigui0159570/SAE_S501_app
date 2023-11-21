package com.example.sae_s501.retrofit;

import com.example.sae_s501.Publication;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FilActuService {

    @GET("/publication/getAll")
    Call<List<Object>> getAllPublication();
}
