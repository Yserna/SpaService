package com.example.diego.spaservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diego.spaservice.Ref.Users;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private CardView ingresar;
    private TextView registrar;
    private EditText usuario, contrasena;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private GoogleApiClient googleApiClient;
    private SignInButton signIn;
    private ProgressBar progress;
    private CallbackManager callbackManager;
    private LoginButton fbLogin;
    DatabaseReference db;
    public static final int SIGN_IN_CODE = 777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuario = (EditText) findViewById(R.id.editUser);
        contrasena = (EditText) findViewById(R.id.editPassword);
        ingresar = (CardView) findViewById(R.id.ingresar);
        ingresar.setOnClickListener(this);
        registrar = (TextView) findViewById(R.id.registrar);
        registrar.setOnClickListener(this);
        signIn = (SignInButton) findViewById(R.id.signIn);
        signIn.setOnClickListener(this);
        fbLogin = (LoginButton) findViewById(R.id.facebook);
        fbLogin.setOnClickListener(this);
        fbLogin.setReadPermissions("email", "public_profile", "user_friends");
        progress = (ProgressBar) findViewById(R.id.progressBar2);
        signIn.setColorScheme(signIn.COLOR_DARK);

        progressDialog = new ProgressDialog(this);

        db = FirebaseDatabase.getInstance().getReference("users");

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this).
                enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        callbackManager = CallbackManager.Factory.create();
        fbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAcessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "No se pudo llevar a cabo la autenticación", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "Error inesperado "+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void autenticacion(String perfilUsuario, String nombre, String direccion, String telefono,
                              String educacion, String experiencia, String perfil, String servicios, String path){
        String email = usuario.getText().toString().trim();
        String password = contrasena.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Debe ingresar su correo electronico", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Debe ingresar una contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Autenticando...");
        progressDialog.show();
        final String finalPerfilUsuario = perfilUsuario;
        final String finalNombre = nombre;
        final String finalDireccion = direccion;
        final String finalTelefono = telefono;
        final String finalEducacion = educacion;
        final String finalExperiencia = experiencia;
        final String finalPerfil = perfil;
        final String finalServicios = servicios;
        final String finalPath = path;
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Bienvenido "+ finalNombre, Toast.LENGTH_SHORT).show();
                            if(finalPerfilUsuario.equals("usuario")){
                                Intent intent = new Intent(MainActivity.this, PerfilUsuario.class);
                                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_CLEAR_TOP |intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("nombre", finalNombre);
                                intent.putExtra("direccion", finalDireccion);
                                intent.putExtra("telefono", finalTelefono);
                                intent.putExtra("educacion", finalEducacion);
                                intent.putExtra("experiencia", finalExperiencia);
                                intent.putExtra("perfil", finalPerfil);
                                intent.putExtra("servicios", finalServicios);
                                intent.putExtra("path", finalPath);

                                startActivity(intent);
                            }
                            else if(finalPerfilUsuario.equals("cliente")){
                                Intent intent = new Intent(MainActivity.this, ListaServicios.class);
                                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_CLEAR_TOP |intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("nombre", finalNombre );
                                intent.putExtra("path", finalPath);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Error inesperado", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(MainActivity.this, "No se pudo realizar la autenticacion"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_CODE){
           GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
           handleSignInResult(result);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAcessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "No se pudo llevar a cabo la autenticación "+task.getException(), Toast.LENGTH_SHORT).show();
                }
                if(task.isSuccessful()){
                    Intent intent = new Intent(MainActivity.this, ListaServicios.class);
                    intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_CLEAR_TOP |intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            firebaseAuthWithGoogle(result.getSignInAccount());
        }else{
            Toast.makeText(this, "No se pudo llevar a cabo la autenticación", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount signInAccount) {
        progress.setVisibility(View.VISIBLE);
        signIn.setVisibility(View.GONE);
        ingresar.setVisibility(View.GONE);
        registrar.setVisibility(View.GONE);
        usuario.setVisibility(View.GONE);
        contrasena.setVisibility(View.GONE);
        fbLogin.setVisibility(View.GONE);

        AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progress.setVisibility(View.GONE);
                signIn.setVisibility(View.VISIBLE);
                ingresar.setVisibility(View.VISIBLE);
                registrar.setVisibility(View.VISIBLE);
                usuario.setVisibility(View.VISIBLE);
                contrasena.setVisibility(View.VISIBLE);
                fbLogin.setVisibility(View.VISIBLE);

                if(!task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "No se pudo llevar a cabo la autenticación", Toast.LENGTH_SHORT).show();
                } if(task.isSuccessful()){
                    Intent intent = new Intent(MainActivity.this, ListaServicios.class);
                    intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_CLEAR_TOP |intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    public void consultarPerfil(){
        final String[] perfilUsuario = {""};
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datasnapshot: dataSnapshot.getChildren()){
                    Users user = datasnapshot.getValue(Users.class);
                    String email = user.getEmail().toString();
                    if(email !=null && email.equals(usuario.getText().toString())){
                        perfilUsuario[0] = user.getPerfil();
                        String nombre = user.getNombre();
                        String direccion = user.getDireccion();
                        String telefono = user.getTelefono();
                        String educacion = user.getEducacion();
                        String experiencia = user.getExperiencia();
                        String perfil = user.getPerfilPro();
                        String servicios = user.getServicios();
                        String path = user.getFoto();
                        autenticacion(perfilUsuario[0], nombre, direccion, telefono, educacion, experiencia, perfil, servicios, path);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId()){
            case R.id.ingresar:
            {
                consultarPerfil();
                break;
            }
            case R.id.registrar:
            {
                Intent intent = new Intent(this, SeleccionPerfil.class);
                startActivity(intent);
                break;
            }
            case R.id.signIn:
            {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_CODE);
                break;
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
