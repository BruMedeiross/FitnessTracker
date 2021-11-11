package com.bms.fitnesstracker;

// a rv servirá para criar multiplos elementos com base num modelo de dados
// mainItem: modelo com as propriedades dinamicas no layout que são editaveis
class MainItem {

    //O QUE TERA NO MEU BOTÃO > ID, desenhavel, texto e cor de fundo (parametros)
    private int id;
    private int drawableId;
    private int textStringId;
    private int color;


    //contrutor dos objetos - assim que forem criados já setam as propriedades do botão
    // CODE > GENERATE > CONTRUCTOR - selecione os parametros
    public MainItem(int id, int drawableId, int textStringId, int color) {
        this.id = id;
        this.drawableId = drawableId;
        this.textStringId = textStringId;
        this.color = color;
    }


    //CRIAR o GETTER PARA OS ITENS > ****SETER SUBSTITUIDOS PELO CONTRUTOR
    //CODE > GENERATE > SETTER AND GETTER
    public int getId() {
        return id;
    }

    //public void setId(int id) {        this.id = id;    }

    public int getDrawableId() {
        return drawableId;
    }

    //public void setDrawableId(int drawableId) {        this.drawableId = drawableId;    }

    public int getTextStringId() {
        return textStringId;
    }

    //public void setTextStringId(int textStringId) {        this.textStringId = textStringId;    }

    public int getColor() {
        return color;
    }

    //public void setColor(int color) {        this.color = color;    }
}
