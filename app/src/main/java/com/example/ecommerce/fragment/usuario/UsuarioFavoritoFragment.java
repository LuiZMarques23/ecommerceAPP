package com.example.ecommerce.fragment.usuario;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerce.R;
import com.example.ecommerce.adapter.CategoriaAdapter;
import com.example.ecommerce.adapter.LojaProdutoAdapter;
import com.example.ecommerce.databinding.FragmentUsuarioFavoritoBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.model.Favorito;
import com.example.ecommerce.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioFavoritoFragment extends Fragment implements LojaProdutoAdapter.OnClickFavorito, LojaProdutoAdapter.OnClickLister {

    private FragmentUsuarioFavoritoBinding binding;

    private final List<Produto> produtoList = new ArrayList<>();
    private final List<String> idsFavoritos = new ArrayList<>();

    private LojaProdutoAdapter lojaProdutoAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsuarioFavoritoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configRvProdtudos();
        recuperaFavoritos();
    }

    private void configRvProdtudos() {
        binding.rvProdutos.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvProdutos.setHasFixedSize(true);
        lojaProdutoAdapter = new LojaProdutoAdapter(produtoList, requireContext(), true, idsFavoritos, this, this);
        binding.rvProdutos.setAdapter(lojaProdutoAdapter);
    }

    private void recuperaFavoritos() {

        if (FirebaseHelper.getAutenticado()) {

            DatabaseReference favoritoRef = FirebaseHelper.getDatabaseReference()
                    .child("favoritos")
                    .child(FirebaseHelper.getIdFirebase());
            favoritoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    idsFavoritos.clear();

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String idFavoritos = ds.getValue(String.class);
                        idsFavoritos.add(idFavoritos);
                    }
                    Collections.reverse(idsFavoritos);
                    listEmpty();


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    private void recuperaProdutos() {
        for (int i = 0; i < idsFavoritos.size(); i++) {
            DatabaseReference favoritoRef = FirebaseHelper.getDatabaseReference()
                    .child("produtos")
                    .child(idsFavoritos.get(i));
            favoritoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Produto produto = snapshot.getValue(Produto.class);
                    produtoList.add(produto);

                    if (produtoList.size() == idsFavoritos.size()){
                        binding.progressBar.setVisibility(View.GONE);
                        lojaProdutoAdapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    private void listEmpty() {
        if (idsFavoritos.isEmpty()) {
            binding.progressBar.setVisibility(View.GONE);
            binding.textInfo.setText("Nenhum produto na sua lista de favoritos.");
        } else {

            binding.textInfo.setText("");


            recuperaProdutos();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(Produto produto) {

    }

    @Override
    public void onClickFavorito(String idProduto) {
        if (!idsFavoritos.contains(idProduto)) {
            idsFavoritos.add(idProduto);
        } else {
            idsFavoritos.remove(idProduto);
        }
        Favorito.salvar(idsFavoritos);
    }

}