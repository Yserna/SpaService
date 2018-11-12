package com.example.diego.spaservice;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilUsuario extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private TextView nombre, departamento, telefono, educacio, experienca, perfi;
    private double longitud = 0;
    private double latitud = 0;
    private CircleImageView foto;
    private ListView servicio;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private Switch disponible;
    private boolean avalaible = false;
    Uri imagen;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    String name, lugar, phone, educacion, experiencia, perfil, servicios, image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        foto = (CircleImageView) findViewById(R.id.foto);
        nombre = (TextView) findViewById(R.id.user);
        departamento = (TextView) findViewById(R.id.lugar);
        educacio = (TextView) findViewById(R.id.educacion);
        experienca = (TextView) findViewById(R.id.experiencia);
        perfi = (TextView) findViewById(R.id.perfil);
        telefono = (TextView) findViewById(R.id.telefono);
        servicio = (ListView) findViewById(R.id.servicios);
        disponible = (Switch) findViewById(R.id.disponible);
        disponible.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras !=null){
            name = extras.getString("nombre");
            lugar = extras.getString("direccion");
            phone = extras.getString("telefono");
            educacion = extras.getString("educacion");
            experiencia = extras.getString("experiencia");
            perfil = extras.getString("perfil");
            servicios = extras.getString("servicios");
            image = extras.getString("path");
        }

        nombre.setText(name);
        departamento.setText(lugar);
        telefono.setText(phone);
        educacio.setText(educacion);
        experienca.setText(experiencia);
        perfi.setText(perfil);
        if(image!=null)imagen = Uri.parse(image);
        if(imagen != null){
            Glide.with(PerfilUsuario.this)
                    .load(imagen)
                    .into(foto);
        }

        /*String[] info = {"Formación académica", "Experiencia laboral", "Perfil profesional"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PerfilUsuario.this, android.R.layout.simple_list_item_1, info);
        informacion.setAdapter(adapter);*/

        ArrayList<String> serv = new ArrayList<String>();
        if(servicios!=null) {
            String[] arrSplit = servicios.split(",");
            for (int i = 0; i < arrSplit.length; i++) {
                String item = arrSplit[i];
                serv.add(item);
            }
        }

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PerfilUsuario.this, android.R.layout.simple_list_item_1, serv);
        servicio.setAdapter(adapter1);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    if(user.getDisplayName()!= null){
                        nombre.setText(user.getDisplayName());
                    }
                    if(user.getPhotoUrl()!= null){
                        Glide.with(PerfilUsuario.this).load(user.getPhotoUrl()).into(foto);
                    }
                }else{
                    goLogInScreen();
                }
            }
        };

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuthListener != null){
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    public void goLogInScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_CLEAR_TOP |intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_perfil, menu);
        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_salir:
            {
                firebaseAuth.signOut();
                LoginManager.getInstance().logOut();
                goLogInScreen();
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if(status.isSuccess()){
                            goLogInScreen();
                        }else{
                            Toast.makeText(getApplicationContext(), "No se pudo cerrar sesión", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return true;
            }
            case R.id.action_solicitud:
            {
                if(avalaible){
                    Intent intent = new Intent(this, Solicitudes.class);
                    startActivity(intent);
                    return true;
                }else{
                    Toast.makeText(getApplicationContext(), "Verifique si se encuentra disponible", Toast.LENGTH_SHORT).show();
                }
            }
            case R.id.action_ubicacion:
            {
                LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                LocationListener locationListener = new LocationListener() {

                    @Override
                    public void onLocationChanged(Location location) {
                        latitud = location.getLatitude();
                        longitud = location.getLongitude();
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                int permissionCheck = ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

                if(avalaible){
                    Intent intent = new Intent(this, Mapa.class);
                    intent.putExtra("latitud", latitud);
                    intent.putExtra("longitud", longitud);
                    intent.putExtra("perfil", "usuario");
                    startActivity(intent);
                    return true;
                }else{
                    Toast.makeText(getApplicationContext(), "Verifique si se encuentra disponible", Toast.LENGTH_SHORT).show();
                }


            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.disponible){
            if(disponible.isChecked()){
                avalaible = true;
            }
        }
    }
}
