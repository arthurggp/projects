package com.example.gatekeeper.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.gatekeeper.enums.FiltrosPesquisaEnum;
import com.example.gatekeeper.models.ConvidadoModel;

import java.util.ArrayList;
import java.util.List;

public class ConvidadoDaoImpl extends SQLiteOpenHelper implements ConvidadoDao<ConvidadoModel> {

    private static final int DATABASE_VERSION=1;
    private static final String NOME_BANCO = "gatekeeper.db";
    private static final String NOME_TABELA = "convidados";
    private static final String CONVIDADO_DE = "convidado_de";
    private static final String CODIGO = "id";
    private static final String CPF = "cpf";
    private static final String RG = "rg";
    private static final String NOME = "nome";
    private static final String STATUS = "status";
    
//    public ConvidadoDaoImpl(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
//    }

    public ConvidadoDaoImpl(Context context) {
        super(context,NOME_BANCO,null,DATABASE_VERSION);
    }

    // CRIAR TABELA
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE if not EXISTS " + NOME_TABELA +
                "(" +
                CODIGO + " INTEGER PRIMARY KEY, " +
                NOME + " TEXT, " +
                CPF + " INTEGER, " +
                RG + " TEXT, " +
                STATUS + " TEXT, " +
                CONVIDADO_DE + " TEXT " +
                ")";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+NOME_TABELA);
    }

    // ADICIONAR CONVIDADO
    @Override
    public void adicionarConvidado(ConvidadoModel convidado) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues=new ContentValues();
        contentValues.put(CODIGO, convidado.getCodigo());
        contentValues.put(NOME, convidado.getNome());
        contentValues.put(RG, convidado.getRg());
        contentValues.put(STATUS, convidado.getStatus());
        contentValues.put(CPF, convidado.getCpf());
        contentValues.put(CONVIDADO_DE, convidado.getConvidado_de());

        db.insert(NOME_TABELA, null, contentValues);
        db.close();
    }

    // ATUALIZAR CONVIDADO
    @Override
    public Integer atualizarConvidado(ConvidadoModel convidado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = inicializarContentValues(convidado);
        return db.update(NOME_TABELA, contentValues, CODIGO + "=?", new String[]{String.valueOf(convidado.getCodigo())});
    }
    
    // DELETAR CONVIDADO
    @Override
    public void deletarConvidado(String id) {
        SQLiteDatabase connection = this.getWritableDatabase();
        connection.delete(NOME_TABELA, CODIGO + "=?", new String[]{id});
        connection.close();
    }

    // BUSCA LISTA DE TODOS OS CONVIDADOS
    @Override
    public List<ConvidadoModel> buscarTodosConvidados() {
        List<ConvidadoModel> convidadoModelList = new ArrayList<>();
        String query = "SELECT * from " + NOME_TABELA;
        SQLiteDatabase connection = this.getReadableDatabase();
        
        Cursor cursor = connection.rawQuery(query,null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                convidadoModelList.add(new ConvidadoModel(cursor));
            } while (cursor.moveToNext());
        }
        connection.close();

        return convidadoModelList;
    }

    // CONTAGEM DE CONVIDADOS
    @Override
    public Integer buscarNumeroTotalConvidados() {
        String query = "SELECT * from " + NOME_TABELA;
        SQLiteDatabase connection = this.getReadableDatabase();
        Cursor cursor = connection.rawQuery(query, null);
        connection.close();
        return cursor.getCount();
    }

    private static final String COLUNAS_BANCO = "CODIGO, CPF, RG, NOME, STATUS, CONVIDADO_DE";
    
    // BUSCA POR FILTRO
    @Override
    public ConvidadoModel buscarConvidadoFiltro(FiltrosPesquisaEnum colunaFiltro) {
        SQLiteDatabase connection = this.getReadableDatabase();
        
        Cursor cursor = connection.query(NOME_TABELA, new String[]{COLUNAS_BANCO}, 
                colunaFiltro.toString() + " = ?",
                new String[]{colunaFiltro.toString()},
                null,
                null,
                null);

        connection.close();
        
        if (cursor == null || (cursor != null && cursor.getCount() <= 0)) {
            return new ConvidadoModel();
        }
        
        cursor.moveToFirst();
        return new ConvidadoModel(cursor);
    }

    private static ContentValues inicializarContentValues(ConvidadoModel convidado) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOME, convidado.getNome());
        contentValues.put(CPF, convidado.getCpf());
        contentValues.put(RG, convidado.getRg());
        contentValues.put(STATUS, convidado.getStatus());
        contentValues.put(CPF, convidado.getCpf());
        contentValues.put(CONVIDADO_DE, convidado.getConvidado_de());
        return contentValues;
    }
}
