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
    @POST("/api/auth/register/save")
    Call<Utilisateur> registerUser(@Field("pseudo") String pseudo,
                                   @Field("email") String email,
                                   @Field("password") String password);


    @Multipart
    @POST("/publication/save")
    Call<Void> createPublication(
            @Part("titre") RequestBody title,
            @Part("description") RequestBody description,
            @Part("gratuit") RequestBody gratuit,
            @Part("publique") RequestBody publique,
            @Part("prix") RequestBody prix,
            @Part MultipartBody.Part image,
            @Part("proprietaire") RequestBody proprietaire
    );
}
/*@PostMapping("/savePublication")
    public Publication savePublication(
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("gratuit") boolean gratuit,
            @RequestParam("publique") boolean publique,
            @RequestParam("prix") float prix,
            @RequestParam("image") MultipartFile image,
            @RequestParam("fichier") MultipartFile fichier,
            @RequestParam("proprietaire") Long proprietaire) {*/

