package com.example.MerceariaPalestraitalia.model;

import com.example.MerceariaPalestraitalia.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pedido implements Serializable {

    private String id;
    private StatusPedido statusPedido;
    private String idCliente;
    private Endereco endereco;
    private List<ItemPedido> itemPedidos = new ArrayList<>();
    private long dataPedido;
    private long dataStadusPedido;
    private double total;
    private String pagamento;
    private double desconto;
    private double acrescimo;

    public Pedido() {
        DatabaseReference pedidoRef = FirebaseHelper.getDatabaseReference();
        this.setId(pedidoRef.push().getKey());
    }

    public void salvar(boolean novoPedido){
        DatabaseReference usuariopedidoRef = FirebaseHelper.getDatabaseReference()
                .child("usuarioPedidos")
                .child(FirebaseHelper.getIdFirebase())
                .child(this.getId());
        usuariopedidoRef.setValue(this);

        DatabaseReference lojapedidoRef = FirebaseHelper.getDatabaseReference()
                .child("lojaPedidos")
                .child(this.getId());
        lojapedidoRef.setValue(this);

        if (novoPedido){
            DatabaseReference dataPedidoUsuarioRef = usuariopedidoRef
                    .child("dataPedido");
            dataPedidoUsuarioRef.setValue(ServerValue.TIMESTAMP);

            DatabaseReference dataPedidoLojaRef = lojapedidoRef
                    .child("dataPedido");
            dataPedidoLojaRef.setValue(ServerValue.TIMESTAMP);
        }else {

        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StatusPedido getStatusPedido() {
        return statusPedido;
    }

    public void setStatusPedido(StatusPedido statusPedido) {
        this.statusPedido = statusPedido;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public List<ItemPedido> getItemPedidos() {
        return itemPedidos;
    }

    public void setItemPedidos(List<ItemPedido> itemPedidos) {
        this.itemPedidos = itemPedidos;
    }

    public long getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(long dataPedido) {
        this.dataPedido = dataPedido;
    }

    public long getDataStadusPedido() {
        return dataStadusPedido;
    }

    public void setDataStadusPedido(long dataStadusPedido) {
        this.dataStadusPedido = dataStadusPedido;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getPagamento() {
        return pagamento;
    }

    public void setPagamento(String pagamento) {
        this.pagamento = pagamento;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    public double getAcrescimo() {
        return acrescimo;
    }

    public void setAcrescimo(double acrescimo) {
        this.acrescimo = acrescimo;
    }
}
