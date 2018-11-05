package com.example.diego.spaservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diego.spaservice.Ref.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RegistrarUsuario2 extends AppCompatActivity implements View.OnClickListener{

    ArrayList<String> selectedItems = new ArrayList<>();
    String nombre, direccion, telefono, email, contrasena, items="";
    private EditText educacion, perfilP, experiencia;
    private CardView registrar;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    String foto;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario2);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras !=null){
            nombre = extras.getString("nombre");
            direccion = extras.getString("direccion");
            telefono = extras.getString("telefono");
            email = extras.getString("email");
            contrasena = extras.getString("contrasena");
            foto = extras.getString("foto");
        }

        perfilP = (EditText) findViewById(R.id.perfil);
        educacion = (EditText) findViewById(R.id.educacion);
        experiencia = (EditText) findViewById(R.id.experiencia);
        registrar = (CardView) findViewById(R.id.registrarse);
        registrar.setOnClickListener(this);
        ListView check = (ListView) findViewById(R.id.listview);
        check.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        String[] items = { "Alisado", "Cejas", "Chocolaterapia", "Corporales", "Depilación con cera", "Digitopuntura", "Faciales",
                "Manicure", "Maquillaje", "Masajes descontracturantes", "Masajes de relajación", "Pedicure", "Pestañas", "Reflexología",
                "Ozonoterapia", "Peinado", "Shoch revitalizante", "Tratamiento reductivo", "Tratamiento tonificante"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row, R.id.txt, items);
        check.setAdapter(adapter);
        check.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = ((TextView)view).getText().toString();
                if(selectedItems.contains(selectedItem)){
                    selectedItems.remove(selectedItem);
                }else selectedItems.add(selectedItem);
            }
        });

        db = FirebaseDatabase.getInstance().getReference("users");
    }

    public void selectedItems(){
        for(String item:selectedItems){
            items+=item + ",";
        }
    }

    public void registrarUsuario(){
        progressDialog.setMessage("Realizando registro en línea...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, contrasena)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            registrarBD();
                            Toast.makeText(RegistrarUsuario2.this, "Se ha registrado el usuario", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistrarUsuario2.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else{
                            if(task.getException() instanceof FirebaseAuthInvalidUserException){
                                Toast.makeText(RegistrarUsuario2.this, "El usuario ya está registrado", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(RegistrarUsuario2.this, "No se pudo registrar el usuario "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        progressDialog.dismiss();
                    }
                });
    }

    public void registrarBD(){
        Users user = new Users(nombre, direccion, telefono, email, contrasena, "usuario", educacion.getText().toString(),
                experiencia.getText().toString(), perfilP.getText().toString(), items, foto);
        String clave= db.push().getKey();
        db.child(clave).setValue(user);
    }

    @Override
    public void onClick(View v) {
        selectedItems();
        registrarUsuario();
    }
}
