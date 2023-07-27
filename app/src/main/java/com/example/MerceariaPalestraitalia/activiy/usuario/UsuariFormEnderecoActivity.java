package com.example.MerceariaPalestraitalia.activiy.usuario;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MerceariaPalestraitalia.databinding.ActivityUsuariFormEnderecoBinding;
import com.example.MerceariaPalestraitalia.model.Endereco;

public class UsuariFormEnderecoActivity extends AppCompatActivity {

    private ActivityUsuariFormEnderecoBinding binding;
    private Endereco endereco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuariFormEnderecoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciaComponentes();

        configClicks();
    }

    private void configClicks(){
        binding.include.include.ibVoltar.setOnClickListener(view -> finish());
        binding.include.btnSalvar.setOnClickListener(v -> validaDados());
    }

    private void validaDados(){
        String nomeEndereco = binding.edtNomeEndereco.getText().toString().trim();
        String cep = binding.edtCEP.getText().toString().trim();
        String uf = binding.edtUF.getText().toString().trim();
        String numero = binding.edtNumEndereco.getText().toString().trim();
        String logradouro = binding.edtLogradouro.getText().toString().trim();
        String bairro = binding.edtBairro.getText().toString().trim();
        String município = binding.edtMunicPio.getText().toString().trim();

        if (!nomeEndereco.isEmpty()){
            if (!cep.isEmpty()){
                if (!uf.isEmpty()){
                    if (!logradouro.isEmpty()){
                        if (!bairro.isEmpty()){
                            if (!município.isEmpty()){


                                ocultaTeclado();
                                binding.progressBar.setVisibility(View.VISIBLE);

                                if (endereco == null) endereco = new Endereco();
                                endereco.setNomeEndereco(nomeEndereco);
                                endereco.setCep(cep);
                                endereco.setUf(uf);
                                endereco.setNumero(numero);
                                endereco.setLogradouro(logradouro);
                                endereco.setBairro(bairro);
                                endereco.setLocalidade(município);

                                endereco.salvar();
                                finish();

                                binding.edtMunicPio.requestFocus();
                                binding.edtMunicPio.setError("Informação obrigatória!");
                            }

                        }else {
                            binding.edtBairro.requestFocus();
                            binding.edtBairro.setError("Informação obrigatória!");
                        }

                    }else {
                        binding.edtLogradouro.requestFocus();
                        binding.edtLogradouro.setError("Informação obrigatória!");
                    }

                }else {
                    binding.edtUF.requestFocus();
                    binding.edtUF.setError("Informação obrigatória!");
                }

            }else {
                binding.edtCEP.requestFocus();
                binding.edtCEP.setError("Informação obrigatória!");
            }

        }else {
            binding.edtNomeEndereco.requestFocus();
            binding.edtNomeEndereco.setError("Informação obrigatória!");
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