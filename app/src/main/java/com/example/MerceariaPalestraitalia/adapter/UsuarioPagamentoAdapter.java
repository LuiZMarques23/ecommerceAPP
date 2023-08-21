package com.example.MerceariaPalestraitalia.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MerceariaPalestraitalia.R;
import com.example.MerceariaPalestraitalia.model.FormaPagamento;

import java.util.List;

public class UsuarioPagamentoAdapter extends RecyclerView.Adapter<UsuarioPagamentoAdapter.MyViewHolder> {

    private final List<FormaPagamento> formaPagamentoList;
    private final onClick onClick;
    private int row_index = -1;

    public UsuarioPagamentoAdapter(List<FormaPagamento> formaPagamentoList, UsuarioPagamentoAdapter.onClick onClick) {
        this.formaPagamentoList = formaPagamentoList;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pagamento_pedido_adapter,parent,false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FormaPagamento formaPagamento = formaPagamentoList.get(position);

        holder.textNomePagamento.setText(formaPagamento.getNome());
        holder.textDescricaoPagamento.setText(formaPagamento.getDescricao());

        holder.itemView.setOnClickListener(view -> {
            onClick.onClickListener(formaPagamento);

            row_index =  holder.getAdapterPosition() ;
            notifyDataSetChanged();
        });

        holder.rvCheck.setChecked(row_index == holder.getAdapterPosition());

    }

    @Override
    public int getItemCount() {
        return formaPagamentoList.size();
    }

    public interface onClick {
        void onClickListener(FormaPagamento formaPagamento);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        RadioButton rvCheck;


        TextView textNomePagamento, textDescricaoPagamento;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rvCheck = itemView.findViewById(R.id.rvCheck);
            textNomePagamento = itemView.findViewById(R.id.textNomePagamento);
            textDescricaoPagamento = itemView.findViewById(R.id.textDescricaoPagamento);

        }
    }
}
