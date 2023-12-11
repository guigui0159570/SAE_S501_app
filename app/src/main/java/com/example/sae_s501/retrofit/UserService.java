package com.example.sae_s501.retrofit;

import com.example.sae_s501.Publication;
import com.example.sae_s501.model.Utilisateur;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {
    /* register */

    @FormUrlEncoded
    @POST("/api/auth/register/save")
    Call<Utilisateur> registerUser(@Field("pseudo") String pseudo,
                                   @Field("email") String email,
                                   @Field("password") String password);


    /* creation pub*/
    @Multipart
    @POST("/publication/save")
    Call<Void> createPublication(
            @Part("titre") RequestBody title,
            @Part("description") RequestBody description,
            @Part("gratuit") RequestBody gratuit,
            @Part("publique") RequestBody publique,
            @Part("prix") RequestBody prix,
            @Part MultipartBody.Part image,
            @Part("tags") List<String> tags,
            @Part("email") String email
    );
    @DELETE("/utilisateur/delete/{id}")
    Call<Void> deleteUtilisateur(@Path("id") Long id);
    @FormUrlEncoded
    @POST("/aide/mailAide")
    Call<Void> envoieAide(@Field("email") String email,@Field("aide") String aide);



}


