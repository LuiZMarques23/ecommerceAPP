package com.example.MerceariaPalestraitalia.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.MerceariaPalestraitalia.model.ItemPedido;
import com.example.MerceariaPalestraitalia.model.Produto;

public class ItemDAO {

    private final SQLiteDatabase write;
    private final SQLiteDatabase read;


    public ItemDAO(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        write = dbHelper.getWritableDatabase();
        read = dbHelper.getReadableDatabase();
    }

    public long salvar(Produto produto){
        long idRetorno = 0;

        ContentValues values = new ContentValues();
        values.put("id_firebase", produto.getId());
        values.put("nome", produto.getTitulo());
        values.put("valor", produto.getValorAtual());

        for (int i = 0; i < produto.getUrlImagens().size(); i++) {
            if (produto.getUrlImagens().get(i).getIndex() == 0 ){
                values.put("url_imagem", produto.getUrlImagens().get(i).getCaminhoImagem());

            }
        }

        try {
            idRetorno = write.insert(DBHelper.TB_ITEM, null,values);
        }catch (Exception e){
            Log.i("INFODB:", "Erro ao salvar o item." + e.getMessage());
        }

        return idRetorno;

    }

    public boolean remover(ItemPedido itemPedido){
        String where = "id=?";
        String[] args = {String.valueOf(itemPedido.getId())};
        Log.i("INFODB:", "Sucesso ao remover o itemPedido.");
        try {
            write.delete(DBHelper.TB_ITEM, where, args);
        }catch (Exception e){
            Log.i("INFODB:", "Erro ao remover o itemPedido." + e.getMessage());
            return false;
        }

        return true;

    }

}
