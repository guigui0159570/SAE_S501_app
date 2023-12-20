package com.example.sae_s501.retrofit;

import com.example.sae_s501.Publication;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PanierService {

    @FormUrlEncoded
    @POST("/panier/createPanier")
    Call<Void> createPanier(@Field("email") String email);

    @GET("/panier/getPrix")
    Call<Float> getPrixByUtiId(@Query("email") String email);

    @FormUrlEncoded
    @POST("/panier/ajoutPublication")
    Call<Void> ajoutPublicationPanier(@Field("email") String email, @Field("idPub") Long idPub);

    @GET("/panier/getPublication")
    Call<List<Publication>> getPublicationsPanier(@Query("email") String email);

    @DELETE("/panier/delete/{id}")
    Call<Void> deletePublication(@Path("id") Long id,@Query("email") String email);

    @POST("/panier/paiement")
    Call<Void> paiementPanier(@Query("email") String email);

}
