package com.example.MerceariaPalestraitalia.model;

import com.example.MerceariaPalestraitalia.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

public class Categoria {

    private String id;
    private String nome;
    private String urlImagem;
    private boolean todas = false;

    public Categoria() {
        DatabaseReference categoriaRef = FirebaseHelper.getDatabaseReference();
        this.setId(categoriaRef.push().getKey());
    }

    public void salvar() {
        DatabaseReference categoriaRef = FirebaseHelper.getDatabaseReference()
                .child("categoria")
                .child(this.getId());
        categoriaRef.setValue(this);


    }

    public void Delete() {
        DatabaseReference categoriaRef = FirebaseHelper.getDatabaseReference()
                .child("categoria")
                .child(this.getId());
        categoriaRef.removeValue();

        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagem")
                .child("categoria")
                .child(this.getId()+ "jpeg");
        storageReference.delete();


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public boolean isTodas() {
        return todas;
    }

    public void setTodas(boolean todas) {
        this.todas = todas;
    }
}
