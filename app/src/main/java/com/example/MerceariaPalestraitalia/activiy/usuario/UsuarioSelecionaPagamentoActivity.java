package com.example.MerceariaPalestraitalia.activiy.usuario;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.MerceariaPalestraitalia.adapter.UsuarioPagamentoAdapter;
import com.example.MerceariaPalestraitalia.databinding.ActivityUsuarioSelecionaPagamentoBinding;
import com.example.MerceariaPalestraitalia.helper.FirebaseHelper;
import com.example.MerceariaPalestraitalia.model.FormaPagamento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioSelecionaPagamentoActivity extends AppCompatActivity implements UsuarioPagamentoAdapter.onClick {

    private ActivityUsuarioSelecionaPagamentoBinding binding;
    private UsuarioPagamentoAdapter usuarioPagamentoAdapter;
    private final List<FormaPagamento> formaPagamentoList = new ArrayList<>();
    private FormaPagamento formaPagamento = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioSelecionaPagamentoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recuperaDados();
    }

    private void recuperaDados(){
        iniciaComponentes();
        configClicks();
        configRV();
        recuperaFormaPagamento();
        corStatusBar();
    }
    private void configClicks(){
        binding.include.include.ibVoltar.setOnClickListener(view -> finish());

        binding.btnContinua.setOnClickListener(view -> {
        if (formaPagamento != null){
                Intent intent = new Intent(this, UsuarioResumoPedidoActivity.class);
                intent.putExtra("pagamentoSelecionado", formaPagamento);
                startActivity(intent);
        }else {
            Toast.makeText(this, "Seleciona a forma de pagamento do seu pedidos.", Toast.LENGTH_SHORT).show();
        }
        });
    }
    private void recuperaFormaPagamento(){
        DatabaseReference pagamentoRef = FirebaseHelper.getDatabaseReference()
                .child("formapagamento");
        pagamentoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    formaPagamentoList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        FormaPagamento formaPagamento = ds.getValue(FormaPagamento.class);
                        formaPagamentoList.add(formaPagamento);
                    }
                    binding.textInfo.setText("");
                }else {
                    binding.textInfo.setText("Nenhuma forma de pagamento cadastrada.");
                }

                binding.progressBar.setVisibility(View.GONE);
                Collections.reverse(formaPagamentoList);
                usuarioPagamentoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void configRV(){
        binding.rvPagamentos.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPagamentos.setHasFixedSize(true);
        usuarioPagamentoAdapter = new UsuarioPagamentoAdapter(formaPagamentoList, this);
        binding.rvPagamentos.setAdapter(usuarioPagamentoAdapter);
    }
    private void iniciaComponentes(){
        binding.include.textTitulo.setText("Formas de pagamento");
    }
    @Override
    public void onClickListener(FormaPagamento pagamento) {
        this.formaPagamento = pagamento;


    }
    private void corStatusBar(){
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
    }
}