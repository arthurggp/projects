package com.example.gatekeeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.gatekeeper.models.ConvidadoModel;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;
    private static final String NOME_BANCO = "gatekeeper.db";
    private static final String NOME_TABELA = "convidados";
    private static final String CONVIDADO_DE = "convidado_de";
    private static final String CODIGO = "id";
    private static final String CPF = "cpf";
    private static final String RG = "rg";
    private static final String NOME = "nome";
    private static final String STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context,NOME_BANCO,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //CRIA TABELA
        String query = "CREATE TABLE if not EXISTS " +NOME_TABELA+
                "("+
                CODIGO+ " INTEGER PRIMARY KEY, "+
                NOME+" TEXT, "+
                CPF+" INTEGER, "+
                RG+ " TEXT, "+
                STATUS+" TEXT, "+
                CONVIDADO_DE+" TEXT "+
                ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+NOME_TABELA);
    }

    //INSERE CONVIDADO
    public void AddConvidado(ConvidadoModel convidado) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues=new ContentValues();
        contentValues.put(CODIGO, convidado.getCodigo());
        contentValues.put(CPF, convidado.getCpf());
        contentValues.put(RG, convidado.getRg());
        contentValues.put(NOME, convidado.getNome());
        contentValues.put(STATUS, convidado.getStatus());
        contentValues.put(CONVIDADO_DE, convidado.getConvidado_de());

        db.insert(NOME_TABELA, null,contentValues);
        db.close();
    }

    //BUSCA CONVIDADO BY ID
    public ConvidadoModel getConvidado(int id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.query(
                NOME_TABELA,
                new String[]{CODIGO,CPF,RG,NOME,STATUS, CONVIDADO_DE},
                CODIGO+" = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
                                                         //CODIGO               ,NOME                  ,CPF                   ,RG                   ,STATUS                 ,CONVIDADO DE
        ConvidadoModel convidadoModel = new ConvidadoModel(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3), cursor.getString(4),cursor.getString(5));
        db.close();

        return convidadoModel;
    }

    //BUSCA LISTA DE CONVIDADOS
    public List<ConvidadoModel> getAllConvidados(){
        List<ConvidadoModel> convidadoModelList = new ArrayList<>();
        String query = "SELECT * from "+NOME_TABELA;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do{
                                                                  //CODIGO               ,NOME                  ,CPF                   ,RG                   ,STATUS                 ,CONVIDADO DE
                ConvidadoModel convidadoModel = new ConvidadoModel(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3), cursor.getString(4),cursor.getString(5));
                convidadoModelList.add(convidadoModel);
            }while (cursor.moveToNext());
        }
        db.close();

        return convidadoModelList;
    }

    //ATUALIZA CONVIDADO
    public int updateConvidado(ConvidadoModel convidadoModel){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(NOME,convidadoModel.getNome());
        contentValues.put(CPF,convidadoModel.getCpf());
        contentValues.put(RG,convidadoModel.getRg());
        contentValues.put(STATUS,convidadoModel.getStatus());
        return db.update(NOME_TABELA,contentValues,CODIGO+"=?",new String[]{String.valueOf(convidadoModel.getCodigo())});

    }

    //DELETA CONVIDADO
    public void deleteConvidado(String id){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(NOME_TABELA,CODIGO+"=?",new String[]{id});
        db.close();
    }

    //CONTAGEM DE CONVIDADOS
    public int getTotalCount(){
        String query="SELECT * from "+NOME_TABELA;
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(query,null);
        return cursor.getCount();
    }

    //BUSCA POR CPF
    public ConvidadoModel getConvidadoByCpf(String cpf){
        SQLiteDatabase db=this.getReadableDatabase();
        ConvidadoModel convidadoModel = new ConvidadoModel();

        Cursor cursor = db.query(
                NOME_TABELA,
                new String[]{CODIGO,CPF,RG,NOME,STATUS, CONVIDADO_DE},
                CPF+" = ?",
                new String[]{String.valueOf(cpf)},
                null,
                null,
                null);
        if(cursor!=null){
            cursor.moveToFirst();
        }

        if(cursor.getCount() > 0){
            convidadoModel = new ConvidadoModel(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3), cursor.getString(4),cursor.getString(5));
        }

        db.close();

        return convidadoModel;
    }

    //BUSCA POR RG
    public ConvidadoModel getConvidadoByRg(String rg){
        SQLiteDatabase db=this.getReadableDatabase();
        ConvidadoModel convidadoModel = new ConvidadoModel();
        Cursor cursor = db.query(
                NOME_TABELA,
                new String[]{CODIGO,CPF,RG,NOME,STATUS, CONVIDADO_DE},
                RG+" = ?",
                new String[]{String.valueOf(rg)},
                null,
                null,
                null);
        if(cursor!=null){
            cursor.moveToFirst();
        }

        if(cursor.getCount() > 0){
            convidadoModel = new ConvidadoModel(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3), cursor.getString(4),cursor.getString(5));
        }

        db.close();

        return convidadoModel;
    }

    //BUSCA POR NOME
    public ConvidadoModel getConvidadoByNome(String nome){
        SQLiteDatabase db=this.getReadableDatabase();
        ConvidadoModel convidadoModel = new ConvidadoModel();

        Cursor cursor = db.query(
                NOME_TABELA,
                new String[]{CODIGO,CPF,RG,NOME,STATUS, CONVIDADO_DE},
                NOME+" = ?",
                new String[]{String.valueOf(nome)},
                null,
                null,
                null);
        if(cursor!=null){
            cursor.moveToFirst();
        }

        if(cursor.getCount() > 0){
            convidadoModel = new ConvidadoModel(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3), cursor.getString(4),cursor.getString(5));
        }
        db.close();

        return convidadoModel;
    }
}
