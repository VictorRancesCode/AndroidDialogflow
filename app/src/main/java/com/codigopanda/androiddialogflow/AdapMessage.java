package com.codigopanda.androiddialogflow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by victor on 25-11-17.
 */

public class AdapMessage  extends BaseAdapter{
    List<Message> lista;
    Context context;
    LayoutInflater inflater;

    public AdapMessage(List<Message> lista,Context context){
        this.lista=lista;
        this.context=context;
        inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int i) {
        return lista.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public class Holder{
        TextView bot;
        TextView mymessage;
        ImageView fotobot;
        ImageView mifoto;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = inflater.inflate(R.layout.item_message,null);
        Holder h = new Holder();
        h.bot=(TextView) v.findViewById(R.id.botmessage);
        h.mymessage=(TextView) v.findViewById(R.id.mymessage);
        h.fotobot=(ImageView) v.findViewById(R.id.imagenbot);
        h.mifoto=(ImageView) v.findViewById(R.id.miimagen);

        if(lista.get(i).tipo==0){
            h.bot.setText("Bot: "+lista.get(i).texto);
            h.mymessage.setVisibility(View.GONE);
            h.mifoto.setVisibility(View.GONE);
        }else{
            h.mymessage.setText("Yo: "+lista.get(i).texto);
            h.bot.setVisibility(View.GONE);
            h.fotobot.setVisibility(View.GONE);
        }
        return v;
    }
}
