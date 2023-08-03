package com.example.MerceariaPalestraitalia.fragment.usuario;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.MerceariaPalestraitalia.adapter.UsuarioPedidoAdapter;
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
    private List<Pedido> pedidoList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsuarioPedidoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configRv();
        recuperPedidos();
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
    public void onDestroy() {
        super.onDestroy();

        binding = null;
    }

    @Override
    public void onClick(Pedido pedido) {
        Toast.makeText(requireContext(), pedido.getPagamento(), Toast.LENGTH_SHORT).show();
    }
}