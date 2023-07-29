package com.example.MerceariaPalestraitalia.activiy.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.MerceariaPalestraitalia.adapter.EnderecoAdapter;
import com.example.MerceariaPalestraitalia.databinding.ActivityUsuarioEnderecoBinding;
import com.example.MerceariaPalestraitalia.helper.FirebaseHelper;
import com.example.MerceariaPalestraitalia.model.Endereco;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioEnderecoActivity extends AppCompatActivity implements EnderecoAdapter.OnClickListener {

    private ActivityUsuarioEnderecoBinding binding;
    private EnderecoAdapter enderecoAdapter;
    private final List<Endereco> enderecoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioEnderecoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        iniciaComponentes();

        confgCliks();

        configRv();


    }

    @Override
    protected void onStart() {
        super.onStart();

        recuperaEndereco();
    }

    private void configRv(){
        binding.rvEnderecos.setLayoutManager(new LinearLayoutManager(this));
        binding.rvEnderecos.setHasFixedSize(true);
        enderecoAdapter = new EnderecoAdapter(enderecoList, this,this);
        binding.rvEnderecos.setAdapter(enderecoAdapter);
    }

    private void recuperaEndereco(){
        DatabaseReference enderecoRef = FirebaseHelper.getDatabaseReference()
                .child("enderecos")
                .child(FirebaseHelper.getIdFirebase());
        enderecoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    enderecoList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Endereco endereco = ds.getValue(Endereco.class);
                        enderecoList.add(endereco);
                    }
                    binding.textInfo.setText("");
                }else {
                    binding.textInfo.setText("Nenhum endereço cadastrado.");

                }

                binding.progressBar.setVisibility(View.GONE);
                Collections.reverse(enderecoList);
                enderecoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void confgCliks(){
        binding.include.btnAdd.setOnClickListener(view ->
                startActivity(new Intent(this, UsuariFormEnderecoActivity.class)));
    }
    private void iniciaComponentes(){
        binding.include.textTitulo.setText("Meus endereços");
        binding.include.include.ibVoltar.setOnClickListener(view -> finish());

    }

    @Override
    public void onClick(Endereco endereco) {
        Intent intent = new Intent(this, UsuariFormEnderecoActivity.class);
        intent.putExtra("enderecoSelecionado", endereco);
        startActivity(intent);
    }
}