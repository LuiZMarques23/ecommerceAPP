package com.example.MerceariaPalestraitalia.model;

import com.example.MerceariaPalestraitalia.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class Loja {

    private String id;
    private String nome;
    private String urlLogo;
    private String CNPJ;
    private double pedidoMinimo;
    private double freteGratis;
    private String email;
    private String senha;
    private String publickey;
    private String accessToken;
    private int parcelas;

    public Loja() {
    }


    public void salvar(){
        DatabaseReference lojaRef = FirebaseHelper.getDatabaseReference()
                .child("loja");
        lojaRef.setValue(this);
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

    public String getUrlLogo() {
        return urlLogo;
    }

    public void setUrlLogo(String urlLogo) {
        this.urlLogo = urlLogo;
    }

    public String getCNPJ() {
        return CNPJ;
    }

    public void setCNPJ(String CNPJ) {
        this.CNPJ = CNPJ;
    }

    public double getPedidoMinimo() {
        return pedidoMinimo;
    }

    public void setPedidoMinimo(double pedidoMinimo) {
        this.pedidoMinimo = pedidoMinimo;
    }

    public double getFreteGratis() {
        return freteGratis;
    }

    public void setFreteGratis(double freteGratis) {
        this.freteGratis = freteGratis;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPublickey() {
        return publickey;
    }

    public void setPublickey(String publickey) {
        this.publickey = publickey;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getParcelas() {
        return parcelas;
    }

    public void setParcelas(int parcelas) {
        this.parcelas = parcelas;
    }
}
