package com.example.sae_s501.retrofit;

import com.example.sae_s501.Avis;
import com.example.sae_s501.AvisDTO;
import com.example.sae_s501.Publication;
import com.example.sae_s501.Utilisateur;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FilActuService {

    @GET("/publication/getAllByTime")
    Call<List<Publication>> getAllPublication();

    @GET("/publication/getByFiltre")
    Call<List<Publication>> getAllPublicationByFiltre(@Query("filtre") String filtre);

    @GET("/publication/avis/get/pub/{id}")
    Call<List<AvisDTO>> getAllAvisByPublication(@Path(("id")) Long publication);

    @GET("/publication/get/{id}")
    Call<Publication> getPublicationById(@Path(("id")) Long publication);

    @GET("/publication/get/uti/{id}/getFiltreByUser")
    Call<List<Publication>> getPublicationFiltreByUtilisateurId(@Path("id") Long id,@Query("filtre") String filtre);

    /* affichage pub*/
    @GET("/publication/get/uti/{id}")
    Call<List<Publication>> getPublicationByUtilisateurId(@Path("id") Long id);

    /* recup id utilisateur */
    @GET("getUtilisateurIdByEmail")
    Call<Long> getUtilisateurIdByEmail(@Query("email") String email);

    @DELETE("/publication/delete/{id}")
    Call<Void> deletePublication(@Path("id") Long id);

    @GET("fichiers/image/{nomFichier}")
    Call<ResponseBody> getImage(@Path("nomFichier") String nomFichier);

    @GET("/getUtilisateur/{id}")
    Call<Utilisateur> getUtilisateurById(@Path("id") Long id);
}
