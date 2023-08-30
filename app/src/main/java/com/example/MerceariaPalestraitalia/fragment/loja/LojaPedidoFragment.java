package com.example.MerceariaPalestraitalia.fragment.loja;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.MerceariaPalestraitalia.R;
import com.example.MerceariaPalestraitalia.activiy.app.DetalhePedidoActivity;
import com.example.MerceariaPalestraitalia.adapter.LojaPedidoAdapter;
import com.example.MerceariaPalestraitalia.databinding.FragmentLojaPedidoBinding;
import com.example.MerceariaPalestraitalia.databinding.LayoutDialogStatusPedidoBinding;
import com.example.MerceariaPalestraitalia.helper.FirebaseHelper;
import com.example.MerceariaPalestraitalia.model.Pedido;
import com.example.MerceariaPalestraitalia.model.StatusPedido;
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
    private AlertDialog dialog;


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

    private void showDialogStatus(Pedido pedido){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                getContext(), R.style.CustomAlertDialog2);

        View view = getLayoutInflater().inflate(R.layout.dialog_form_cadegoria, null);

        LayoutDialogStatusPedidoBinding statusBinding = LayoutDialogStatusPedidoBinding
                .inflate(LayoutInflater.from(getContext()));

        RadioGroup rgStatus = statusBinding.rgStatus;
        RadioButton rbPendente = statusBinding.rbPendente;
        RadioButton rbProvado = statusBinding.rbProvado;
        RadioButton rbTransito = statusBinding.rbTransito;
        RadioButton rbMotoboy = statusBinding.rbMotoboy;
        RadioButton rbEntregue = statusBinding.rbEntregue;
        RadioButton rbCancelado = statusBinding.rbCancelado;


        switch (pedido.getStatusPedido()){
            case PENDENTE:
                rgStatus.check(R.id.rbPendente);
                rbEntregue.setEnabled(true);
                rbTransito.setEnabled(true);
                rbProvado.setEnabled(true);
                rbCancelado.setEnabled(true);
                break;


            case APROVADO:
                rgStatus.check(R.id.rbProvado);
                rbPendente.setEnabled(false);


                break;

            case CAMINHO:
                rgStatus.check(R.id.rbTransito);
                rbProvado.setEnabled(false);
                rbPendente.setEnabled(false);

                break;

            case MOTOBOY_CHAMANDO:
                rgStatus.check(R.id.rbMotoboy);
                rbProvado.setEnabled(false);
                rbPendente.setEnabled(false);

                break;

            case ENTREGUE:
                rgStatus.check(R.id.rbEntregue);
                rbTransito.setEnabled(false);
                rbCancelado.setEnabled(false);
                rbPendente.setEnabled(false);
                rbProvado.setEnabled(false);


                break;

            default:
                rgStatus.check(R.id.rbCancelado);
                rbPendente.setEnabled(false);
                rbProvado.setEnabled(false);
                rbTransito.setEnabled(false);
                rbEntregue.setEnabled(false);


                break;
        }

        statusBinding.btnFecha.setOnClickListener(v -> {
            dialog.dismiss();
        });

        rgStatus.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.rbPendente){
                pedido.setStatusPedido(StatusPedido.PENDENTE);
            } else if (i == R.id.rbProvado) {
                pedido.setStatusPedido(StatusPedido.APROVADO);

            }else if (i == R.id.rbTransito){
                pedido.setStatusPedido(StatusPedido.CAMINHO);

            } else if (i == R.id.rbMotoboy) {
                pedido.setStatusPedido(StatusPedido.MOTOBOY_CHAMANDO);

            }else if (i == R.id.rbEntregue){
                pedido.setStatusPedido(StatusPedido.ENTREGUE);

            }else {
                pedido.setStatusPedido(StatusPedido.CANCELADO);
            }


        });
        statusBinding.btnConfirma.setOnClickListener(view1 -> {

            pedido.salvar(false);
            dialog.dismiss();
        });

        builder.setView(statusBinding.getRoot());

        dialog = builder.create();

        if (!requireActivity().isFinishing()){
            dialog.show();
        }


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
                showDialogStatus(pedido);
                break;
            default:
                Toast.makeText(requireContext(), "Opereção inálida, favor verifique.", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}