package com.example.MerceariaPalestraitalia.fragment.usuario;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.MerceariaPalestraitalia.DAO.ItemDAO;
import com.example.MerceariaPalestraitalia.DAO.ItemPedidoDAO;
import com.example.MerceariaPalestraitalia.R;
import com.example.MerceariaPalestraitalia.adapter.CarrinhoAdapter;
import com.example.MerceariaPalestraitalia.databinding.FragmentUsuarioCarinhoBinding;
import com.example.MerceariaPalestraitalia.model.ItemPedido;
import com.example.MerceariaPalestraitalia.util.GetMask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioCarinhoFragment extends Fragment implements CarrinhoAdapter.OnClick {

    private FragmentUsuarioCarinhoBinding binding;
    private List<ItemPedido> itemPedidoList = new ArrayList<>();
    private CarrinhoAdapter carrinhoAdapter;
    private ItemDAO itemDAO;
    private ItemPedidoDAO itemPedidoDAO;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsuarioCarinhoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemDAO = new ItemDAO(requireContext());
        itemPedidoDAO = new ItemPedidoDAO(requireContext());
        itemPedidoList.addAll(itemPedidoDAO.getList());

        configRv();
    }

    private void configRv(){
        Collections.reverse(itemPedidoList);
        binding.rvProdutos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvProdutos.setHasFixedSize(true);
        carrinhoAdapter = new CarrinhoAdapter(itemPedidoList, itemPedidoDAO, requireContext(), this);
        binding.rvProdutos.setAdapter(carrinhoAdapter);

        configSaldoCarrinho();
    }

    private void configSaldoCarrinho(){
        binding.textValor.setText(getString(R.string.valor_total_carrinho, GetMask.getValor(itemPedidoDAO.getTotalCarinho())));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding  = null;

    }

    @Override
    public void onClickLister(int position, String opercao) {

    }
}