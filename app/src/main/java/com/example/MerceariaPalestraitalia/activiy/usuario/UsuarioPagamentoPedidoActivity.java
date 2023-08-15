package com.example.MerceariaPalestraitalia.activiy.usuario;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.MerceariaPalestraitalia.DAO.ItemDAO;
import com.example.MerceariaPalestraitalia.DAO.ItemPedidoDAO;
import com.example.MerceariaPalestraitalia.api.MercadoPagoService;
import com.example.MerceariaPalestraitalia.databinding.ActivityUsuarioSelecionaPagamentoBinding;
import com.example.MerceariaPalestraitalia.helper.FirebaseHelper;
import com.example.MerceariaPalestraitalia.model.Endereco;
import com.example.MerceariaPalestraitalia.model.ItemPedido;
import com.example.MerceariaPalestraitalia.model.Loja;
import com.example.MerceariaPalestraitalia.model.Produto;
import com.example.MerceariaPalestraitalia.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mercadopago.android.px.configuration.AdvancedConfiguration;
import com.mercadopago.android.px.core.MercadoPagoCheckout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsuarioPagamentoPedidoActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 100;

    private ActivityUsuarioSelecionaPagamentoBinding binding;

    private Endereco enderecoSelecionado;
    private Usuario usuario;
    private Loja loja;
    private ItemPedidoDAO itemPedidoDAO;
    private ItemDAO itemDAO;
    private List<ItemPedido> itemPedidoList = new ArrayList<>();
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioSelecionaPagamentoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recuperaDados();

        iniciaRetrofit();
    }

    private void configJSON(){
        JsonObject dados = new JsonObject();

        JsonArray itemsList = new JsonArray();

        JsonObject payer = new JsonObject();
        JsonObject phone = new JsonObject();
        JsonObject address = new JsonObject();
        JsonObject payment_methods = new JsonObject();


        String telefone = usuario.getTelefone()
                .replace(")","")
                .replace("(","")
                .replace("(","");
        phone.addProperty("area_code", telefone.substring(0, 2));
        phone.addProperty("number", telefone.substring(2, 12));

        address.addProperty("street_name", enderecoSelecionado.getLogradouro());
        if (enderecoSelecionado.getNumero() != null){
            address.addProperty("street_number", enderecoSelecionado.getNumero());
        }
        address.addProperty("zip_code", enderecoSelecionado.getCep());

        payment_methods.addProperty("installments", loja.getParcelas());

        JsonObject item;
        for (ItemPedido itemPedido : itemPedidoList){
            Produto produto = itemPedidoDAO.getProduto(itemPedido.getId());

            item = new JsonObject();

            item.addProperty("title", produto.getTitulo());
            item.addProperty("currency_id", "BRL");
            item.addProperty("picture_url", produto.getUrlImagens().get(0).getCaminhoImagem());
            item.addProperty("description", produto.getDescricao());
            item.addProperty("quantity", itemPedido.getQuantidade());
            item.addProperty("unit_price", produto.getValorAtual());

            itemsList.add(item);

            itemPedido.setNomeProduto(produto.getTitulo());


        }

        dados.add("items", itemsList);

        payer.addProperty("name", usuario.getNome());
        payer.addProperty("email", usuario.getEmail());
        payer.add("phone", phone);
        payer.add("address", address);

        dados.add("payer", payer);
        dados.add("payment_methods", payment_methods);

        efetuarPagamento(dados);



    }

    private void iniciaRetrofit(){
        retrofit = new Retrofit
                .Builder()
                .baseUrl("https://api.mercadopago.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void efetuarPagamento(JsonObject dados){
        String url = "checkout/preferences?access_token=" + loja.getAccessToken();

        MercadoPagoService mercadoPagoService = retrofit.create(MercadoPagoService.class);
        Call<JsonObject> call = mercadoPagoService.efetuarPagamento(url, dados);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                Log.i("INFOTESTE", "onResponse:" + response.body());
//                String id = response.body().get("id").getAsString();
//                continuaPagamento(id);



            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

            }
        });
    }

    private void continuaPagamento(String idPagamento) {
        final AdvancedConfiguration advancedConfiguration =
                new AdvancedConfiguration.Builder().setBankDealsEnabled(false).build();

        new MercadoPagoCheckout
                .Builder(loja.getPublickey(), idPagamento)
                .setAdvancedConfiguration(advancedConfiguration).build()
                .startPayment(this, REQUEST_CODE);
    }
    private void recuperaDados(){
        itemPedidoDAO = new ItemPedidoDAO(this);
        itemDAO = new ItemDAO(this);
        itemPedidoList = itemPedidoDAO.getList();

        recuperaUsuario();

        getExtra();
        corStatusBar();
    }
    private void getExtra(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            enderecoSelecionado = (Endereco) bundle.getSerializable("enderecoSelecionado");
        }
    }
    private void recuperaUsuario(){
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(FirebaseHelper.getIdFirebase());
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuario = snapshot.getValue(Usuario.class);

                if (usuario != null){
                    recuperaLoja();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void recuperaLoja(){
        DatabaseReference lojaRef = FirebaseHelper.getDatabaseReference()
                .child("loja");
        lojaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loja = snapshot.getValue(Loja.class);

                if (loja != null){
                    configJSON();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void corStatusBar(){
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
    }
}