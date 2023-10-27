package com.example.sae_s501;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Connexion extends AppCompatActivity {
    private String email;
    private String password;

    public Connexion(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexion);
    }


    public void onLoginClick(View view) {
        EditText emailEditText = findViewById(R.id.editTextEmail);
        EditText passwordEditText = findViewById(R.id.editTextPassword);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        Connexion userData = new Connexion(email, password);

        Gson gson = new Gson();
        String jsonData = gson.toJson(userData);

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, jsonData);

        Request request = new Request.Builder()
                .url("http://localhost:8080/Connexion")
                .post(requestBody)
                .build();

        try {
            // Exécuter la requête
            Response response = client.newCall(request).execute();

            // Traiter la réponse, par exemple, afficher le code de réponse
            int statusCode = response.code();
            String responseBody = response.body().string();

            // Vous pouvez gérer la réponse du serveur ici
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
