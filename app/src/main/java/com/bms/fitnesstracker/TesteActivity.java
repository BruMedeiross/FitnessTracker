package com.bms.fitnesstracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Toast;

public class TesteActivity extends AppCompatActivity {

    private Button button;
    private Chronometer chronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste);

        //ligação entre a parte grafica e parte lógica
        button = findViewById(R.id.button);
        Button buttonFinish = findViewById(R.id.buttonFinish);
        chronometer = findViewById(R.id.chronometer);

        button.setOnClickListener(view -> startClick());

        button.setOnLongClickListener(view -> {
            pauseClick();
            return true;
        });

        buttonFinish.setOnClickListener(this::onClick);
    }


    private void pauseClick(){
            button.setText(R.string.start);
            chronometer.stop();
    }

    private void startClick(){
         button.setText(R.string.buttonpause);

            chronometer.start();

            Toast.makeText(TesteActivity.this, R.string.dados, Toast.LENGTH_SHORT).show();
    }


    private void onClick(View view) {
        finish();
        Toast.makeText(TesteActivity.this, R.string.pause, Toast.LENGTH_LONG).show();


    }
}