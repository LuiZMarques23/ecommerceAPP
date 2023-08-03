package com.example.MerceariaPalestraitalia.activiy.usuario;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MerceariaPalestraitalia.databinding.ActivityPoliticaBinding;

public class PoliticaActivity extends AppCompatActivity {

    private ActivityPoliticaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPoliticaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.include.textTitulo.setText("Politica de Privacidade");
        binding.include.include.ibVoltar.setOnClickListener(view -> finish());

        corStatusBar();
    }

    private void corStatusBar(){
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
    }

}