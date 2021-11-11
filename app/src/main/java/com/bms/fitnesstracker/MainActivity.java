package com.bms.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // private View btnImc;
    private RecyclerView rvMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Trabalhando com RecyclerView
        rvMain = findViewById(R.id.rv_Main);
        
        //Inclusão dos itens na MainItem
        List<MainItem> mainItems = new ArrayList<>();
        mainItems.add(new MainItem(1, R.drawable.ic_fitness, R.string.label_imc, Color.BLACK));
        mainItems.add(new MainItem(2, R.drawable.ic_baseline_directions_run_24, R.string.label_tmb, Color.BLACK));
        mainItems.add(new MainItem(3, R.drawable.ic_baseline_format_list_numbered_24, R.string.Teste, Color.BLACK));
        mainItems.add(new MainItem(4, R.drawable.ic_timer, R.string.crono, Color.BLACK));

        // SETANDO NO RECYCLER VIEW - tipo GRID COM 2 COLUNAS
        // exemplo Linear (vertical, com uma SPAN"divisórias" horizontal)
        rvMain.setLayoutManager(new GridLayoutManager(this, 2)); //layout

        //ADAPTER: personalizar a apresentação de dados em lista com qqr caracteristica
        // neste caso o modelo de dados é o mainItens
        MainAdapter adapter = new MainAdapter(mainItems); //adapter

        //listener ouvira qual botao foi clicado para ex acao abaixo
        adapter.setListener(id -> {
        switch (id){
            case 1:
                startActivity(new Intent(MainActivity.this, ImcActivity.class));
                break;
            case 2:
                startActivity(new Intent(MainActivity.this, TMBActivity.class));
                break;
            case 3:
                startActivity(new Intent(MainActivity.this, ListaExActivity.class));
                break;
            case 4:
                startActivity(new Intent(MainActivity.this, TesteActivity.class));
                break;

        }
    });

        rvMain.setAdapter(adapter); //seta na rv

    }

    //adptador
    public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

        private List<MainItem> mainItems;
        private OnItemClickListener listener;

        // Construtor da lista MAIN
        public MainAdapter(List<MainItem> mainItems) {
            this.mainItems = mainItems;
        }

        public void setListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        // Método que espera uma célula especifica
        // neste caso MainViewHolder() + o layout dinâmico (será inflado)
        public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MainViewHolder(getLayoutInflater().inflate(R.layout.main_item, parent, false));
        }

        //ao rolar tela chama o metodo on bind view holder,
        // que aprensenta o tema corrente, troca o conteúdo
        @Override
        public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
            MainItem mainItemCurrent = mainItems.get(position);
            holder.bind(mainItemCurrent);
        }

        // Método que cuida da quantidade de itens na celula, dentro do nosso recycleView
        //o tamanho da lista é o mesmo do mainItems
        @Override
        public int getItemCount() {
            return mainItems.size();
        }

        // View da célula que esta dentro do recycleView
        //classe que vai ter a referencia dos componentes criados - neste caso: layout, textview e img
        public class MainViewHolder extends RecyclerView.ViewHolder {

            public MainViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            //bind espera o modelo MainItem e setara as propriedades na view
            public void bind(MainItem item) { //chamada no on bind view holder: refere-se a posicao atual da celula
                TextView textName = itemView.findViewById(R.id.item_txt_name);
                ImageView imgIcon = itemView.findViewById(R.id.item_img_icon);
                LinearLayout btn_imc = (LinearLayout) itemView.findViewById(R.id.btn_imc);

                //item recebera um id correspondente ao botao clicado
                btn_imc.setOnClickListener(view -> listener.onClick(item.getId()));

                //setara os dados na view - um desenhavel, um texto e cor de fundo
                textName.setText(item.getTextStringId());
                imgIcon.setImageResource(item.getDrawableId());
                btn_imc.setBackgroundColor(item.getColor());

            }
        }
    }
}


//       btnImc = findViewById(R.id.btn_imc);

//       btnImc.setOnClickListener(new View.OnClickListener() {
//  @Override
//     public void onClick(View view) {
//         Intent intent = new Intent(MainActivity.this, ImcActivity.class);
//         startActivity(intent);            }
//  });


//btnImc = findViewById(R.id.btn_imc);

//SEM LAMBDA
/*
    btnImc.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        startActivity(intent);
    }
*/
        /*
        // COM LAMBDA

        */

