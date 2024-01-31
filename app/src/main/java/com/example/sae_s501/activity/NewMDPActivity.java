package com.example.sae_s501.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sae_s501.R;

public class NewMDPActivity extends AppCompatActivity {
    public static final String ACTION_VIEW_DESTINATION = "com.votreapp.action.VIEW_DESTINATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_mdp);
    }
}
