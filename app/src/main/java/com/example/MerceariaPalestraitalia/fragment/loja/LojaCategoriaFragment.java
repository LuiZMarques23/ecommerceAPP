package com.example.MerceariaPalestraitalia.fragment.loja;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.MerceariaPalestraitalia.R;
import com.example.MerceariaPalestraitalia.adapter.CategoriaAdapter;
import com.example.MerceariaPalestraitalia.databinding.DialogDeleteBinding;
import com.example.MerceariaPalestraitalia.databinding.DialogFormCadegoriaBinding;
import com.example.MerceariaPalestraitalia.databinding.FragmentLojaCategoriaBinding;
import com.example.MerceariaPalestraitalia.helper.FirebaseHelper;
import com.example.MerceariaPalestraitalia.model.Categoria;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LojaCategoriaFragment extends Fragment implements CategoriaAdapter.onClick {

    private CategoriaAdapter categoriaAdapter;
    private final List<Categoria> categoriaList = new ArrayList<>();

    private DialogFormCadegoriaBinding categoriaBinding;

    private String caminhoImagem = null;

    private FragmentLojaCategoriaBinding binding;
    private AlertDialog dialog;

    private Categoria categoria;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLojaCategoriaBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recuperaCategorias();
        configCliks();
        configRv();
    }



    private void configRv(){
        binding.rvCategorias.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCategorias.setHasFixedSize(true);
        categoriaAdapter = new CategoriaAdapter(R.layout.item_categoria_vertical, false,categoriaList,this, requireContext());
        binding.rvCategorias.setAdapter(categoriaAdapter);


        binding.rvCategorias.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {

            }

            @Override
            public void onSwipedRight(int position) {

                showDialogDelete(categoriaList.get(position));
            }
        });
    }

    private void recuperaCategorias(){
        DatabaseReference categoriaRef = FirebaseHelper.getDatabaseReference()
                .child("categoria");
        categoriaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    categoriaList.clear();

                    for (DataSnapshot ds : snapshot.getChildren()){
                        Categoria categoria = ds.getValue(Categoria.class);
                        categoriaList.add(categoria);
                    }

                    binding.textInfo.setText("");
                }else {
                    binding.textInfo.setText("Nenhuma categoria cadastrada.");
                }
                binding.progressBar.setVisibility(View.GONE);
                Collections.reverse(categoriaList);
                categoriaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void configCliks(){
        binding.btnAddCadegoria.setOnClickListener(v -> {

            categoria = null;
            showDialog();
        });

    }

    private void showDialogDelete(Categoria categoria){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                getContext(), R.style.CustomAlertDialog2);

        View view = getLayoutInflater().inflate(R.layout.dialog_form_cadegoria, null);

        DialogDeleteBinding deleteBinding = DialogDeleteBinding
                .inflate(LayoutInflater.from(getContext()));

        deleteBinding.btnFechar.setOnClickListener(v -> {
            dialog.dismiss();
            categoriaAdapter.notifyDataSetChanged();
        });

        deleteBinding.textTitulo.setText("Deseja remover está categoria ?");

        deleteBinding.btnSim.setOnClickListener(v -> {
            categoriaList.remove(categoria);

            if (categoriaList.isEmpty()){
                binding.textInfo.setText("Nenhuma categoria cadastrado.");

            }else {
                binding.textInfo.setText("");
            }

            categoria.Delete();

            categoriaAdapter.notifyDataSetChanged();

            dialog.dismiss();
        });

        builder.setView(deleteBinding.getRoot());

        dialog = builder.create();
        dialog.show();


    }
    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                getContext(), R.style.CustomAlertDialog);

        View view = getLayoutInflater().inflate(R.layout.dialog_form_cadegoria, null);

        categoriaBinding = DialogFormCadegoriaBinding
                .inflate(LayoutInflater.from(getContext()));

        if (categoria != null){
            categoriaBinding.edtCategoria.setText(categoria.getNome());
            Glide.with(requireContext())
                    .load(categoria.getUrlImagem())
                    .centerCrop()
                    .into(categoriaBinding.imagemCategoria);

            categoriaBinding.cbTodos.setChecked(categoria.isTodas());
        }

        categoriaBinding.btnFecha.setOnClickListener(v -> {
            dialog.dismiss();
        });

        categoriaBinding.btnSalvar.setOnClickListener(v -> {

            String nomeCategoria = categoriaBinding.edtCategoria.getText().toString().trim();


            if (!nomeCategoria.isEmpty()){

                if (categoria == null) categoria = new Categoria();
                categoria.setNome(nomeCategoria);
                categoria.setTodas(categoriaBinding.cbTodos.isChecked());

                ocultaTeclado();
                categoriaBinding.progressBar.setVisibility(View.VISIBLE);


                if (caminhoImagem != null){ // Novo cadastro ou edição da imagem

                    salvarImagemFirebase();
                } else if (categoria.getUrlImagem() != null) { // Edição de nome ou checkbox
                    categoria.salvar();

                    dialog.dismiss();
                }else { // Não preencheu a imagem

                    categoriaBinding.progressBar.setVisibility(View.GONE);

                    Toast.makeText(getContext(), "Escolha uma imagem para categoria", Toast.LENGTH_SHORT).show();
                }
            }else {
                categoriaBinding.edtCategoria.setError("Informação obrigatória!");
            }

        });

        categoriaBinding.imagemCategoria.setOnClickListener(v -> verificaPermissaoGaleria());

        builder.setView(categoriaBinding.getRoot());

        dialog = builder.create();
        dialog.show();


    }

    private void salvarImagemFirebase(){
        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagem")
                .child("categoria")
                .child(categoria.getId() + "jpeg");

        UploadTask uploadTask = storageReference.putFile(Uri.parse(caminhoImagem));
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnCompleteListener(task -> {

            String urlImagem = task.getResult().toString();

            categoria.setUrlImagem(urlImagem);
            categoria.salvar();

            categoria = null;
            dialog.dismiss();



        })).addOnFailureListener( e -> {
            dialog.dismiss();
            Toast.makeText(getContext(), "Erro ao fazer upload da imagem.", Toast.LENGTH_SHORT).show();
        });

    }
    private void verificaPermissaoGaleria(){

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abrirGaleria();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getContext(), "Permissão Negada.", Toast.LENGTH_SHORT).show();
            }


        };

        if (Integer.parseInt(Build.VERSION.RELEASE) < 13){
            showDialogPermissaoGaleria(permissionlistener, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
        }else {
            showDialogPermissaoGaleria(permissionlistener, new String[]{Manifest.permission.READ_MEDIA_IMAGES});
        }



        }

        private void showDialogPermissaoGaleria( PermissionListener permissionListener, String[] permissoes){

            TedPermission.create()
                    .setPermissionListener(permissionListener)
                    .setDeniedTitle("Permissões!")
                    .setDeniedMessage("Se você não aceitar a permissão não poderá acessar a Galeria do dispositivo, deseja ativar a permissão agora ?")
                    .setDeniedCloseButtonText("Não")
                    .setGotoSettingButtonText("Sim")
                    .setPermissions(permissoes)
                    .check();




        }



    private void abrirGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resultLauncher.launch(intent);

    }

    private ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {

                    // Recupera Caminho da imagem
                    Uri imagemSelecionada = result.getData().getData();
                    caminhoImagem = imagemSelecionada.toString();

                    try {
                        Bitmap bitmap;
                        if (Build.VERSION.SDK_INT < 28){
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imagemSelecionada);
                        }else {
                            ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(),imagemSelecionada);
                            bitmap = ImageDecoder.decodeBitmap(source);
                        }
                        categoriaBinding.imagemCategoria.setImageBitmap(bitmap);

                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }

            }
    );

    // Oculta o teclado do dispossitivo
    private void ocultaTeclado(){
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(categoriaBinding.edtCategoria.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onClickListener(Categoria categoria) {
        this.categoria = categoria;

        showDialog();
    }
}