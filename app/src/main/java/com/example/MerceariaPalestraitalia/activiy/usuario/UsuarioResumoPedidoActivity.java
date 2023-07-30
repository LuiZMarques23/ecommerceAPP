package com.example.MerceariaPalestraitalia.activiy.usuario;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.MerceariaPalestraitalia.DAO.ItemDAO;
import com.example.MerceariaPalestraitalia.DAO.ItemPedidoDAO;
import com.example.MerceariaPalestraitalia.R;
import com.example.MerceariaPalestraitalia.databinding.ActivityUsuarioResumoPedidoBinding;
import com.example.MerceariaPalestraitalia.helper.FirebaseHelper;
import com.example.MerceariaPalestraitalia.model.Endereco;
import com.example.MerceariaPalestraitalia.util.GetMask;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioResumoPedidoActivity extends AppCompatActivity {

    private ActivityUsuarioResumoPedidoBinding binding;

    private List<Endereco>enderecoList = new ArrayList<>();

    private ItemDAO itemDAO;
    private ItemPedidoDAO itemPedidoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioResumoPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recuperaEndereco();

    }

    private void configDados(){
        itemDAO = new ItemDAO(this);
        itemPedidoDAO = new ItemPedidoDAO(this);

        binding.include.textTitulo.setText("Resumo pedido");
        binding.include.include.ibVoltar.setOnClickListener(view -> finish());

        if (!enderecoList.isEmpty()){
            Endereco endereco = enderecoList.get(0);

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

            binding.btnAlterarEndereco.setText("ALTERAR ENDEREÇO DE ENTREGA");

        }else {
            binding.textEnderecoEntrega.setText("Nenhum endereço cadastrado.");
            binding.btnAlterarEndereco.setText("Cadastra endereço.");
        }

        binding.textValorTotal.setText(getString(R.string.valor_total_carrinho, GetMask.getValor(itemPedidoDAO.getTotalPedido())));
        binding.textValor.setText(getString(R.string.valor_total_carrinho, GetMask.getValor(itemPedidoDAO.getTotalPedido())));
    }

    private void recuperaEndereco(){
        DatabaseReference enderecoRef = FirebaseHelper.getDatabaseReference()
                .child("enderecos")
                .child(FirebaseHelper.getIdFirebase());
        enderecoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()){
                    Endereco endereco = ds.getValue(Endereco.class);
                    enderecoList.add(endereco);
                }

                binding.progressBar.setVisibility(View.GONE);
                Collections.reverse(enderecoList);

                configDados();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}