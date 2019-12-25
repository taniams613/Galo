package com.formabase.galo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;



public class DetalhesJogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhe_jogo);

        Intent intent = getIntent();

        String resultado = intent.getStringExtra("resultado");
        TextView res = findViewById(R.id.resultado);
        res.setText(resultado);

        int num_jogo = intent.getIntExtra("jogo", 0);
        TextView textojogo = findViewById(R.id.numjogo);
        textojogo.setText("Jogo " + num_jogo);

        String texto_data = intent.getStringExtra("data");
        TextView data = findViewById(R.id.data);
        data.setText("Jogo realizado em " + texto_data);

        String texto_imagem = intent.getStringExtra("imagem");
        ImageView img = findViewById(R.id.img_resultado);

        if (texto_imagem.equals("bola")) {
            img.setImageResource(R.drawable.bola);
        }
        else if (texto_imagem.equals("cruz")) {
            img.setImageResource(R.drawable.cruz);
        }
        else {
            img.setImageResource(R.drawable.vazio);
        }


    }
}
