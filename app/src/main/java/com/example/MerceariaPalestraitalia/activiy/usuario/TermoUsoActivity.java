package com.example.MerceariaPalestraitalia.activiy.usuario;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MerceariaPalestraitalia.databinding.ActivityTermoUsoBinding;

public class TermoUsoActivity extends AppCompatActivity {

    private ActivityTermoUsoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTermoUsoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recuperarDados();

    }
    private void recuperarDados(){
        binding.include.textTitulo.setText("Termos de uso");
        binding.include.include.ibVoltar.setOnClickListener(view -> finish());

        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
    }
}