package com.example.sae_s501;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.sae_s501.activity.FilActuActivity;
import com.example.sae_s501.activity.MesPublicationsActivity;
import com.example.sae_s501.activity.MyCompteActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class ToolBarreFragment extends Fragment implements NavigationBarView.OnItemSelectedListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tool_barre, container, false);
        BottomNavigationView navigationView = rootView.findViewById(R.id.nav_bottom);
        changeItemSelected(navigationView, requireActivity());
        navigationView.setOnItemSelectedListener(this);


        return rootView;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        int itemId = item.getItemId();

        if (itemId == R.id.nav_bottom_publication) {
            // Afficher les publications
            intent = new Intent(getContext(), MesPublicationsActivity.class);
        } else if (itemId == R.id.nav_bottom_home) {
            // Afficher l'accueil
            intent = new Intent(getContext(), FilActuActivity.class);
        } else if (itemId == R.id.nav_bottom_profil) {
            // Afficher le profil
            intent = new Intent(getContext(), MyCompteActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
            requireActivity().finish();
            return true;
        } else {
            return false;
        }
    }

    public void changeItemSelected(BottomNavigationView navigationView, Activity activity) {
        if (activity instanceof MesPublicationsActivity) {
            navigationView.setSelectedItemId(R.id.nav_bottom_publication);
        } else if (activity instanceof FilActuActivity) {
            navigationView.setSelectedItemId(R.id.nav_bottom_home);
        } else if (activity instanceof MyCompteActivity) {
            navigationView.setSelectedItemId(R.id.nav_bottom_profil);
        }
    }
}