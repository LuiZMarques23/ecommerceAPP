package com.example.MerceariaPalestraitalia.fragment.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.MerceariaPalestraitalia.activiy.app.DetalhePedidoActivity;
import com.example.MerceariaPalestraitalia.adapter.UsuarioPedidoAdapter;
import com.example.MerceariaPalestraitalia.autenticacao.LoginActivity;
import com.example.MerceariaPalestraitalia.databinding.FragmentUsuarioPedidoBinding;
import com.example.MerceariaPalestraitalia.helper.FirebaseHelper;
import com.example.MerceariaPalestraitalia.model.Pedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class UsuarioPedidoFragment extends Fragment implements UsuarioPedidoAdapter.OnClickListener {

    private FragmentUsuarioPedidoBinding binding;
    private UsuarioPedidoAdapter usuarioPedidoAdapter;
    private final List<Pedido> pedidoList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsuarioPedidoBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configClick();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (FirebaseHelper.getAutenticado()){
            binding.btnLogin.setVisibility(View.GONE);
            configRv();
            recuperPedidos();
        }else {
            binding.btnLogin.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
            binding.textInfo.setText("Você não está autenticado no app clique em.");

        }
    }

    private void configClick(){
        binding.btnLogin.setOnClickListener(view -> {
            startActivity(new Intent(requireContext(), LoginActivity.class));
        });
    }

    private void configRv(){
        binding.rvPedidos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvPedidos.setHasFixedSize(true);
        usuarioPedidoAdapter = new UsuarioPedidoAdapter(pedidoList, requireContext(), this);
        binding.rvPedidos.setAdapter(usuarioPedidoAdapter);
    }

    private void recuperPedidos(){
        DatabaseReference pedidoRef = FirebaseHelper.getDatabaseReference()
                .child("usuarioPedidos")
                .child(FirebaseHelper.getIdFirebase());
        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    pedidoList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Pedido pedido = ds.getValue(Pedido.class);
                        pedidoList.add(pedido);
                    }
                    binding.textInfo.setText("");
                }else {
                    binding.textInfo.setText("Nenhum pedido encontrado.");
                }
                binding.progressBar.setVisibility(View.GONE);
                Collections.reverse(pedidoList);
                usuarioPedidoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onClick(Pedido pedido) {
        Intent intent = new Intent(requireContext(), DetalhePedidoActivity.class);
        intent.putExtra("pedidoSelecionado", pedido);
        startActivity(intent);
    }
}