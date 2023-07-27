package com.example.MerceariaPalestraitalia.fragment.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.MerceariaPalestraitalia.activiy.usuario.UsuarioEnderecoActivity;
import com.example.MerceariaPalestraitalia.autenticacao.CadastroActivity;
import com.example.MerceariaPalestraitalia.autenticacao.LoginActivity;
import com.example.MerceariaPalestraitalia.databinding.FragmentUsuarioPerfilBinding;
import com.example.MerceariaPalestraitalia.helper.FirebaseHelper;

public class UsuarioPerfilFragment extends Fragment {

    private FragmentUsuarioPerfilBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUsuarioPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        configClicks();
    }

    private void configClicks(){
        binding.btnEntrar.setOnClickListener(view ->  startActivity(LoginActivity.class));
        binding.btnMeusDados.setOnClickListener(view ->  startActivity(LoginActivity.class));
        binding.btnCadastrar.setOnClickListener(view ->  {
            startActivity(new Intent(requireContext(), CadastroActivity.class));
        });
        binding.btnEnderecos.setOnClickListener(view -> {
            startActivity(new Intent(requireContext(), UsuarioEnderecoActivity.class));
        });
    }

    private void startActivity(Class<?> clazz){
        if (FirebaseHelper.getAutenticado()){
            startActivity(new Intent(requireContext(),clazz));
        }else {
            startActivity(new Intent(requireContext(),LoginActivity.class));
        }
    }
}