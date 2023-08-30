package com.example.MerceariaPalestraitalia.model;

public enum StatusPedido {
    PENDENTE,APROVADO,CAMINHO,MOTOBOY_CHAMANDO,ENTREGUE,CANCELADO;

    public static String getStatus(StatusPedido status){
        String statusPedido;
        switch (status){
            case PENDENTE:
                statusPedido = "pendente";
                break;
            case APROVADO:
                statusPedido = "aprovado";
                break;
            case CAMINHO:
                statusPedido = "caminho";
                break;
            case MOTOBOY_CHAMANDO:
                statusPedido = "motoboy chamando";
                break;
            case ENTREGUE:
                statusPedido = "entregue";
                break;
            default:
                statusPedido = "cancelado";
                break;
        }
        return statusPedido;

    }
}
