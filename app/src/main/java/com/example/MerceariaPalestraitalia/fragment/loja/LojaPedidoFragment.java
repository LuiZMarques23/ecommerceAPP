package com.example.MerceariaPalestraitalia.fragment.loja;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.MerceariaPalestraitalia.activiy.app.DetalhePedidoActivity;
import com.example.MerceariaPalestraitalia.adapter.LojaPedidoAdapter;
import com.example.MerceariaPalestraitalia.databinding.FragmentLojaPedidoBinding;
import com.example.MerceariaPalestraitalia.helper.FirebaseHelper;
import com.example.MerceariaPalestraitalia.model.Pedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LojaPedidoFragment extends Fragment implements LojaPedidoAdapter.OnClickListener {

    private FragmentLojaPedidoBinding binding;
    private LojaPedidoAdapter lojaPedidoAdapter;
    private final List<Pedido> pedidoList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLojaPedidoBinding.inflate(inflater, container, false);
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
        lojaPedidoAdapter = new LojaPedidoAdapter(pedidoList, requireContext(), this);
        binding.rvPedidos.setAdapter(lojaPedidoAdapter);
    }

    private void recuperPedidos(){
        DatabaseReference pedidoRef = FirebaseHelper.getDatabaseReference()
                .child("lojaPedidos");
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
                    binding.textInfo.setText("Nenhum pedido recebido.");
                }
                binding.progressBar.setVisibility(View.GONE);
                Collections.reverse(pedidoList);
                lojaPedidoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(Pedido pedido, String operacao) {
        switch (operacao){
            case "detalhes":
                Intent intent = new Intent(requireContext(), DetalhePedidoActivity.class);
                intent.putExtra("pedidoSelecionado", pedido);
                startActivity(intent);
                break;
            case "status":
                Toast.makeText(requireContext(), "Status do pedido.", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(requireContext(), "Opereção inálida, favor verifique.", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}