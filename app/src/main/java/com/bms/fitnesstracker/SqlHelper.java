package com.bms.fitnesstracker;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//sql é a classe da criação e edição de tabelas
// Uma classe somente poderá ser utilizada após ser instanciada.
//on create e on upgrade associados ao sqlite open helper

public class SqlHelper extends SQLiteOpenHelper {

    //variveis de execução
    private static final String DB_NAME = "fitness_tracker_db";
    private static final int DB_VERSION = 2;
    private static SqlHelper INSTANCE;

    //se nao existir uma instancia na variavel ira gerar/armazenar
    // caso existir irá retornar a própria instacia viva(sommente um objto sql helper vivo durnte toda a execuçao)
    //Uma instância de uma classe é um novo objeto criado dessa classe, com o operador new.
    // Instanciar uma classe é criar um novo objeto do mesmo tipo dessa classe.
    static SqlHelper getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = new SqlHelper(context);
        return INSTANCE;
    }

    //construtor - um único sqlHelper
    private SqlHelper(@Nullable Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    //onCREATE - DISPARADO quando não tiver uma base de dados
    //executa a query - Uma query é um pedido de uma informação ou de um dado.
    // Esse pedido também pode ser entendido como uma consulta, uma solicitação ou, ainda, uma requisição
    //primary key, cada vez que houver uma insercao de dados o id sera gerado automaticmnt
    //Será disparado sempre que você não tiver base de dados
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE calc (id INTEGER primary key, type_calc TEXT, res DECIMAL, created_date DATETIME)"
        );
    }

    @Override
    //Será executado quando você tiver uma base de dados nova um UPDATE dos registros que já existem
    //onUpgrade - usada ao subir atualizacoes, passa condições novas(ex uma nova coluna) para o base de dados antiga
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("Teste ", "on Upgrade disparado");
    }

    //criando a intrução - lista de registros
    List<Register> getRegisterBy(String type){

        //lista vazia > cria uma nova lista
        List<Register> registers =  new ArrayList<>();

        //instrução da query, readable
        SQLiteDatabase db = getReadableDatabase();

        //Cursor varre as linhas do BD, para identificar todos os indices adicionados a tabela
        Cursor cursor = db.rawQuery("SELECT * FROM calc WHERE type_calc = ?", new String[]{ type });

        try {

            if (cursor.moveToFirst()) {
                do {
                    //novo registro no db
                    Register register = new Register();

                    register.id = cursor.getInt(cursor.getColumnIndex("id"));
                    register.type = cursor.getString(cursor.getColumnIndex("type_calc"));
                    register.response = cursor.getDouble(cursor.getColumnIndex("res"));
                    register.createdDate = cursor.getString(cursor.getColumnIndex("created_date"));

                    registers.add(register);
                } while (cursor.moveToNext());
            }

        }   catch(Exception e){
            Log.e("SQLite", e.getMessage(), e);

        }   finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        return registers;
    }

    //add item é o quer será amarzenado ao ser digitado > imc == result ou tmb == tmb
    long addItem(String type, double response) {
        SQLiteDatabase db = getWritableDatabase();

        long calcId = 0;
        try {
            db.beginTransaction(); //iniciar transação

            ContentValues values = new ContentValues();
            values.put("type_calc", type);
            values.put("res", response);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("pt", "BR"));
            String now = sdf.format(new Date());


            values.put("created_date", now);
            calcId = db.insertOrThrow("calc", null, values);
            db.setTransactionSuccessful(); //sucesso transação
        }   catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        }   finally {
            if (db.isOpen())
                db.endTransaction();//fechar transação
        }
        return calcId;
    }

    //atualiza o registro
    long updateItem(String type, double response, int id) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        long calcId = 0;

        try {
            ContentValues values = new ContentValues();
            values.put("type_calc", type);
            values.put("res", response);

            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("pt", "BR"))
                    .format(Calendar.getInstance().getTime());
            values.put("created_date", now);

            // Passamos o whereClause para verificar o registro pelo ID e TYPE_CALC
            calcId = db.update("calc", values, "id = ? and type_calc = ?", new String[]{String.valueOf(id), type});

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        } finally {
            db.endTransaction();
        }
        return calcId;
    }


    //deleta o registro
    long removeItem(String type, int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        long calcId = 0;

        try {
            // Passamos o whereClause para verificar o registro pelo ID e TYPE_CALC
            calcId = db.delete("calc", "id = ? and type_calc = ?", new String[]{String.valueOf(id), type});

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        } finally {
            db.endTransaction();
        }
        return calcId;
    }



}

