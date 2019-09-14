package br.com.rcarvs.bibliotecappufsj;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


public class AdapterListView extends BaseAdapter{
    List<ItemListView> itens;

    public AdapterListView(List<ItemListView> itens){
        this.itens = itens;
    }

    @Override
    public int getCount() {
        return itens.size();
    }

    @Override
    public Object getItem(int position) {
        return itens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.item_list, parent,false);
        }

        TextView data = (TextView)convertView.findViewById(R.id.dataVencimento);
        TextView titulo = (TextView)convertView.findViewById(R.id.tituloItem);

        final ItemListView item = itens.get(position);


        data.setText("Vence em: "+item.getDataVencimento());
        titulo.setText(item.getTitulo());
        return convertView;
    }
}
