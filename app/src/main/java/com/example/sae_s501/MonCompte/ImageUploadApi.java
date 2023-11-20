package com.example.sae_s501.MonCompte;
import com.google.gson.JsonObject;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ImageUploadApi {

    @POST("/upload")
    Call<Void> uploadImage(@Body RequestBody imageBase64);
}