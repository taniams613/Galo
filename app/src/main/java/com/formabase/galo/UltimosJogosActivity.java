package com.formabase.galo;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UltimosJogosActivity extends ListActivity {

    private Cursor c;
    private Basedados bd;
    private CursorAdapter ca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ultimos_jogos);
    }

    public void onStart () {
        super.onStart();


        bd = new Basedados (this);
        c = bd.consultaLeitura("select * from jogos");
        UltimosJogosActivity ultimoscontexto = this;

        setListAdapter(new CursorAdapter(ultimoscontexto, c, 0) {

            @Override
            public View newView (Context context, Cursor cursor, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View v = inflater.inflate(R.layout.lista_jogos_item, parent, false);
                bindView(v, context, cursor);
                return v;

            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {

                TextView textores = view.findViewById(R.id.textoresultado);
                String textores_bd = cursor.getString(cursor.getColumnIndex("tresultado"));
                textores.setText(textores_bd);

                String textoimagem = cursor.getString(cursor.getColumnIndex("nome_imagem"));
                ImageView imagem = view.findViewById(R.id.imagemresultado);
                if (textoimagem.equals ("bola"))
                    imagem.setImageResource(R.drawable.bola);
                else if (textoimagem.equals ("cruz"))
                    imagem.setImageResource(R.drawable.cruz);
                else
                    imagem.setImageResource(R.drawable.vazio);

                TextView textotempo = view.findViewById(R.id.textodata);
                long tempomilis = cursor.getLong(cursor.getColumnIndex("tempo"));

                Date tempodate = new Date (tempomilis);
                SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                textotempo.setText(formato.format(tempodate));

            }


        });

        ca = (CursorAdapter)getListAdapter();
        ListView lista = getListView();

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                AlertDialog.Builder construtordialogo = new AlertDialog.Builder(UltimosJogosActivity.this);
                construtordialogo.setMessage("Deseja apagar este jogo?");
                construtordialogo.setCancelable(true);
                construtordialogo.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {


                c.moveToPosition(position);
                int idjogo = c.getInt(c.getColumnIndex("_id"));
                bd.consultaEscrita("delete from jogos where _id = " + idjogo);
                c = bd.consultaLeitura("select * from jogos");
                ca.changeCursor(c);

                    }
                });

                construtordialogo.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) { }
                });

                AlertDialog caixadialogo = construtordialogo.create();
                caixadialogo.show();

            }
        });

        registerForContextMenu(lista);
    }

    public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        c.moveToPosition(info.position);

        long tempomilis = c.getLong(c.getColumnIndex("tempo"));

        Date tempodate = new Date (tempomilis);
        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy hh:mm");

        menu.setHeaderTitle("Opções sobre o jogo de " + formato.format(tempodate));

        menu.add(Menu.NONE,0, Menu.NONE,"Detalhes");
        menu.add(Menu.NONE,1, Menu.NONE,"Eliminar");
    }

    public boolean onContextItemSelected(MenuItem item)  {
        switch (item.getItemId()){
            case 0:
                Intent intent = new Intent();
                intent.setClass(UltimosJogosActivity.this, DetalhesJogoActivity.class);

                String textores_bd = c.getString(c.getColumnIndex("tresultado"));
                String textoimagem = c.getString(c.getColumnIndex("nome_imagem"));
                int num_jogo = c.getInt(c.getColumnIndex("_id"));

                long tempomilis = c.getLong(c.getColumnIndex("tempo"));
                Date tempodate = new Date(tempomilis);
                SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                intent.putExtra("resultado", textores_bd);
                intent.putExtra("imagem", textoimagem);
                intent.putExtra("jogo", num_jogo);
                intent.putExtra("data", formato.format(tempodate));

                startActivity(intent);
                break;
            case 1:
                int idjogo = c.getInt(c.getColumnIndex("_id"));
                bd.consultaEscrita("delete from jogos where _id = " + idjogo);
                c = bd.consultaLeitura("Select * from jogos");
                ca.changeCursor(c);
                break;
        }
        return false;
    }



    @Override
    protected void onDestroy () {
        c.close();
        super.onDestroy();
    }
}


