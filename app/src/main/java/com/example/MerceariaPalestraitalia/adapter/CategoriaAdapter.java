package com.example.MerceariaPalestraitalia.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.MerceariaPalestraitalia.R;
import com.example.MerceariaPalestraitalia.model.Categoria;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.MyViewHolder> {

    private final int layout;
    private final boolean background;
    private final List<Categoria> categoriaList;
    private final onClick onClick;
    private final Context context;
    private int row_index = 0;

    public CategoriaAdapter(int layout, boolean background, List<Categoria> categoriaList, CategoriaAdapter.onClick onClick, Context context) {
        this.layout = layout;
        this.background = background;
        this.categoriaList = categoriaList;
        this.onClick = onClick;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Categoria categoria = categoriaList.get(position);

        if (background){

            holder.itemView.setOnClickListener(v -> {
                onClick.onClickListener(categoria);

                row_index =  holder.getAdapterPosition() ;
                notifyDataSetChanged();
            });

            if (row_index ==  holder.getAdapterPosition() ){
                holder.itemView.setBackgroundResource(R.drawable.bg_categori_home);
                holder.nomeCategoria.setTextColor(Color.parseColor("#FF000000"));
                holder.imagemCategoria.setColorFilter(Color.parseColor("#FF000000"), PorterDuff.Mode.SRC_IN);
            }else {

                holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                holder.nomeCategoria.setTextColor(Color.parseColor("#808080"));
                holder.imagemCategoria.setColorFilter(Color.parseColor("#03A9F4"), PorterDuff.Mode.SRC_IN);

            }
        }else {
            holder.itemView.setOnClickListener(v -> onClick.onClickListener(categoria));

        }

        holder.nomeCategoria.setText(categoria.getNome());


        Glide.with(context)
                .load(categoria.getUrlImagem())
                .centerCrop()
                .into(holder.imagemCategoria);


    }

    @Override
    public int getItemCount() {
        return categoriaList.size();
    }

    public interface onClick {
        void onClickListener(Categoria categoria);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imagemCategoria;
        TextView nomeCategoria;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagemCategoria = itemView.findViewById(R.id.imagemCategoria);
            nomeCategoria = itemView.findViewById(R.id.nomeCategoria);
        }
    }
}
