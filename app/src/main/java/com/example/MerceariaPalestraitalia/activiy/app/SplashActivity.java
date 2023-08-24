package com.example.MerceariaPalestraitalia.activiy.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.MerceariaPalestraitalia.DAO.ItemPedidoDAO;
import com.example.MerceariaPalestraitalia.R;
import com.example.MerceariaPalestraitalia.activiy.loja.MainActivityEmpresa;
import com.example.MerceariaPalestraitalia.activiy.usuario.MainActivityUsuario;
import com.example.MerceariaPalestraitalia.helper.FirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler(getMainLooper()).postDelayed(this::verificaAcesso,2000);

       recuperaDados();

    }

    private void recuperaDados(){
        limparCarrinho();
        corStatusBar();
    }

    private void limparCarrinho(){
        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO(this);
        itemPedidoDAO.limparCarrinho();
    }

    private void corStatusBar(){
        getWindow().setStatusBarColor(Color.parseColor("#03A9F4"));
    }

    private void verificaAcesso(){
        if (FirebaseHelper.getAutenticado()){
            recuperaAcesso();

        }else {
            finish();
            startActivity(new Intent(this, MainActivityUsuario.class));
        }
    }

    private void recuperaAcesso(){
        DatabaseReference usuariosRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(FirebaseHelper.getIdFirebase());
        usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){ // Usuário
                    startActivity(new Intent(getBaseContext(), MainActivityUsuario.class));
                }else { // Loja
                    startActivity(new Intent(getBaseContext(), MainActivityEmpresa.class));
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}