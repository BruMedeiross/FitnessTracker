package com.bms.fitnesstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class ListaExActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_ex);

        ListView listview = (ListView) findViewById(R.id.listview);

        Toast.makeText(ListaExActivity.this, R.string.exercise, Toast.LENGTH_SHORT).show();

        String[] dados = new String[] { "Abdominal - 25x ", "Flexões - 25x", "Prancha - 25x", "Pular corda - 25x",
                "Polichinelos - 25x", "Agachamento - 25x",
                "Panturilha - 25x", "Braços - 25x", "Esteira - 5 minutos" };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dados);
        listview.setAdapter(adapter);
    }
}

