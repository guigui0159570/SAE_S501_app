package com.example.sae_s501.model.MonCompte;
import android.graphics.Bitmap;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UpdateUserService {

    @POST("/updateStringProfil/{user}")
    Call<Void> envoyerString(@Path("user") Long user, @Body Map<String, String> requestBody);
}

