package com.example.sae_s501.retrofit;

import com.example.sae_s501.Avis;
import com.example.sae_s501.Publication;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FilActuService {

    @GET("/publication/getAll")
    Call<List<Publication>> getAllPublication();

    @GET("/avis/get/pub/{id}")
    Call<List<Avis>> getAllAvisByPublication(@Query(("publication_id")) Long publication);
}
