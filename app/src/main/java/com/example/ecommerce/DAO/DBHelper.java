package com.example.ecommerce.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static int VERSION = 1;
    static String DB_NAME = "DB_MERCEARIADOZE";
    static String TB_ITEM = "TB_ITEM";
    static String TB_ITEM_PEDIDO = "TB_ITEM_PEDIDO";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String tbItem = "CREATE TABLE IF NOT EXISTS " + TB_ITEM
                + "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_firebase TEXT NOT NULL," +
                "nome TEXT NOT NULL," +
                "valor DOUBLE NOT NULL, " +
                "url_imagem TEXT NOT NULL); ";

        String tbItemPedido = "CREATE TABLE IF NOT EXISTS " + TB_ITEM_PEDIDO
                + "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_produto TEXT NOT NULL," +
                "valor DOUBLE NOT NULL, " +
                "quantidade INTEGER NOT NULL); ";

        try {
            sqLiteDatabase.execSQL(tbItem);
            sqLiteDatabase.execSQL(tbItemPedido);


            Log.i("INFODB:", "Sucesso ao criar as tabelas.");
        }catch (Exception e){
            Log.i("INFODB:", "Erro ao criar as tabelas." + e.getMessage());
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
