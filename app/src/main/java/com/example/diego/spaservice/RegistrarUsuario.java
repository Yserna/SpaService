package com.example.diego.spaservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrarUsuario extends AppCompatActivity implements View.OnClickListener{

    private CardView continuar;
    private EditText nombre, email, departamento, contrasena, contrasenaConf, telefono;
    private CircleImageView foto;
    private ImageButton cargarFoto;
    private StorageReference storage;
    Uri uri, descargarFoto;
    String perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras !=null){
            perfil = extras.getString("perfil");
        }

        nombre = (EditText) findViewById(R.id.nombre);
        email = (EditText) findViewById(R.id.email);
        departamento = (EditText) findViewById(R.id.lugar1);
        telefono = (EditText) findViewById(R.id.telefono);
        contrasena = (EditText) findViewById(R.id.password);
        contrasenaConf = (EditText) findViewById(R.id.password2);
        foto = (CircleImageView) findViewById(R.id.img);
        cargarFoto = (ImageButton) findViewById(R.id.cargarImagen2);
        cargarFoto.setOnClickListener(this);
        continuar = (CardView) findViewById(R.id.continuar);
        continuar.setOnClickListener(this);

        storage = FirebaseStorage.getInstance().getReference();
    }


    private void cargarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==1 && resultCode==RESULT_OK){
            uri = data.getData();
            StorageReference filePath = storage.child("fotos").child(uri.getLastPathSegment());
            foto.setImageURI(uri);
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    descargarFoto = taskSnapshot.getDownloadUrl();
                }
            });
        }
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId()) {
            case R.id.continuar: {
                 if(TextUtils.isEmpty(nombre.getText().toString())){
                    Toast.makeText(this, "Debe ingresar su nombre", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(departamento.getText().toString())){
                    Toast.makeText(this, "Debe ingresar su direccion", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(email.getText().toString())){
                    Toast.makeText(this, "Debe ingresar un email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(contrasena.getText().toString())){
                    Toast.makeText(this, "Debe ingresar una contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(contrasenaConf.getText().toString())){
                    Toast.makeText(this, "Debe confirmar la contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(contrasena.getText().toString().equals(contrasenaConf.getText().toString())){
                    Intent intent = new Intent(this, RegistrarUsuario2.class);
                    intent.putExtra("nombre", nombre.getText().toString().trim());
                    intent.putExtra("direccion", departamento.getText().toString().trim());
                    intent.putExtra("telefono", telefono.getText().toString().trim());
                    intent.putExtra("email", email.getText().toString().trim());
                    intent.putExtra("contrasena", contrasena.getText().toString().trim());
                    if(uri!=null)intent.putExtra("foto", descargarFoto.toString());
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.cargarImagen2: {
                cargarImagen();
                break;
            }
        }

    }
}
