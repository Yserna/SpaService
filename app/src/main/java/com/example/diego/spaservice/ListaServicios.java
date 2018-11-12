package com.example.diego.spaservice;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

public class ListaServicios extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    ArrayList<String> selectedItems = new ArrayList<>();
    private double longitud = 0;
    private double latitud = 0;
    private Button consultar;
    private FirebaseAuth firebaseAuth;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_servicios);
        consultar = (Button) findViewById(R.id.consultar);
        consultar.setOnClickListener(this);
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
                        //nombre.setText(user.getDisplayName());
                    }
                    if(user.getPhotoUrl()!= null){
                        //Glide.with(ListaServicios.this).load(user.getPhotoUrl()).into(foto);
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

    public String selectedItems(){
        String items = "";
        for(String item:selectedItems){
            items+=item+",";
        }
        return items;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_lista_servicios, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }

    public void goLogInScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_CLEAR_TOP |intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        String control = selectedItems();
        if(control.equals("")){
            Toast.makeText(getApplicationContext(), "Debe seleccionar al menos un servicio", Toast.LENGTH_SHORT).show();
        }else{
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

            Intent intent = new Intent(this, Mapa.class);
            intent.putExtra("latitud", latitud);
            intent.putExtra("longitud", longitud);
            intent.putExtra("perfil", "cliente");
            intent.putStringArrayListExtra("servicios", (ArrayList<String>)selectedItems);
            startActivity(intent);

            /*Intent intent1 = new Intent(this, VistaUsuario.class);
            intent.putStringArrayListExtra("servicios", (ArrayList<String>)selectedItems);
            startService(intent1);*/
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
