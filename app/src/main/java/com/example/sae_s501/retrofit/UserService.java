package com.example.sae_s501.retrofit;

import android.graphics.Bitmap;

import com.example.sae_s501.Publication;
import com.example.sae_s501.model.Utilisateur;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
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

    @GET("/abonneUser/{id}")
    Call<List<Map>> getAbonneUtilisateur(@Path("id") long id);

    @GET("/abonnementUser/{id}")
    Call<List<Map>> getAbonnementUtilisateur(@Path("id") long id);

    @GET("/userInformation/{user}")
    Call<Map<String, String>> RequestInformationUser(@Path("user") long user);

    @GET ("/desabonnemnt/{user}/{id}")
    Call<Void> desabonnement (@Path("user") long user, @Path("id") long id);

    @GET ("/abonnenement/{user}/{id}")
    Call<Void> abonnement (@Path("user") long user, @Path("id") long id);

    @GET ("/presenceAbonne/{userId}/{abonneid}")
    Call<Boolean> presenceAbonne (@Path("userId") long userId, @Path("abonneid") long abonneid);

    @POST("/updateStringProfil/{user}")
    Call<Void> envoyerString(@Path("user") Long user, @Body Map<String, String> requestBody);

    @Multipart
    @POST("/uploadProfil/{user}")
    Call<Void> handleFileUpload(@Part MultipartBody.Part image, @Path("user") long user);

    @GET("/imageProfil/{nomFichier}")
    Call<ResponseBody> getImageProfil(@Path("nomFichier") String nomFichier);

}


