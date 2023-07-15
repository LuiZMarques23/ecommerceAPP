package com.example.ecommerce.fragment.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.R;
import com.example.ecommerce.activiy.usuario.DetalhesProdutoActivity;
import com.example.ecommerce.adapter.CategoriaAdapter;
import com.example.ecommerce.adapter.LojaProdutoAdapter;
import com.example.ecommerce.databinding.FragmentUsuarioHomeBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.model.Categoria;
import com.example.ecommerce.model.Favorito;
import com.example.ecommerce.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioHomeFragment extends Fragment implements CategoriaAdapter.onClick, LojaProdutoAdapter.OnClickLister, LojaProdutoAdapter.OnClickFavorito {

    private FragmentUsuarioHomeBinding binding;
    private final List<Categoria> categoriaList = new ArrayList<>();
    private final List<Produto> produtoList = new ArrayList<>();
    private final List<String> idsFavoritos = new ArrayList<>();
    private CategoriaAdapter categoriaAdapter;
    private LojaProdutoAdapter lojaProdutoAdapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsuarioHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configRvCategorias();

        recuperaCategorias();

        recuperaFavoritos();

    }

    @Override
    public void onStart() {
        super.onStart();
        recuperaProdutos();
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
                       String idFavorito = ds.getValue(String.class);
                       idsFavoritos.add(idFavorito);
                   }

                   categoriaAdapter.notifyDataSetChanged();
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }

           });
       }
    }

    private void configRvCategorias(){
        binding.rvCategorias.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvCategorias.setHasFixedSize(true);
        categoriaAdapter = new CategoriaAdapter(R.layout.item_categoria_horizontal, true, categoriaList, this);
        binding.rvCategorias.setAdapter(categoriaAdapter);
    }

    private void recuperaCategorias(){
        DatabaseReference categoriaRef = FirebaseHelper.getDatabaseReference()
                .child("categoria");
        categoriaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                categoriaList.clear();

                for (DataSnapshot ds : snapshot.getChildren()){
                    Categoria categoria = ds.getValue(Categoria.class);
                    categoriaList.add(categoria);
                }

                Collections.reverse(categoriaList);
                categoriaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void configRvProdtudos(List<Produto> produtoList){
        binding.rvProdutos.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvProdutos.setHasFixedSize(true);
        lojaProdutoAdapter = new LojaProdutoAdapter(produtoList, requireContext(),true, idsFavoritos, this, this);
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
                    produtoList.add(produto);
                }
                listEmpty(produtoList);

                binding.progressBar.setVisibility(View.GONE);
                Collections.reverse(produtoList);


                configRvProdtudos(produtoList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void listEmpty(List<Produto> produtoList) {
        if (produtoList.isEmpty()) {
            binding.textInfo.setText("Nenhum produto localizado.");
        } else {
            binding.textInfo.setText("");
        }
    }


    @Override
    public void onClickListener(Categoria categoria) {
        Toast.makeText(requireContext(), categoria.getNome(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(Produto produto) {
        Intent intent = new Intent(requireContext(), DetalhesProdutoActivity.class);
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