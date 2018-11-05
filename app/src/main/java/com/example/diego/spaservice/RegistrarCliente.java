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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.diego.spaservice.Ref.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrarCliente extends AppCompatActivity implements View.OnClickListener {

    private EditText nombre, email, departamento, contrasena, contrasenaConf, telefono;
    String nombreS, direccionS, telefonoS, emailS, contrasenaS;
    private CircleImageView foto;
    private ImageButton cargarFoto;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private StorageReference storage;
    private CardView registrarse;
    Uri uri, descargarFoto;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_cliente);

        nombre = (EditText) findViewById(R.id.nombre);
        email = (EditText) findViewById(R.id.email);
        departamento = (EditText) findViewById(R.id.lugar2);
        telefono = (EditText) findViewById(R.id.telefono);
        contrasena = (EditText) findViewById(R.id.password);
        contrasenaConf = (EditText) findViewById(R.id.password2);
        foto = (CircleImageView) findViewById(R.id.img);
        cargarFoto = (ImageButton) findViewById(R.id.cargarImagen);
        cargarFoto.setOnClickListener(this);
        registrarse = (CardView) findViewById(R.id.registrarse);
        registrarse.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        db = FirebaseDatabase.getInstance().getReference("users");
        storage = FirebaseStorage.getInstance().getReference();
    }

    public void registrarCliente(){

        nombreS = nombre.getText().toString().trim();
        direccionS = departamento.getText().toString().trim();
        emailS = email.getText().toString().trim();
        telefonoS = telefono.getText().toString().trim();
        contrasenaS = contrasena.getText().toString().trim();
        String contrasenaConfC = contrasenaConf.getText().toString().trim();

        if(TextUtils.isEmpty(nombreS)){
            Toast.makeText(this, "Debe ingresar su nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(direccionS)){
            Toast.makeText(this, "Debe ingresar su direccion", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(emailS)){
            Toast.makeText(this, "Debe ingresar un email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(contrasenaS)){
            Toast.makeText(this, "Debe ingresar una contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(contrasenaConfC)){
            Toast.makeText(this, "Debe confirmar la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        if(contrasenaS.equals(contrasenaConfC)){
            progressDialog.setMessage("Realizando registro en línea...");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(emailS, contrasenaS)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                registrarBD();
                                Toast.makeText(RegistrarCliente.this, "Se ha registrado el usuario", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegistrarCliente.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                if(task.getException() instanceof FirebaseAuthInvalidUserException){
                                    Toast.makeText(RegistrarCliente.this, "El usuario ya está registrado", Toast.LENGTH_SHORT).show();
                                }else{

                                    Toast.makeText(RegistrarCliente.this, "No se pudo registrar el usuario "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            progressDialog.dismiss();
                        }
                    });
        }else{
            Toast.makeText(RegistrarCliente.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        }

    }

    public void registrarBD(){
        Users user = new Users(nombreS, direccionS, telefonoS, emailS, contrasenaS, "cliente",
                null, null, null, null, descargarFoto.toString());
        String clave= db.push().getKey();
        db.child(clave).setValue(user);
    }


    private void cargarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode==RESULT_OK){
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
            case R.id.registrarse: {
                registrarCliente();
                break;
            }
            case R.id.cargarImagen: {
                cargarImagen();
                break;
            }
        }
    }
}

