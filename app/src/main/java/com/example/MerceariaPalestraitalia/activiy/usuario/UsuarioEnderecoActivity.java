package com.example.MerceariaPalestraitalia.activiy.usuario;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MerceariaPalestraitalia.databinding.ActivityUsuarioEnderecoBinding;

public class UsuarioEnderecoActivity extends AppCompatActivity {

    private ActivityUsuarioEnderecoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioEnderecoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        iniciaComponentes();

        confgCliks();
    }

    private void confgCliks(){
        binding.include.btnAdd.setOnClickListener(view ->
                startActivity(new Intent(this, UsuariFormEnderecoActivity.class)));
    }
    private void iniciaComponentes(){
        binding.include.textTitulo.setText("Meus endereços");
        binding.include.include.ibVoltar.setOnClickListener(view -> finish());

    }

}