package com.example.MerceariaPalestraitalia.activiy.usuario;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.MerceariaPalestraitalia.api.CEPService;
import com.example.MerceariaPalestraitalia.databinding.ActivityUsuariFormEnderecoBinding;
import com.example.MerceariaPalestraitalia.model.Endereco;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsuariFormEnderecoActivity extends AppCompatActivity {

    private ActivityUsuariFormEnderecoBinding binding;
    private Endereco endereco;
    private boolean novoEndereco = true;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuariFormEnderecoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));

        iniciaComponentes();

        configClicks();

        getExtra();

        iniciaRetrofit();
    }

    private void getExtra(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            endereco = (Endereco) bundle.getSerializable("enderecoSelecionado");

            configDados();
            novoEndereco = false;
        }
    }

    private void iniciaRetrofit(){
        retrofit = new Retrofit
                .Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void configDados(){
        binding.edtNomeEndereco.setText(endereco.getNomeEndereco());
        binding.edtCEP.setText(endereco.getCep());
        binding.edtUF.setText(endereco.getUf());
        binding.edtNumEndereco.setText(endereco.getNumero());
        binding.edtLogradouro.setText(endereco.getLogradouro());
        binding.edtBairro.setText(endereco.getBairro());
        binding.edtMunicPio.setText(endereco.getLocalidade());
    }

    private void configClicks(){
        binding.include.include.ibVoltar.setOnClickListener(view -> finish());
        binding.include.btnSalvar.setOnClickListener(v -> validaDados());
        binding.btnBuscar.setOnClickListener(view -> buscarCEP());
    }

    private void buscarCEP(){
        String cep = binding.edtCEP.getMasked().replace("-", "").replaceAll("_","");

        if (cep.length() == 8){

            ocultaTeclado();

            binding.progressBar.setVisibility(View.VISIBLE);

            CEPService cepService = retrofit.create(CEPService.class);
            Call<Endereco> call = cepService.recuperarCEP(cep);

            call.enqueue(new Callback<Endereco>() {
                @Override
                public void onResponse(@NonNull Call<Endereco> call, @NonNull Response<Endereco> response) {
                    if (response.isSuccessful()){

                        String nomeEndereco = "";
                        String numEndereco = "";
                        String idEndereco = "";
                        if (!novoEndereco){
                            nomeEndereco = endereco.getNomeEndereco();
                            numEndereco = endereco.getNumero();
                            idEndereco = endereco.getId();
                        }

                        endereco = response.body();

                        if (endereco != null){
                            if (endereco.getLocalidade() != null){

                                if (!novoEndereco)endereco.setId(idEndereco);
                                endereco.setNomeEndereco(nomeEndereco);
                                endereco.setNumero(numEndereco);

                                configDados();

                            }else {
                                Toast.makeText(getBaseContext(), "Não foi possivel recuperar o endereço.", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(getBaseContext(), "Não foi possivel recuperar o endereço.", Toast.LENGTH_SHORT).show();
                        }

                        binding.progressBar.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onFailure(@NonNull Call<Endereco> call, @NonNull Throwable t) {
                    Toast.makeText(getBaseContext(), "Não foi possivel recuperar o endereço.", Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);

                }
            });

        }else {
            Toast.makeText(this, "Formato do cep inválido.", Toast.LENGTH_SHORT).show();
        }

    }

    private void validaDados(){
        String nomeEndereco = binding.edtNomeEndereco.getText().toString().trim();
        String cep = binding.edtCEP.getMasked();
        String uf = binding.edtUF.getText().toString().trim();
        String numero = binding.edtNumEndereco.getText().toString().trim();
        String logradouro = binding.edtLogradouro.getText().toString().trim();
        String bairro = binding.edtBairro.getText().toString().trim();
        String municipio = binding.edtMunicPio.getText().toString().trim();

        if (!nomeEndereco.isEmpty()){
            if (!cep.isEmpty()){
                if (!uf.isEmpty()){
                    if (!logradouro.isEmpty()){
                        if (!bairro.isEmpty()){
                            if (!municipio.isEmpty()){


                                ocultaTeclado();
                                binding.progressBar.setVisibility(View.VISIBLE);

                                if (endereco == null) endereco = new Endereco();
                                endereco.setNomeEndereco(nomeEndereco);
                                endereco.setCep(cep);
                                endereco.setUf(uf);
                                endereco.setNumero(numero);
                                endereco.setLogradouro(logradouro);
                                endereco.setBairro(bairro);
                                endereco.setLocalidade(municipio);

                                endereco.salvar();
                                binding.progressBar.setVisibility(View.GONE);

                                if (novoEndereco){
                                    Intent intent = new Intent();
                                    intent.putExtra("enderecoCadastrado", endereco);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }

                            }else {
                                binding.edtMunicPio.requestFocus();
                                binding.edtMunicPio.setError("Digite seu Município!");
                            }

                        }else {
                            binding.edtBairro.requestFocus();
                            binding.edtBairro.setError("Digite Seu bairro!");
                        }

                    }else {
                        binding.edtLogradouro.requestFocus();
                        binding.edtLogradouro.setError("Digite seu logradouro!");
                    }

                }else {
                    binding.edtUF.requestFocus();
                    binding.edtUF.setError("Digite seu UF!");
                }

            }else {
                binding.edtCEP.requestFocus();
                binding.edtCEP.setError("Digite seu CEP!");
            }

        }else {
            binding.edtNomeEndereco.requestFocus();
            binding.edtNomeEndereco.setError("Digite seu endereço!");
        }
    }

    // Ocultar teclado do dispositivo
    private void ocultaTeclado(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(binding.edtNomeEndereco.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void iniciaComponentes(){
        binding.include.textTitulo.setText("Novo endereços");

    }
}