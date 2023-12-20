package com.example.sae_s501;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sae_s501.model.MonCompte.AbonneCompte;
import com.example.sae_s501.model.MonCompte.AbonnementCompte;
import com.example.sae_s501.model.MonCompte.ConfigSpring;
import com.example.sae_s501.databinding.UpdatemoncompteBinding;
import com.example.sae_s501.retrofit.FilActuService;
import com.example.sae_s501.retrofit.PanierService;
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.SessionManager;
import com.example.sae_s501.retrofit.UserService;
import com.example.sae_s501.model.MonCompte.MonCompteViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.example.sae_s501.databinding.MoncompterespBinding;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyCompteActivity extends AppCompatActivity {
    private ConfigSpring configSpring = new ConfigSpring();
    private MoncompterespBinding binding;
    private RetrofitService retrofitService;
    private FilActuService filActuService;
    private PanierService panierService;
    private String jwtEmail;
    private Long jwtId;

    private UpdatemoncompteBinding bindingUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MoncompterespBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        MonCompteViewModel monCompteViewModel = new ViewModelProvider(this).get(MonCompteViewModel.class);
        jwtEmail = SessionManager.getUserEmail(this);
        jwtId = SessionManager.getUserId(this);

        retrofitService = new RetrofitService(this);
        FilActuService filActuService = retrofitService.getRetrofit().create(FilActuService.class);
        //Frag publication mon compte
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_moncompte_pub, new MesPublicationsFrag())
                .commit();
        //Partie information
        CompletableFuture<String> stringCompletableFuture = monCompteViewModel.requestInformation(this,jwtId);
        informationUser(stringCompletableFuture, root);

        //Partie fragment parametre
        View fragment = root.findViewById(R.id.parametreCompte);
        fragment.setVisibility(View.INVISIBLE);

        LinearLayout floutage = root.findViewById(R.id.floutage);
        floutage.setVisibility(View.INVISIBLE);

        //Style tool barre
        LinearLayout layoutProfil = root.findViewById(R.id.profilLayout);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#bdbdbd" ));
        layoutProfil.setBackground(colorDrawable);

        // ouverture animé du parametre
        ImageButton troisPoints = root.findViewById(R.id.troispoints);
        troisPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Créez une animation d'échelle
                Animation scaleInAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.fragment_parametre);
                Animation floutageAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.animfloutage);

                // Appliquez l'animation d'échelle aux éléments
                fragment.startAnimation(scaleInAnimation);
                floutage.startAnimation(floutageAnimation);

                fragment.setVisibility(View.VISIBLE);
                floutage.setVisibility(View.VISIBLE);
                floutage.setClickable(true);
            }
        });
        ImageButton shop = root.findViewById(R.id.shop);


        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Créez une instance de l'API définie par votre interface
                panierService = retrofitService.getRetrofit().create(PanierService.class);

                // Effectuez l'appel réseau pour créer le panier
                Call<Void> call = panierService.createPanier(jwtEmail);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Intent intent = new Intent(view.getContext(), Panier.class);
                            startActivity(intent);
                        } else {
                            showToast("Erreur requete");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        showToast("Erreur serveur");
                    }
                });
            }
        });

        // fermeture animé du parametre
        Button closeParametre = root.findViewById(R.id.closeParametre);
        closeParametre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation scaleInAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.inverseanimfloutage);
                Animation fragmentAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.inversefragment_parametre);

                floutage.startAnimation(scaleInAnimation);
                fragment.startAnimation(fragmentAnim);

                fragment.setVisibility(View.INVISIBLE);
                floutage.setVisibility(View.INVISIBLE);
                floutage.setClickable(false);
            }
        });

        //Page modification profile
        Button updateProfile = root.findViewById(R.id.updateprofile);
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MyUpdateCompteActivity.class);
                startActivity(intent);
            }
        });

        //Deconnexion
        Button deconnexion = root.findViewById(R.id.deconnexion);
        deconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager.deleteToken(getApplicationContext());
                Toast.makeText(MyCompteActivity.this, "Vous êtes déconnecté !", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(view.getContext(), Connexion.class);
                startActivity(intent);

            }
        });
        Button langue = root.findViewById(R.id.langues);
        ImageView flag =  root.findViewById(R.id.flag_langue);
        Locale currentLocale = getResources().getConfiguration().locale;

        if (currentLocale.getLanguage().equals("en")) {
            flag.setImageResource(R.drawable.france_flag);
        } else {
            flag.setImageResource(R.drawable.greatbritain);
        }
        langue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Changer la langue de l'application
                if (currentLocale.getLanguage().equals("en")) {
                    setLocale("fr");
                } else {
                    setLocale("en");
                }
                recreate();
            }
        });
        //Aide
        Button aide = root.findViewById(R.id.aide);
        aide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Aide.class);
                startActivity(intent);

            }
        });
        //Supprimer compte
        Button supprimer = root.findViewById(R.id.supprimer);
        supprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<Long> callUserId = filActuService.getUtilisateurIdByEmail(jwtEmail);
                Log.d("callUserId", callUserId.toString());

                callUserId.enqueue(new Callback<Long>() {
                    @Override
                    public void onResponse(Call<Long> call, Response<Long> response) {
                        if (response.isSuccessful()) {
                            Long userId = response.body();
                            Log.d("UserID", String.valueOf(userId));
                            if (userId != null) {
                                DeleteConfirmation(userId);

                            }
                        }
                        else{
                            Log.d("ERREUR REQUETE", "ERREUR REQUETE" + response);
                        }
                    }

                    @Override
                    public void onFailure(Call<Long> call, Throwable t) {
                        showToast("Erreur lors de la communication avec le serveur");
                    }
                });
            }
        });

        TextView textViewAbonnementview = root.findViewById(R.id.AbonnementView);
        textViewAbonnementview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AbonnementCompte.class);
                startActivity(intent);
            }
        });

        TextView textViewAbonneview = root.findViewById(R.id.AbonneView);
        textViewAbonneview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AbonneCompte.class);
                startActivity(intent);
            }
        });
    }

    public void informationUser(CompletableFuture<String> integerCompletableFuture, View root) {
        integerCompletableFuture.thenAccept(resultat -> {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("8899989933333", String.valueOf(resultat));
                        try {
                            JsonParser parser = new JsonParser();
                            Log.d("JSON_BEFORE_PARSE", resultat);
                            JsonObject jsonObject = parser.parse(resultat).getAsJsonObject();
                            Log.d("JSON_AFTER_PARSE", jsonObject.toString());

                            // Partie abonne
                            try {
                                JsonElement countAbonneElement = jsonObject.get("countAbonne");
                                Log.d("7783333333", String.valueOf(countAbonneElement));
                                TextView textViewAbonne = root.findViewById(R.id.countAbonne);
                                textViewAbonne.setText(String.valueOf(countAbonneElement).replaceAll("^\"|\"$", ""));
                            } catch (Exception e) {
                                Log.e("Exception", "Erreur lors de la lecture de countAbonne", e);
                            }

                            // Partie abonnement
                            try {
                                JsonElement countAbonnementElement = jsonObject.get("countAbonnement");
                                TextView textViewAbonnement = root.findViewById(R.id.countAbonnement);
                                textViewAbonnement.setText(String.valueOf(countAbonnementElement).replaceAll("^\"|\"$", ""));
                            } catch (Exception e) {
                                Log.e("Exception", "Erreur lors de la lecture de countAbonnement", e);
                            }

                            // Partie pseudo

                            JsonElement pseudoElement = jsonObject.get("pseudo");
                            TextView textViewPseudo = root.findViewById(R.id.mon_compte);
                            String pseudo = String.valueOf(pseudoElement).replaceAll("^\"|\"$", "");
                            textViewPseudo.setText(pseudo);


                            // Partie Description
                            TextView textViewDescription = root.findViewById(R.id.description);
                            JsonElement descriptionElement = jsonObject.get("description");
                            if (!descriptionElement.isJsonNull()) {
                                Log.d("TAG7", "run: ");
                                String Description = descriptionElement.getAsString();
                                String decodedDescription = URLDecoder.decode(Description, StandardCharsets.UTF_8.toString());
                                textViewDescription.setText(decodedDescription);
                            }
                            // Partie photo
                            try {
                                ImageView imageViewPhoto = root.findViewById(R.id.photoProfil);
                                JsonElement photoElement = jsonObject.get("photo");

                                if (!photoElement.isJsonNull()) {
                                    Log.d("gggggggggggg", "777 ");
                                    String photoElementAsString = photoElement.getAsString();
                                    MonCompteViewModel monCompteViewModel = new MonCompteViewModel();
                                    monCompteViewModel.Imageprofil(getBaseContext(),imageViewPhoto, photoElementAsString);
                                } else {
                                    Log.d("fffffffffffffff", "777 ");
                                    // Création image random
                                    String initials = String.valueOf(pseudo.charAt(0));
                                    int width = 200;
                                    int height = 200;

                                    int backgroundColor = ConfigSpring.couleurDefault;
                                    int textColor = Color.WHITE;

                                    MonCompteViewModel monCompteViewModel = new MonCompteViewModel();
                                    Bitmap generatedImage = monCompteViewModel.generateInitialsImage(initials, width, height, backgroundColor, textColor);
                                    imageViewPhoto.setImageBitmap(generatedImage);
                                }
                            } catch (Exception e) {
                                Log.e("Exception", "Erreur lors de la lecture de la photo", e);
                            }
                        } catch (Exception e) {
                            Log.e("Exception", "Erreur lors de l'analyse JSON", e);
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("Exception", "Une autre exception s'est produite", e);
            }
        });
    }
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
    }

    private void DeleteConfirmation(Long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation")
                .setMessage("Voulez-vous vraiment supprimer votre compte?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePublication(id);
                        SessionManager.deleteToken(getApplicationContext());
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void deletePublication(Long id) {
        retrofitService = new RetrofitService(this);
        UserService userService = retrofitService.getRetrofit().create(UserService.class);
        Call<Void> callsupp = userService.deleteUtilisateur(id);

        callsupp.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("REQUETE_SUPPRESSION", "Votre compte a été supprimé avec succès");
                    showToast("Votre compte a été supprimé !");
                    Intent intent = new Intent(MyCompteActivity.this, Connexion.class);
                    startActivity(intent);
                } else {
                    Log.e("REQUETE_SUPPRESSION", "Échec de la suppression du compte. Code de réponse : " + response.code());
                    showToast("Échec de la suppression du compte. Veuillez réessayer.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("REQUETE_SUPPRESSION", "Échec de la suppression du compte. Erreur : " + t.getMessage());
                showToast("Échec de la suppression du compte. Veuillez vérifier votre connexion internet.");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}