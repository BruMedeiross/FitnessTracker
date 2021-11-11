package com.bms.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListCalcActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_calc);

        //extras -  se houver dados vindos da outra activity ele pega
        Bundle extras = getIntent().getExtras();

        RecyclerView recyclerView = findViewById(R.id.recycler_view_list);

        if (extras != null) {
            String type = extras.getString("type");//buscou dado passado em TMB activity

            new Thread(() -> {
                List<Register> registers = SqlHelper.getInstance(this).getRegisterBy(type);

                runOnUiThread(() -> {
                    //Log.d("Teste", registers.toString());
                    ListCalcAdapter adapter = new ListCalcAdapter(registers);  //adapter
                    recyclerView.setLayoutManager(new LinearLayoutManager(this)); //layout
                    recyclerView.setAdapter(adapter); //seta na rv com o adapter
            });
        }).start();
    }
}

public class ListCalcAdapter extends RecyclerView.Adapter<ListCalcViewHolder> implements OnAdapterItemClickListener {

    private List<Register> datas;

    // Construção da lista MAIN
    public ListCalcAdapter(List<Register> datas) {
        this.datas = datas;
    }

    @NonNull
    @Override
    // Método que espera uma célula especifica, neste caso MainViewHolder() + o layout dinâmico será inflado (layout modelo do item)
    public ListCalcViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListCalcViewHolder(getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false));
    }

    //ao rolar tela chama o metodo on bind view holder,
    // que aprensenta o tema corrente, troca o conteúdo
    @Override
    public void onBindViewHolder(@NonNull ListCalcViewHolder holder, int position) {
        Register data = datas.get(position);
        holder.bind(data, this);
    }

    // Método que cuida da quantidade de itens na celula, dentro do nosso recycleView
    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public void onLongClick(int position, String type, int id) {
        // evento de exclusão (PERGUNTAR ANTES PARA O USUARIO)
        AlertDialog alertDialog = new AlertDialog.Builder(ListCalcActivity.this)
                .setMessage(getString(R.string.delete_message))
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {

                    new Thread(() -> {
                        SqlHelper sqlHelper = SqlHelper.getInstance(ListCalcActivity.this);
                        long calcId = sqlHelper.removeItem(type, id);

                        runOnUiThread(() -> {
                            if (calcId > 0) {
                                Toast.makeText(ListCalcActivity.this, R.string.calc_removed, Toast.LENGTH_LONG).show();
                                datas.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                    }).start();


                })
                .create();

        alertDialog.show();
    }

    @Override
    public void onClick(int id, String type) {
        // verificar qual tipo de dado deve ser EDITADO na tela seguinte
        switch (type) {
            case "imc":
                Intent intent = new Intent(ListCalcActivity.this, ImcActivity.class);
                intent.putExtra("updateId", id);
                startActivity(intent);
                break;
            case "tmb":
                Intent i = new Intent(ListCalcActivity.this, TMBActivity.class);
                i.putExtra("updateId", id);
                startActivity(i);
                break;
        }
    }
}



    // View da célula que esta dentro do recycleView
    public class ListCalcViewHolder extends RecyclerView.ViewHolder {

        public ListCalcViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        //chamada no on bind view holder: refere-se a posicao atual da celula
        //A PROPRIEDADES DE VIEW COM AS ESPECIFICACOES ABAIXO
        void bind(Register data, final OnAdapterItemClickListener onItemClickListener) {

            String formatted = "";
            try {
                //armazena os dados de data
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("pt", "BR"));
                Date dateSaved = sdf.parse(data.createdDate); //parse: Você simplesmente define os pares de valores-chave que desejar e nosso back-end irá armazená-los.

                //como desejo exibir o formato de data
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", new Locale("pt", "BR"));
                formatted = dateFormat.format(dateSaved);

            }   catch (ParseException e){

            }

            ((TextView) itemView).setText(
                getString(R.string.list_response, data.response, formatted)
            );

            // listener para ouvir evento de click (ABRIR EDIÇAO)
            itemView.setOnClickListener(view -> {
                onItemClickListener.onClick(data.id, data.type);
            });

            // listener para ouvir evento de long-click (segurar touch - EXCLUIR)
            itemView.setOnLongClickListener(view -> {
                onItemClickListener.onLongClick(getAdapterPosition(), data.type, data.id);
                return false;
            });

        }
    }
}
