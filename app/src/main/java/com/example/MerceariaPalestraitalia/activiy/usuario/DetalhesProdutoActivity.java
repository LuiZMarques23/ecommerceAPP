package com.example.MerceariaPalestraitalia.activiy.usuario;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.MerceariaPalestraitalia.DAO.ItemDAO;
import com.example.MerceariaPalestraitalia.DAO.ItemPedidoDAO;
import com.example.MerceariaPalestraitalia.R;
import com.example.MerceariaPalestraitalia.adapter.LojaProdutoAdapter;
import com.example.MerceariaPalestraitalia.adapter.SliderAdapter;
import com.example.MerceariaPalestraitalia.databinding.ActivityDetalhesProdutoBinding;
import com.example.MerceariaPalestraitalia.databinding.DialogAddItemCarrinhoBinding;
import com.example.MerceariaPalestraitalia.helper.FirebaseHelper;
import com.example.MerceariaPalestraitalia.model.Favorito;
import com.example.MerceariaPalestraitalia.model.ItemPedido;
import com.example.MerceariaPalestraitalia.model.Produto;
import com.example.MerceariaPalestraitalia.util.GetMask;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetalhesProdutoActivity extends AppCompatActivity implements LojaProdutoAdapter.OnClickLister, LojaProdutoAdapter.OnClickFavorito {

    private ActivityDetalhesProdutoBinding binding;
    private final List<String> idsFavoritos = new ArrayList<>();
    private final List<Produto> produtoList = new ArrayList<>();
    private LojaProdutoAdapter lojaProdutoAdapter;

    private Produto produtoSelecionado;

    private ItemDAO itemDAO;
    private ItemPedidoDAO itemPedidoDAO;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalhesProdutoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recuperaDados();
    }
    private void recuperaDados(){
        itemDAO = new ItemDAO(this);
        itemPedidoDAO = new ItemPedidoDAO(this);

        configClicks();

        getExtra();

        recuperaFavoritos();

        corStatusBar();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) dialog.dismiss();
    }

    private void configClicks(){
        binding.include.textTitulo.setText("Detalhes do produto");
        binding.include.include.ibVoltar.setOnClickListener(view -> finish());

        binding.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (FirebaseHelper.getAutenticado()){
                    idsFavoritos.add(produtoSelecionado.getId());
                    Favorito.salvar(idsFavoritos);

                }else {
                    Toast.makeText(getBaseContext(), "Voçê não esta autenticado no app.", Toast.LENGTH_SHORT).show();
                    binding.likeButton.setLiked(false);
                }

            }

            @Override
            public void unLiked(LikeButton likeButton) {
                idsFavoritos.remove(produtoSelecionado.getId());
                Favorito.salvar(idsFavoritos);


            }
        });

        binding.btnAddCarinho.setOnClickListener(view -> showDialogCarrinho());
    }

    private void addCarrinho(){
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setIdProduto(produtoSelecionado.getId());
        itemPedido.setQuantidade(1);
        itemPedido.setValor(produtoSelecionado.getValorAtual());

        itemPedidoDAO.salvar(itemPedido);

        itemDAO.salvar(produtoSelecionado);
    }

    private void corStatusBar(){
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
    }

    private void configRvProdtudos(List<Produto> produtoList){
        binding.rvProdutos.setLayoutManager(new GridLayoutManager(this,1, LinearLayoutManager.HORIZONTAL,false));
        binding.rvProdutos.setHasFixedSize(true);
        lojaProdutoAdapter = new LojaProdutoAdapter(R.layout.item_produto_similar_adapter,produtoList, this,true, idsFavoritos, this, this);
        binding.rvProdutos.setAdapter(lojaProdutoAdapter);
    }

    private void recuperaProdutos(){
        DatabaseReference produtoRef = FirebaseHelper.getDatabaseReference()
                .child("produtos");
        produtoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produtoList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    Produto produto = ds.getValue(Produto.class);

                    for (String categoria : produtoSelecionado.getIdsCategorias()){
                        if (produto.getIdsCategorias().contains(categoria)){
                            if (!produtoList.contains(produto) && !produto.getId().equals(produtoSelecionado.getId())){
                                produtoList.add(produto);

                            }

                        }

                    }
                }

                Collections.reverse(produtoList);
                configRvProdtudos(produtoList);
                lojaProdutoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperaFavoritos(){
        if (FirebaseHelper.getAutenticado()){
            DatabaseReference favoritoRef = FirebaseHelper.getDatabaseReference()
                    .child("favoritos")
                    .child(FirebaseHelper.getIdFirebase());
            favoritoRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    idsFavoritos.clear();

                    for (DataSnapshot ds : snapshot.getChildren()){
                        String idFavoritos = ds.getValue(String.class);
                        idsFavoritos.add(idFavoritos);
                    }
                    binding.likeButton.setLiked(idsFavoritos.contains(produtoSelecionado.getId()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    }

    private void getExtra(){
        produtoSelecionado = (Produto) getIntent().getSerializableExtra("produtoSelecionado");

        configDados();
        recuperaProdutos();

    }
    private void configDados(){
        binding.sliderView.setSliderAdapter(new SliderAdapter(produtoSelecionado.getUrlImagens(), this));
        binding.sliderView.startAutoCycle();
        binding.sliderView.setScrollTimeInSec(4);
        binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);

        binding.textProduto.setText(produtoSelecionado.getTitulo());
        binding.textDescricao.setText(produtoSelecionado.getDescricao());
        binding.textValor.setText(getString(R.string.valor, GetMask.getValor(produtoSelecionado.getValorAtual())));


    }

    private void showDialogCarrinho(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);

        DialogAddItemCarrinhoBinding dialogBinding = DialogAddItemCarrinhoBinding
                .inflate(LayoutInflater.from(this));

        addCarrinho();

        dialogBinding.btnFechar.setOnClickListener(view -> dialog.dismiss());

        dialogBinding.btnIrcarrinho.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivityUsuario.class);
            intent.putExtra("id", 2);
            startActivity(intent);
            finish();

        });

        builder.setView(dialogBinding.getRoot());

        dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onClick(Produto produto) {
        Intent intent = new Intent(this, DetalhesProdutoActivity.class);
        intent.putExtra("produtoSelecionado", produto);
        startActivity(intent);

    }

    @Override
    public void onClickFavorito(Produto produto) {
        if (!idsFavoritos.contains(produto.getId())){
            idsFavoritos.add(produto.getId());
        }else {
            idsFavoritos.remove(produto.getId());
        }
        Favorito.salvar(idsFavoritos);
    }
}