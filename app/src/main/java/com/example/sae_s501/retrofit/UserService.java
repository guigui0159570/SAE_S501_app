package com.example.sae_s501.retrofit;

import com.example.sae_s501.model.Publication;
import com.example.sae_s501.model.Utilisateur;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserService {

    @FormUrlEncoded
    @POST("/register/save")
    Call<Utilisateur> registerUser(@Field("pseudo") String pseudo,
                                   @Field("email") String email,
                                   @Field("password") String password);


    @FormUrlEncoded
    @POST("/savePublication")
    Call<Publication> createPublication(
            @Field("title") String title,
            @Field("description") String description,
            @Field("gratuit") boolean gratuit,
            @Field("prix") float prix
    );
}


