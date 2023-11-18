package com.example.MerceariaPalestraitalia.autenticacao;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.MerceariaPalestraitalia.activiy.loja.MainActivityEmpresa;
import com.example.MerceariaPalestraitalia.databinding.ActivityLoginBinding;
import com.example.MerceariaPalestraitalia.helper.FirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    String email = result.getData().getStringExtra("email");
                    binding.edtEmail.setText(email);
                }

            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(Color.parseColor("#03A9F4"));

        configCliques();

    }

    public void validaDados(View view) {
        String email = binding.edtEmail.getText().toString().trim();
        String senha = binding.edtSenha.getText().toString().trim();


        if (!email.isEmpty()){
            if (!senha.isEmpty()){

                ocultaTeclado();

                binding.progressBar.setVisibility(View.VISIBLE);

                Login(email, senha);

            }else {
                binding.edtSenha.requestFocus();
                binding.edtSenha.setError("Informe sua senha.");
            }

        }else {
            binding.edtEmail.requestFocus();
            binding.edtEmail.setError("Infome seu email.");
        }

    }

    private void Login(String email, String senha){
        FirebaseHelper.getAuth().signInWithEmailAndPassword(
                email, senha
        ).addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               recuperaUsuario(task.getResult().getUser().getUid());

           }else {
               binding.progressBar.setVisibility(View.GONE);
               Toast.makeText(this, FirebaseHelper.validaErro(task.getException().getMessage()), Toast.LENGTH_SHORT).show();
           }


        });

    }

    private void recuperaUsuario(String id){
        DatabaseReference usuariosRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(id);
        usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){ // Usuário
                    setResult(RESULT_OK);
                }else {// Empresa
                    startActivity(new Intent(getBaseContext(), MainActivityEmpresa.class));
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void configCliques(){
        binding.include.ibVoltar.setOnClickListener(view -> finish());

        binding.btnRecuraSenha.setOnClickListener(view ->
                startActivity(new Intent(this, RecuperaContaActivity.class)));

        binding.btnCadastro.setOnClickListener(view ->{
            Intent intent = new Intent(new Intent(this, CadastroActivity.class));
            resultLauncher.launch(intent);
        });

    }

    // Ocultar teclado do dispositivo
    private void ocultaTeclado(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(binding.edtEmail.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }


}