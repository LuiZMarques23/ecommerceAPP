package com.example.MerceariaPalestraitalia.autenticacao;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MerceariaPalestraitalia.databinding.ActivityCadastroBinding;
import com.example.MerceariaPalestraitalia.helper.FirebaseHelper;
import com.example.MerceariaPalestraitalia.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private ActivityCadastroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        confiCliques();
        corStatusBar();
    }

    public void validaDados(View view){
        String nome = binding.editNome.getText().toString().trim();
        String email = binding.editEmail.getText().toString().trim();
        String telefone = binding.edtTelefone.getMasked();
        String senha = binding.editSenha.getText().toString().trim();
        String confirmaSenha = binding.editConfirmaSenha.getText().toString().trim();

        if (!nome.isEmpty()){
            if (!email.isEmpty()){
               if (telefone.isEmpty()){
                   if (telefone.length() == 15){
                       if (!senha.isEmpty()){
                           if (!confirmaSenha.isEmpty()){

                               if (senha.equals(confirmaSenha)){

                                   binding.progressBar.setVisibility(View.VISIBLE);



                                   Usuario usuario = new Usuario();
                                   usuario.setNome(nome);
                                   usuario.setEmail(email);
                                   usuario.setTelefone(telefone);
                                   usuario.setSenha(senha);

                                   criarConta(usuario);


                               }else {
                                   binding.editConfirmaSenha.requestFocus();
                                   binding.editConfirmaSenha.setError("Senha não confere.");
                               }

                           }else {
                               binding.editConfirmaSenha.requestFocus();
                               binding.editConfirmaSenha.setError("Confirma sua senha.");
                           }

                       }else {
                           binding.editSenha.requestFocus();
                           binding.editSenha.setError("Informe sua senha.");
                       }
                   }else {
                       binding.edtTelefone.requestFocus();
                       binding.edtTelefone.setError("Formato do telefone inválido!");
                   }
               }else {
                   binding.edtTelefone.requestFocus();
                   binding.edtTelefone.setError("Informe um numero de telefone!");
               }

            }else {
                binding.editEmail.requestFocus();
                binding.editEmail.setError("Infome seu email.");
            }

        }else {
            binding.editNome.requestFocus();
            binding.editNome.setError("Informe seu nome.");

        }

    }


    private void criarConta(Usuario usuario){
        FirebaseHelper.getAuth().createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(task -> {

            if (task.isSuccessful()){

                String id = task.getResult().getUser().getUid();

                usuario.setId(id);
                usuario.salvar();

                Intent intent = new Intent();
                intent.putExtra("email",usuario.getEmail());
                setResult(RESULT_OK, intent);
                finish();

            }else {
                Toast.makeText(this, FirebaseHelper.validaErro(task.getException().getMessage()), Toast.LENGTH_SHORT).show();
            }

            binding.progressBar.setVisibility(View.GONE);

        });
    }

    private void corStatusBar(){
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
    }


    private void confiCliques(){
        binding.include.ibVoltar.setOnClickListener(view -> finish());
        binding.btnLogin.setOnClickListener(view -> finish());
    }
}