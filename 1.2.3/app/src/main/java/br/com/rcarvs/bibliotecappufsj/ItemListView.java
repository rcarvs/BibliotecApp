package br.com.rcarvs.bibliotecappufsj;

import java.util.ArrayList;
import java.util.List;

public class ItemListView {

    private String codigo;
    private String titulo;
    private String dataVencimento;
    private String acv;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(String dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getAcv() {
        return acv;
    }

    public void setAcv(String acv) {
        this.acv = acv;
    }

    public ItemListView(String codigo,String titulo, String dataVencimento,String acv) {
        this.setCodigo(codigo);
        this.setTitulo(titulo);
        this.setDataVencimento(dataVencimento);
        this.setAcv(acv);
    }




}
