package com.example.diego.spaservice;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class Mapa extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double latitud, longitud;
    private ArrayList<String> servicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras !=null){
            latitud = extras.getDouble("latitud");
            longitud = extras.getDouble("longitud");
            servicios = intent.getStringArrayListExtra("servicios");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng ubicacion = new LatLng(latitud, longitud);
        mMap.addMarker(new MarkerOptions().position(ubicacion).title("Mi ubicación"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion,15));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().anchor(0.0f, 1.0f).position(latLng));
            }
        });

        Intent intent = new Intent(this, VistaUsuario.class);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(getApplicationContext(), VistaUsuario.class);
                intent.putStringArrayListExtra("servicios", (ArrayList<String>)servicios);
                startActivity(intent);
                return false;
            }
        });
    }
}
