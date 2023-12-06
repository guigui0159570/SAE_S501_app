package com.example.sae_s501.retrofit;

import com.example.sae_s501.authentification.JwtResponse;
import com.example.sae_s501.authentification.LoginRequest;
import com.example.sae_s501.model.UserEmailResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthService {
    @POST("/api/auth/login")
    Call<JwtResponse> authenticate(@Body LoginRequest loginRequest);
    @GET("/api/auth/validate-token")
    Call<ResponseBody> validateToken();

    @FormUrlEncoded
    @POST("/api/auth/resetPassword")
    Call<Void> resetPassword(@Field("email") String email);

}
