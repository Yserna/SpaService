package com.example.diego.spaservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SeleccionPerfil extends AppCompatActivity implements View.OnClickListener{

    private ImageView usuarioR, clienteR;
    String nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_perfil);

        clienteR = (ImageView) findViewById(R.id.cliente);
        clienteR.setOnClickListener(this);
        usuarioR = (ImageView) findViewById(R.id.usuario);
        usuarioR.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras !=null){
            nombre = extras.getString("nombre");
        }
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId()){
            case R.id.usuario:
            {
                Intent intent = new Intent(this, RegistrarUsuario.class);
                startActivity(intent);
                break;
            }
            case R.id.cliente:
            {
                Intent intent = new Intent(this, RegistrarCliente.class);
                startActivity(intent);
                break;
            }
        }
    }
}

