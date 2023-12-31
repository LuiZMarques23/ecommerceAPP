package com.example.MerceariaPalestraitalia.fragment.usuario;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.MerceariaPalestraitalia.DAO.ItemDAO;
import com.example.MerceariaPalestraitalia.DAO.ItemPedidoDAO;
import com.example.MerceariaPalestraitalia.R;
import com.example.MerceariaPalestraitalia.activiy.usuario.UsuarioSelecionaPagamentoActivity;
import com.example.MerceariaPalestraitalia.adapter.CarrinhoAdapter;
import com.example.MerceariaPalestraitalia.autenticacao.LoginActivity;
import com.example.MerceariaPalestraitalia.databinding.DialogRemoverCarrinhoBinding;
import com.example.MerceariaPalestraitalia.databinding.FragmentUsuarioCarinhoBinding;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioCarinhoFragment extends Fragment implements CarrinhoAdapter.OnClick {

    private FragmentUsuarioCarinhoBinding binding;
    private final List<ItemPedido> itemPedidoList = new ArrayList<>();
    private final List<String> idsFavoritos = new ArrayList<>();
    private CarrinhoAdapter carrinhoAdapter;
    private ItemDAO itemDAO;
    private ItemPedidoDAO itemPedidoDAO;

    private AlertDialog dialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsuarioCarinhoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       recuperaDados();
    }

    private void recuperaDados(){
        itemDAO = new ItemDAO(requireContext());
        itemPedidoDAO = new ItemPedidoDAO(requireContext());
        itemPedidoList.addAll(itemPedidoDAO.getList());

        configRv();
        recuperaFavoritos();
        configCliks();
    }

    @Override
    public void onStart() {
        super.onStart();

        configInfo();
    }

    private void configCliks(){
        binding.btnContinuar.setOnClickListener(view -> {
            if (FirebaseHelper.getAutenticado()){
                startActivity(new Intent(requireContext(), UsuarioSelecionaPagamentoActivity.class));

            }else {
                resultLauncher.launch(new Intent(requireContext(), LoginActivity.class));
            }
        });
    }

    private void configRv(){
        Collections.reverse(itemPedidoList);
        binding.rvProdutos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvProdutos.setHasFixedSize(true);
        carrinhoAdapter = new CarrinhoAdapter(itemPedidoList, itemPedidoDAO, requireContext(), this);
        binding.rvProdutos.setAdapter(carrinhoAdapter);

        configTotalCarrinho();
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
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    }

    private void configTotalCarrinho(){
        binding.textValor.setText(getString(R.string.valor_total_carrinho, GetMask.getValor(itemPedidoDAO.getTotalPedido())));
    }

    private void configQtdProduto(int position, String opercao ){
        ItemPedido itemPedido = itemPedidoList.get(position);

        if (opercao.equals("mais")){ // +

            itemPedido.setQuantidade(itemPedido.getQuantidade() + 1);

            itemPedidoDAO.atualizar(itemPedido);

            itemPedidoList.set(position, itemPedido);

        }else { // -

            if (itemPedido.getQuantidade() > 1){

                itemPedido.setQuantidade(itemPedido.getQuantidade() - 1);

                itemPedidoDAO.atualizar(itemPedido);

                itemPedidoList.set(position, itemPedido);

            }

        }

        carrinhoAdapter.notifyDataSetChanged();
        configTotalCarrinho();

    }

    private void showDialogRemover(Produto produto, int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);

        DialogRemoverCarrinhoBinding dialogBinding = DialogRemoverCarrinhoBinding
                .inflate(LayoutInflater.from(requireContext()));
        dialogBinding.likeButtom.setLiked(idsFavoritos.contains(produto.getId()));

        dialogBinding.likeButtom.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (FirebaseHelper.getAutenticado()){
                    salvarFavorito(produto);
                }else {
                    Toast.makeText(requireContext(), "Voçê não esta autenticado no app.", Toast.LENGTH_SHORT).show();
                    dialogBinding.likeButtom.setLiked(false);

                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                salvarFavorito(produto);

            }
        });

        Glide.with(requireContext())
                .load(produto.getUrlImagens().get(0).getCaminhoImagem())
                .centerCrop()
                .into(dialogBinding.imagemProduto);

        dialogBinding.textNomeProduto.setText(produto.getTitulo());
        dialogBinding.btnCancelar.setOnClickListener(view -> dialog.dismiss());

        dialogBinding.btnAddFavorito.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // tecla remover
        dialogBinding.btnRemove.setOnClickListener(v -> {
            removerProdutoCarrinho(position);
            dialog.dismiss();
            Toast.makeText(requireContext(), "Produto removido com sucesso!", Toast.LENGTH_SHORT).show();

        });



        builder.setView(dialogBinding.getRoot());

        dialog = builder.create();
        dialog.show();

    }

    private void salvarFavorito(Produto produto){
        if (!idsFavoritos.contains(produto.getId())){
            idsFavoritos.add(produto.getId());
        }else {
            idsFavoritos.remove(produto.getId());
        }
        Favorito.salvar(idsFavoritos);
    }

    private void removerProdutoCarrinho(int position){
        ItemPedido itemPedido = itemPedidoList.get(position);

        itemPedidoList.remove(itemPedido);

        itemPedidoDAO.remover(itemPedido);

        itemDAO.remover(itemPedido);

        carrinhoAdapter.notifyDataSetChanged();

        configInfo();

        configTotalCarrinho();



    }

    private void configInfo(){
        if (itemPedidoList.isEmpty()){
            binding.textInfo.setVisibility(View.VISIBLE);
        }else {
            binding.textInfo.setVisibility(View.GONE);
        }
    }

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    startActivity(new Intent(requireContext(), UsuarioSelecionaPagamentoActivity.class));
                }

            }
    );

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding  = null;

    }

    @Override
    public void onClickLister(int position, String opercao) {

        int idProduto = itemPedidoList.get(position).getId();
        Produto produto = itemPedidoDAO.getProduto(idProduto);

        switch (opercao){
            case "detalhe":
                break;
            case "remover":
                showDialogRemover(produto, position);
                break;
            case "menos":
            case "mais":
                configQtdProduto(position, opercao);
                break;



        }

    }
}