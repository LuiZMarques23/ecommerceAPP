package com.example.MerceariaPalestraitalia.activiy.usuario;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MerceariaPalestraitalia.databinding.ActivityAjudaBinding;

public class AjudaActivity extends AppCompatActivity {

    private ActivityAjudaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAjudaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.include.textTitulo.setText("Contatos");
        binding.include.include.ibVoltar.setOnClickListener(view -> finish());

        corStatusBar();
    }

    private void corStatusBar(){
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
    }
}