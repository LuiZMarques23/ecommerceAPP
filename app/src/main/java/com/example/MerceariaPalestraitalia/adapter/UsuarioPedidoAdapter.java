package com.example.MerceariaPalestraitalia.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MerceariaPalestraitalia.R;
import com.example.MerceariaPalestraitalia.model.Pedido;
import com.example.MerceariaPalestraitalia.model.StatusPedido;
import com.example.MerceariaPalestraitalia.util.GetMask;

import java.util.List;

public class UsuarioPedidoAdapter extends RecyclerView.Adapter<UsuarioPedidoAdapter.MyViewHolder> {

    private final List<Pedido> pedidoList;
    private final Context context;
    private final OnClickListener clickListener;


    public UsuarioPedidoAdapter(List<Pedido> pedidoList, Context context, OnClickListener clickListener) {
        this.pedidoList = pedidoList;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario_pedido_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Pedido pedido = pedidoList.get(position);

        holder.textIdPedido.setText(pedido.getId());

        holder.textTotalPedido.setText(context.getString(R.string.valor, GetMask.getValor(pedido.getTotal())));
        holder.textDataPedido.setText(GetMask.getDate(pedido.getDataPedido(), 2));

        holder.btnDetalhePedido.setOnClickListener(view -> clickListener.onClick(pedido));

        switch (pedido.getStatusPedido()){
            case PENDENTE:
                holder.textStatusPedido.setTextColor(Color.parseColor("#FC6E20"));
                break;
            case APROVADO:
                holder.textStatusPedido.setTextColor(Color.parseColor("#FF4DFB41"));
                break;
            case CAMINHO:
                holder.textStatusPedido.setTextColor(Color.parseColor("#03A9F4"));
                break;
            case ENTREGUE:
                holder.textStatusPedido.setTextColor(Color.parseColor("#FF500CC7"));
                break;
            default:
                holder.textStatusPedido.setTextColor(Color.parseColor("#E94235"));
                break;
        }
        holder.textStatusPedido.setText(StatusPedido.getStatus(pedido.getStatusPedido()));

    }

    @Override
    public int getItemCount() {
        return pedidoList.size();
    }

    public interface OnClickListener{
        void onClick(Pedido pedido);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textIdPedido, textStatusPedido, textTotalPedido,textDataPedido;
        Button btnDetalhePedido;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textIdPedido = itemView.findViewById(R.id.textIdPedido);
            textStatusPedido = itemView.findViewById(R.id.textStatusPedido);
            textTotalPedido = itemView.findViewById(R.id.textTotalPedido);
            textDataPedido = itemView.findViewById(R.id.textDataPedido);
            btnDetalhePedido = itemView.findViewById(R.id.btnDetalhePedido);
        }
    }
}
