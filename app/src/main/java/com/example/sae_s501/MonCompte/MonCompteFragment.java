package com.example.sae_s501.MonCompte;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sae_s501.R;
import com.example.sae_s501.databinding.MoncompteBinding;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MonCompteFragment extends Fragment {
    private MoncompteBinding binding;
    private ViewGroup containerAll;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MonCompteViewModel monCompteViewModel =
                new ViewModelProvider(this).get(MonCompteViewModel.class);

        binding = MoncompteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        containerAll = container;
        ViewGroup viewParent = root.findViewById(R.id.abonnement);
        TextView textView = new TextView(getContext());
        textView.setTextSize(25);
        textView.setAllCaps(true);
        textView.setText("salut2");
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        textView.setShadowLayer(15, 0, 0, Color.WHITE);
        textView.setTypeface(null, Typeface.BOLD);
        viewParent.addView(textView);

        Log.d("textview", (String) textView.getText());
        CompletableFuture<Integer> requeteFuture = monCompteViewModel.RequeteCountAbonnement();
        requeteFuture.thenAccept(resultat -> {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("99999999", String.valueOf(resultat));
                            TextView textView = root.findViewById(R.id.countAbonnement);
                            textView.setText(String.valueOf(resultat));
                            Log.d("8888888", (String) textView.getText());

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return root;

    }
}
