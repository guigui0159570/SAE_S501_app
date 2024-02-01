package com.example.sae_s501.retrofit;

import com.example.sae_s501.model.Utilisateur;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserService {

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
            @Part MultipartBody.Part files,
            @Part("tags") List<String> tags,
            @Part("email") String email
    );

    @GET("/fichiers/model/{nomFichier}")
    Call<ResponseBody> downloadFile(@Path("nomFichier") String nomFichier);

    @DELETE("/utilisateur/delete/{id}")
    Call<Void> deleteUtilisateur(@Path("id") Long id);
    @FormUrlEncoded
    @POST("/aide/mailAide")
    Call<Void> envoieAide(@Field("email") String email,@Field("aide") String aide);
    @FormUrlEncoded
    @POST("/aide/signalPublication")
    Call<Void> signalement(@Field("email") String email,@Field("id") long id);

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

    @GET ("/sedenotifie/{user}/{id}")
    Call<Void> sedenotifie (@Path("user") long user, @Path("id") long id);

    @GET ("/senotifie/{user}/{id}")
    Call<Void> senotifie (@Path("user") long user, @Path("id") long id);

    @GET ("/presenceAbonne/{userId}/{abonneid}")
    Call<Boolean> presenceAbonne (@Path("userId") long userId, @Path("abonneid") long abonneid);

    @GET ("/presenceUserNotifie/{userId}/{abonnementid}")
    Call<Boolean> presenceUserNotifie (@Path("userId") long userId, @Path("abonnementid") long abonnementid);

    @POST("/updateStringProfil/{user}")
    Call<Void> envoyerString(@Path("user") Long user, @Body Map<String, String> requestBody);

    @Multipart
    @POST("/uploadProfil/{user}")
    Call<Void> handleFileUpload(@Part MultipartBody.Part image, @Path("user") long user);

    @GET("/imageProfil/{nomFichier}")
    Call<ResponseBody> getImageProfil(@Path("nomFichier") String nomFichier);

    @GET("/allsendnotification/{user}")
    Call<Void> allsendnotification(@Path("user") Long user);

    @GET ("/notificationUtilisateur/{user}")
    Call<List<Map>> notificationUtilisateur(@Path("user") Long user);

}


