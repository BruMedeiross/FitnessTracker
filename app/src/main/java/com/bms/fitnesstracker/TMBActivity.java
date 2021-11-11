package com.bms.fitnesstracker;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class TMBActivity extends AppCompatActivity {

    //aqui definimos as variaveis que iremos usar de acordo com os itens que compoem o xml
    //no caso peso, altra, idade e os spinners
    //tipo de visualização (private) -> tipo(Text, Spinner) -> nome (atribuir)
    private EditText editHeight_tmb;
    private EditText editWeight_tmb;
    private EditText editAge_tmb;
    private Spinner spinner_ls;
    private Spinner spinner_sexo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t_m_b);
        //aqui atribuimos a variavel ao id do xml visivel ao usuário
        editHeight_tmb = findViewById(R.id.edit_tmb_height);
        editWeight_tmb = findViewById(R.id.edit_tmb_weight);
        editAge_tmb = findViewById(R.id.edit_tmb_age);
        spinner_ls = findViewById(R.id.tmb_lifestyle);
        spinner_sexo = findViewById(R.id.tmb_sexo);

        //aqui vinculamos a variavel ao botao no xml
        Button btnSend = findViewById(R.id.btn_tmb_send);
        //aqui programamos o evento de click ao botão do xml
        //responsável por realizar o calculo
        btnSend.setOnClickListener(view -> {
            //campo de validação:
            // if: caso os campos não estejam preenchidos envia esta msg
            if (!validate()) {
                Toast.makeText(TMBActivity.this, R.string.fields_message, Toast.LENGTH_LONG).show();
                return;
            }
            //caso seja valido os dados aqui embaixo vai executar
            //transformará o texto digitado pelo user em uma string
            String sHeight = editHeight_tmb.getText().toString();
            String sWeight = editWeight_tmb.getText().toString();
            String sAge = editAge_tmb.getText().toString();


            //PARSEINT: pega o dado acima- string sAge por exemplo
            // e a converte em inteiro para fazer o calculo
            int height = Integer.parseInt(sHeight);
            int weight = Integer.parseInt(sWeight);
            int age = Integer.parseInt(sAge);

            //aqui as variaveis que retornaram os calculos
            //result = retorna o valor calculado pela  calculateTMB
            //tmb = retornarpa o valor no tmbResponse com base no result
            double result = calculateTMB(height, weight, age);
            double tmb = tmbResponse(result);
            //Log.d("teste", "Sua TMB é: " + tmb);
            //log é apenas uma msg de apresentação do resultado no banco de dados, não na tela para o user

            //aqui exibirá uma caixa de diálogo com as iformacoes do tmb: resultado - ok - salvar
            AlertDialog dialog = new AlertDialog.Builder(TMBActivity.this)
                    .setMessage(getString(R.string.tmb_response, tmb))
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    })
                    .setNegativeButton(R.string.save, ((dialog1, which) -> {
                        SqlHelper sqlHelper = SqlHelper.getInstance(TMBActivity.this);

                        //instrução sql
                        //tread secundaria para não interferir na interface grafica
                        new Thread(() -> {

                            int updateId = 0;

                            // verifica se tem ID vindo da tela anterior quando é UPDATE
                            if (getIntent().getExtras() != null)
                                updateId = getIntent().getExtras().getInt("updateId", 0);

                            long calcId;
                            // verifica se é update ou create
                            if (updateId > 0) {
                                calcId = sqlHelper.updateItem("tmb", tmb, updateId);
                            } else {
                                calcId = sqlHelper.addItem("tmb", tmb);
                            }

                            //ui tread executa na tread principal, informando a msg abaixo
                            runOnUiThread(() -> {
                                if (calcId > 0){
                                    Toast.makeText(TMBActivity.this, R.string.saved, Toast.LENGTH_LONG).show();
                                    //abrindo segunda atividade
                                    // sai do tmb para outra tela de destino list calc apresentado os valores calculados
                                    openListCalcActivity();
                                }
                            });
                        }).start();
                    }))
                    .create();
            //tost é mostrada (registro salvo com sucesso)
            dialog.show();

            //nesta section o comando indica que depois que o user digitar os dados (input)
            //ao abrir a caixa de resultado o teclado some da tela para melhor visualizacao da tela
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editWeight_tmb.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editHeight_tmb.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editAge_tmb.getWindowToken(), 0);
        });
    }

    // ***ABAIXO TODAS AS FUNÇOES UTILIZADAS PARA QUE AS AÇÕES ACIMA FUNCIONEM***
    // VALIDADE: CAMPOS
    // CALCULO TMB DE ACORDO COM SEXO
    // CALCULO DO RESULTADO DO TMB DE ACORDO COM O ESTILO DE VIDA QUE A PESSOA LEVA

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

    private void openListCalcActivity(){
        Intent intent = new Intent(TMBActivity.this, ListCalcActivity.class);
        intent.putExtra("type", "tmb");
        startActivity(intent);
    }


    //validade - CONDICÕES PARA VALIDAR O CALCULO
    // POR EX: SE COMECAR POR ZERO, SE ESTIVER VAZIO, SE CONTER PONTOS - MSG ERRO
    private boolean validate() {
        return (!editHeight_tmb.getText().toString().startsWith("0")
                && !editHeight_tmb.getText().toString().isEmpty()
                && !editWeight_tmb.getText().toString().startsWith("0")
                && !editWeight_tmb.getText().toString().isEmpty()
                && !editWeight_tmb.getText().toString().contains(".")
                && !editHeight_tmb.getText().toString().contains(".")
                && !editAge_tmb.getText().toString().contains(".")
                && !editAge_tmb.getText().toString().startsWith("0")
                && !editAge_tmb.getText().toString().isEmpty()
        );
    }

    //homens: TMB = 66 + (13,8 x peso em kg.) + (5 x altura em cm) – (6,8 x idade em anos).
    // mulheres: TMB = 655 + (9,6 x peso em kg.) + (1,8 x altura em cm) – (4,7 x idade em anos).
    private double calculateTMB(int height, int weight, int age) {
        int id = spinner_sexo.getSelectedItemPosition();
        switch (id) {
            case 0:
                return 66 + (13.80 * weight) + (5 * height) - (6.8 * age);
            case 1:
                return 655 + (9.60 * weight) + (1.8 * height) - (4.7 * age);
            default:
                return 0;
            //erro: missing "a fuck" return statement
            //quer dizer que precisa o defaul (caso de erro)
        }
    }


    //informacao do que a funcao retorna - no caso um arquivo de recurso
    private double tmbResponse(double tmb) {
        int index = spinner_ls.getSelectedItemPosition();
        switch (index) {
            case 0:
                return  tmb * 1.2;
            case 1:
                return tmb * 1.375;
            case 2:
                return tmb * 1.55;
            case 3:
                return tmb * 1.725;
            case 4:
                return tmb * 1.9;
            default:
                return 0;
        }
    }
}
