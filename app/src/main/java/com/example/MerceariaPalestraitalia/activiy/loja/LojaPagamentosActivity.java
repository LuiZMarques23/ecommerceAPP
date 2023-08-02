package com.example.MerceariaPalestraitalia.activiy.loja;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.MerceariaPalestraitalia.R;
import com.example.MerceariaPalestraitalia.adapter.LojaPagamentoAdapter;
import com.example.MerceariaPalestraitalia.databinding.ActivityLojaPagamentosBinding;
import com.example.MerceariaPalestraitalia.databinding.DialogDeleteBinding;
import com.example.MerceariaPalestraitalia.helper.FirebaseHelper;
import com.example.MerceariaPalestraitalia.model.FormaPagamento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LojaPagamentosActivity extends AppCompatActivity implements LojaPagamentoAdapter.onClick {

    private ActivityLojaPagamentosBinding binding;
    private LojaPagamentoAdapter lojaPagamentoAdapter;
    private final List<FormaPagamento> formaPagamentoList = new ArrayList<>();
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLojaPagamentosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));


        iniciaComponentes();

        configClick();

        configRV();

        recuperaFormaPagamento();
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
                lojaPagamentoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configRV(){
        binding.rvPagamentos.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPagamentos.setHasFixedSize(true);
        lojaPagamentoAdapter = new  LojaPagamentoAdapter(formaPagamentoList, this, this);
        binding.rvPagamentos.setAdapter(lojaPagamentoAdapter);


        binding.rvPagamentos.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {

            }

            @Override
            public void onSwipedRight(int position) {

                showDialogDelete(formaPagamentoList.get(position));
            }
        });

    }

    private void showDialogDelete(FormaPagamento formaPagamento ){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                this, R.style.CustomAlertDialog2);

        View view = getLayoutInflater().inflate(R.layout.dialog_form_cadegoria, null);

        DialogDeleteBinding deleteBinding = DialogDeleteBinding
                .inflate(LayoutInflater.from(this));

        deleteBinding.btnFechar.setOnClickListener(v -> {
            dialog.dismiss();
            lojaPagamentoAdapter.notifyDataSetChanged();
        });

        deleteBinding.textTitulo.setText("Deseja remover este pagamento ?");

        deleteBinding.btnSim.setOnClickListener(v -> {
            formaPagamentoList.remove(formaPagamento);

            if (formaPagamentoList.isEmpty()){
                binding.textInfo.setText("Nenhuma forma de pagamento cadastrado.");

            }else {
                binding.textInfo.setText("");
            }

            formaPagamento.remover();

            lojaPagamentoAdapter.notifyDataSetChanged();

            dialog.dismiss();
        });

        builder.setView(deleteBinding.getRoot());

        dialog = builder.create();
        dialog.show();


    }

    private void configClick(){
        binding.include.btnAdd.setOnClickListener(view ->
                        resultLauncher.launch(
                                new Intent(this, LojaFormPagamentoActivity.class))
        );

    }

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    FormaPagamento pagamento = (FormaPagamento) result.getData().getSerializableExtra("novoPagamento");
                    formaPagamentoList.add(pagamento);
                    lojaPagamentoAdapter.notifyItemInserted(formaPagamentoList.size());
                    binding.textInfo.setText("");
                }

            }
    );

    private void iniciaComponentes(){
        binding.include.textTitulo.setText("Formas de pagamento");
        binding.include.include.ibVoltar.setOnClickListener(view -> finish());
    }

    @Override
    public void onClickListener(FormaPagamento formaPagamento) {
        Intent intent = new Intent(this, LojaFormPagamentoActivity.class);
        intent.putExtra("formaPagamentoSelecionada", formaPagamento);



    }
}