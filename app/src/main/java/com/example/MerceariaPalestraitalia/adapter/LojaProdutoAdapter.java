package com.example.MerceariaPalestraitalia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MerceariaPalestraitalia.R;
import com.example.MerceariaPalestraitalia.helper.FirebaseHelper;
import com.example.MerceariaPalestraitalia.model.Produto;
import com.example.MerceariaPalestraitalia.util.GetMask;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LojaProdutoAdapter extends RecyclerView.Adapter<LojaProdutoAdapter.MyViewHolder> {

    private final int layout;
    private final List<Produto> produtoList;
    private final Context context;
    private final boolean favorito;
    private final List<String> idsFavoritos;
    private final OnClickLister onClickLister;
    private final OnClickFavorito onClickFavorito;


    public LojaProdutoAdapter(int layout, List<Produto> produtoList, Context context, boolean favorito, List<String> idsFavoritos, OnClickLister onClickLister, OnClickFavorito onClickFavorito) {
        this.layout = layout;
        this.produtoList = produtoList;
        this.context = context;
        this.favorito = favorito;
        this.idsFavoritos = idsFavoritos;
        this.onClickLister = onClickLister;
        this.onClickFavorito = onClickFavorito;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout,parent, false);
        return new MyViewHolder(itemView) ;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Produto produto = produtoList.get(position);


        holder.textNomeProduto.setText(produto.getTitulo());

        if (favorito){
            if (idsFavoritos.contains(produto.getId())){
                holder.likeButton.setLiked(true);
            }
        }else {
            holder.likeButton.setVisibility(View.GONE);
        }



        if (produto.getValorAntigo() > 0){

            double resto = produto.getValorAntigo() - produto.getValorAtual();
            int porcentagem = (int) (resto / produto.getValorAntigo() * 100);

            if (porcentagem >= 10){
                holder.txtDescontoProduto.setText(context.getString(R.string.valor_off, porcentagem, "%"));
            }else {
                String porcent = String.valueOf(porcentagem).replace("0", "");
                holder.txtDescontoProduto.setText(context.getString(R.string.valor_off, Integer.parseInt(porcent), "%"));
            }

        }else {
            holder.txtDescontoProduto.setVisibility(View.GONE);
        }

        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (FirebaseHelper.getAutenticado()){
                    onClickFavorito.onClickFavorito(produto);
                }else {
                    Toast.makeText(context, "Voçê não esta autenticado no app.", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                onClickFavorito.onClickFavorito(produto);

            }
        });




        for (int i = 0; i < produto.getUrlImagens().size(); i++) {
            if (produto.getUrlImagens().get(i).getIndex() == 0 ){
                Picasso.get().load(produto.getUrlImagens().get(i).getCaminhoImagem()).into(holder.imagemProduto);

            }
        }
        holder.txtValorProduto.setText(context.getString(R.string.valor, GetMask.getValor(produto.getValorAtual())));
        holder.itemView.setOnClickListener(v -> onClickLister.onClick(produto));

    }

    @Override
    public int getItemCount() {
        return produtoList.size();
    }

    public interface OnClickLister{
        void onClick(Produto produto);
    }

    public interface OnClickFavorito{
        void onClickFavorito(Produto produto);
    }



    static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imagemProduto;
        TextView textNomeProduto, txtValorProduto, txtDescontoProduto;
        LikeButton likeButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagemProduto = itemView.findViewById(R.id.imagemProduto);
            textNomeProduto = itemView.findViewById(R.id.textNomeProduto);
            txtValorProduto = itemView.findViewById(R.id.txtValorProduto);
            txtDescontoProduto = itemView.findViewById(R.id.txtDescontoProduto);
            likeButton = itemView.findViewById(R.id.likeButtom);
        }
    }
}
