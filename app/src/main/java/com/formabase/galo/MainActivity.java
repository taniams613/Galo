package com.formabase.galo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private char[][] quadro;
    Basedados bd;
    private ImageView[][] quadrado;

    private boolean jogoActivo = false;
    private char simboloCorrente, simboloInicial;

    private String Jogador1 = "", Jogador2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView titulo = findViewById(R.id.starten);
        titulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inicializarTabuleiro();
            }
        });

        inicializarTabuleiro();
        bd = new Basedados(this);

    }

    protected void inicializarTabuleiro() {
        quadro = new char[3][3];
        for (int linha = 0; linha < quadro.length; linha++) {
            for (int coluna = 0; coluna < quadro[linha].length; coluna++) {
                quadro[linha][coluna] = ' ';
            }
        }

        quadrado = new ImageView[3][3];

        quadrado[0][0] = findViewById(R.id.leer1);
        quadrado[0][1] = findViewById(R.id.leer2);
        quadrado[0][2] = findViewById(R.id.leer3);

        quadrado[1][0] = findViewById(R.id.leer4);
        quadrado[1][1] = findViewById(R.id.leer5);
        quadrado[1][2] = findViewById(R.id.leer6);

        quadrado[2][0] = findViewById(R.id.leer7);
        quadrado[2][1] = findViewById(R.id.leer8);
        quadrado[2][2] = findViewById(R.id.leer9);


        for (int linha = 0; linha < quadro.length; linha++) {
            for (int coluna = 0; coluna < quadro[linha].length; coluna++) {
                quadrado[linha][coluna].setImageResource(R.drawable.vazio);
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                final int linha = i, coluna = k;
                quadrado[i][k].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fazerJogada(linha, coluna);
                    }
                });
            }
        }


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String lista_simbolos = sharedPref.getString("lista_simbolos", "Bola");
        Jogador1 = sharedPref.getString("Nome jogador 1", "Ana");
        Jogador2 = sharedPref.getString("Nome jogador 2", "Bruno");

        simboloInicial = lista_simbolos.equals("Bola") ? 'O' : 'X';

        simboloCorrente = simboloInicial;

        jogoActivo = true;
    }

    protected void fazerJogada(final int linha, final int coluna) {
        if (jogoActivo && quadro[linha][coluna] == ' ') {
            quadro[linha][coluna] = simboloCorrente;

            quadrado[linha][coluna].setImageResource(simboloCorrente == 'O' ? R.drawable.bola : R.drawable.cruz);

            jogoAcabado();
        }
    }

    private boolean vitoria(int contar) {
        if (contar == 3) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.vitoria) + " " + (simboloCorrente == simboloInicial ? Jogador1 : Jogador2), Toast.LENGTH_LONG).show();
            bd.consultaEscrita("insert into jogos (tresultado, nome_imagem, tempo) values ('Vitoria de', '" + (simboloCorrente == 'O' ? "bola" : "cruz") + "', " + (new Date()).getTime() + ")");
            jogoActivo = false;
            return true;
        }
        return false;
    }

    private void empate() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.empate), Toast.LENGTH_LONG).show();
        bd.consultaEscrita("insert into jogos (tresultado, nome_imagem, tempo) values ('Empate', 'vazio', " + (new Date()).getTime() + ")");
        jogoActivo = false;
    }

    protected void jogoAcabado() {
        int contar;
        for (int linha = 0; linha < quadro.length; linha++) {
            contar = 0;
            for (int coluna = 0; coluna < quadro[linha].length; coluna++) {
                if (quadro[linha][coluna] == simboloCorrente) {
                    contar++;
                }
            }
            if (vitoria(contar)) {
                return;
            }
        }

        for (int coluna = 0; coluna < quadro.length; coluna++) {
            contar = 0;
            for (int linha = 0; linha < quadro[coluna].length; linha++) {
                if (quadro[linha][coluna] == simboloCorrente) {
                    contar++;
                }
            }
            if (vitoria(contar)) {
                return;
            }
        }


        contar = 0;
        for (int diag = 0; diag < quadro.length; diag++) {
            if (quadro[diag][diag] == simboloCorrente) {
                contar++;
            }

            if (vitoria(contar)) {
                return;
            }
        }


        contar = 0;
        int diag1 = 0;
        int diag2 = quadro.length - 1;

        while (diag1 < quadro.length) {
            if (quadro[diag1][diag2] == simboloCorrente) {
                contar++;
            }

            diag1++;
            diag2--;
        }

        if (vitoria(contar)) {
            return;
        }

        // -----

        contar = 0;
        for (int linha = 0; linha < quadro.length; linha++) {
            for (int coluna = 0; coluna < quadro[linha].length; coluna++) {
                if (quadro[linha][coluna] == ' ') {
                    contar++;
                }
            }
        }

        if (contar == 0) {
            empate();
        }

        if (simboloCorrente == 'O')
            simboloCorrente = 'X';
        else
            simboloCorrente = 'O';
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_regras:
                intent = new Intent();
                intent.setClass(MainActivity.this, RegrasActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_settings:
                intent = new Intent();
                intent.setClass(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.menu_ultimos_jogos:
                intent = new Intent();
                intent.setClass(MainActivity.this, UltimosJogosActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
