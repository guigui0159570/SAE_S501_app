package com.example.sae_s501.retrofit;

import com.example.sae_s501.model.UserRegistrationResponse;
import com.example.sae_s501.model.Utilisateur;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {
    @POST("/register")
    Call<UserRegistrationResponse> registerUser(@Body Utilisateur utilisateur);

    }

