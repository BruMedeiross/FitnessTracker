package com.bms.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ImcActivity extends AppCompatActivity {

    //na actvity imc - o que preciso > botão altura e peso
    private EditText editHeight;
    private EditText editWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imc);

        //ligando unidade logica a visual
        editHeight = findViewById(R.id.edit_imc_height);
        editWeight = findViewById(R.id.edit_imc_weight);

        Button btnSend = findViewById(R.id.btn_imc_send);

        btnSend.setOnClickListener(view -> {
            if (!validate()) {
                Toast.makeText(ImcActivity.this, R.string.fields_message, Toast.LENGTH_LONG).show();
                return;
            }

            //caso seja valido os dados aqui embaixo vai executar
            //transforma o text digitado em uma string
            // **opção** String sHeight = editHeight.getText().toString();
            String sWeight = editWeight.getText().toString();

            //parseInt - pega a string e converte em inteiro para fazer o calculo
            int height = Integer.parseInt(editHeight.getText().toString());
            int weight = Integer.parseInt(sWeight);

            //resultado vira do calculateIMC (dados)
            double result = calculateIMC(height, weight);
            int imcResponseId = imcResponse(result);
            //Log.d("teste", "resultado: " + result);

            //antes apresentava o tost com o resultado
            // Toast.makeText(ImcActivity.this, imcResponseId,Toast.LENGTH_LONG).show();


            //caixa de diálogo com as iformacoes do imc: resultado, msg e botão salvar
            AlertDialog dialog = new AlertDialog.Builder(ImcActivity.this)
                    .setTitle(getString(R.string.Imc_response, result)) //string dinamica com o valor
                    .setMessage(imcResponseId) //mensagem - ex: severamente obeso
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {//botao ok, nao salva info
                    })
                    .setNegativeButton(R.string.save, ((dialog1, which) -> {//botão para salvar info
                        SqlHelper sqlHelper = SqlHelper.getInstance(ImcActivity.this);

                        //adicionando resultado na instrução sql
                        //tread secundaria para não interferir na interface grafica
                        new Thread(() -> {
                           //antes
                            // long calcId = SqlHelper.getInstance(ImcActivity.this).addItem("imc", result);

                            int updateId = 0;

                            // verifica se tem ID vindo da tela anterior quando é UPDATE
                            if(getIntent().getExtras()!=null)
                                updateId = getIntent().getExtras().getInt("updateId", 0);

                            long calcId;
                            // verifica se é update ou create
                            if (updateId > 0) {
                                calcId = SqlHelper.getInstance(ImcActivity.this).updateItem("imc", result, updateId);
                            } else {
                                calcId = SqlHelper.getInstance(ImcActivity.this).addItem("imc", result);
                            }

                            runOnUiThread(() -> {
                                if (calcId > 0){
                                    Toast.makeText(ImcActivity.this, R.string.saved, Toast.LENGTH_LONG).show();

                                    //abrindo segunda atividade -sai do imc para outra tela de destino list calc
                                    openListCalcActivity();
                                }
                            });
                        }).start();
                    }))
                    .create();

            dialog.show();

            //nesta section depois de digitar os dados e ao abrir o dialog
            // o teclado some da tela para melhor visualizacao
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editWeight.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editHeight.getWindowToken(), 0);
        });
    }

    private void openListCalcActivity(){
        Intent intent = new Intent(ImcActivity.this, ListCalcActivity.class);
        intent.putExtra("type", "imc");
        startActivity(intent);
    }

    /* INFLAR MENU - NÃO CONSEGUI REPLICAR NO LAYOUT
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    /* metodo para escutar os eventos de click no menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() = R.id.menu_list) {
            openListCalcActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    //anotacions - @stringRes devove como resultado precisa ser um arqv de resource
    @StringRes
    //resposta que aparecera na caixa de diálogo
    private int imcResponse(double imc) {
        if (imc < 15)
            return R.string.imc_severely_low_weight;
        else if (imc < 16)
            return R.string.imc_very_low_weight;
        else if (imc < 18.5)
            return R.string.imc_low_weight;
        else if (imc < 25)
            return R.string.normal;
        else if (imc < 30)
            return R.string.imc_high_weight;
        else if (imc < 35)
            return R.string.imc_so_high_weight;
        else if (imc < 40)
            return R.string.imc_severely_high_weight;
        else
            return R.string.imc_extreme_weight;
    }

    //peso/(altura*altura)
    private double calculateIMC(int height, int weight) {
        //peso/(peso div por 2 * altura)
        return weight / ( ((double) height / 100) * ((double) height / 100) );
    }

    //validate - são as CONDICÕES PARA VALIDAR O CALCULO
    // POR EX: SE COMECAR POR ZERO, SE ESTIVER VAZIO, SE CONTER PONTOS - MSG ERRO
    private boolean validate() {
        return (!editHeight.getText().toString().startsWith("0")
                && !editHeight.getText().toString().isEmpty()
                && !editWeight.getText().toString().startsWith("0")
                && !editWeight.getText().toString().isEmpty()
                && !editWeight.getText().toString().startsWith(".")
                && !editHeight.getText().toString().startsWith("."));
    }
}

/*-- JAVA 8 -  LAMBIDAS

forma enxuta de declarar classes anonimas
Quando a clase tem apenas uma função pode ser utilizada lambdas

 */