package com.example.MerceariaPalestraitalia.fragment.loja;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.MerceariaPalestraitalia.activiy.loja.LojaConfigActivity;
import com.example.MerceariaPalestraitalia.activiy.loja.LojaPagamentosActivity;
import com.example.MerceariaPalestraitalia.activiy.usuario.MainActivityUsuario;
import com.example.MerceariaPalestraitalia.databinding.FragmentLojaConfigBinding;
import com.example.MerceariaPalestraitalia.helper.FirebaseHelper;

public class LojaConfigFragment extends Fragment {

    private FragmentLojaConfigBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLojaConfigBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configClicks();
    }

    @Override
    public void onStart() {
        super.onStart();

        configMenu();
    }

    private void configClicks(){
        binding.btnConfigLoja.setOnClickListener(v -> {
            startActivity(LojaConfigActivity.class);
        });

        binding.btnPagamentos.setOnClickListener(v -> {
            startActivity(LojaPagamentosActivity.class);
        });

        binding.btnDeslogar.setOnClickListener(v -> {
            FirebaseHelper.getAuth().signOut();
            requireActivity().finish();
            
            startActivity(MainActivityUsuario.class);
        });
    }
    private void startActivity(Class<?> clazz){
        startActivity(new Intent(requireContext(),clazz));
    }

    private void configMenu(){
        if (FirebaseHelper.getAutenticado()){
            
            binding.BoaLogado.setVisibility(View.VISIBLE);

        }else {

            binding.BoaLogado.setVisibility(View.GONE);



        }
    }

}