package com.example.MerceariaPalestraitalia.activiy.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.MerceariaPalestraitalia.R;
import com.example.MerceariaPalestraitalia.adapter.DetalhePedidoAdapter;
import com.example.MerceariaPalestraitalia.databinding.ActivityDetalhePedidoBinding;
import com.example.MerceariaPalestraitalia.model.Endereco;
import com.example.MerceariaPalestraitalia.model.Pedido;
import com.example.MerceariaPalestraitalia.util.GetMask;

public class DetalhePedidoActivity extends AppCompatActivity {

    private ActivityDetalhePedidoBinding binding;
    private Pedido pedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalhePedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciaComponetes();

        getExtra();

        configClick();

    }

    private void configRv(){
        binding.rvProdutos.setLayoutManager(new LinearLayoutManager(this));
        binding.rvProdutos.setHasFixedSize(true);
        DetalhePedidoAdapter detalhePedidoAdapter = new DetalhePedidoAdapter(pedido.getItemPedidos(), this);
        binding.rvProdutos.setAdapter(detalhePedidoAdapter);
    }

    private void configClick(){
        binding.include.include.ibVoltar.setOnClickListener(view -> finish());
    }

    private void getExtra(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            pedido = (Pedido) bundle.getSerializable("pedidoSelecionad");

            configRv();

            configDados();
        }
    }

    private void configDados(){

        Endereco endereco = pedido.getEndereco();
        StringBuilder enderecoCompleto = new StringBuilder();

        enderecoCompleto.append(endereco.getLogradouro())
                .append(", ")
                .append(endereco.getNumero())
                .append("\n")
                .append(endereco.getBairro())
                .append(", ")
                .append(endereco.getLocalidade())
                .append("/ ")
                .append(endereco.getUf())
                .append("\n")
                .append("CEP: ")
                .append(endereco.getCep());
        binding.textEnderecoEntrega.setText(enderecoCompleto);

        binding.textNomePagamento.setText(pedido.getPagamento());


        double valorExtra;
        double totalPedido = pedido.getTotal();
        if (pedido.getAcrescimo() >0){
            binding.textValorTipoPagamento.setText("Acréscimo");
            valorExtra = pedido.getAcrescimo();
            totalPedido += valorExtra;

        }else {
            binding.textValorTipoPagamento.setText("Desconto");
            valorExtra = pedido.getDesconto();
            totalPedido -= valorExtra;

        }

        binding.textValorTipoPagamento.setText(
                getString(R.string.valor, GetMask.getValor(valorExtra))
        );

        binding.textValorProdutos.setText(
                getString(R.string.valor, GetMask.getValor(pedido.getTotal()))
        );

        binding.textValorTotal.setText(
                getString(R.string.valor, GetMask.getValor(totalPedido))
        );


    }

    private void iniciaComponetes(){
        binding.include.textTitulo.setText("Detalhe do pedido");
    }
}