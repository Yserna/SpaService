package com.example.diego.spaservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

public class Calificacion extends AppCompatActivity implements View.OnClickListener {

    private TextView txtId, txtStatus, txtAmount;
    private RatingBar ratingBar;
    private CardView calificar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificacion);

        txtId = (TextView) findViewById(R.id.txtId);
        txtAmount = (TextView) findViewById(R.id.txtAmount);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setNumStars(5);
        calificar = (CardView) findViewById(R.id.calificar);
        calificar.setOnClickListener(this);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(getApplicationContext(), "Número de estrellas: "+(int)rating, Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = getIntent();
        try{
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("paymentDetails"));
            showDetails(jsonObject.getJSONObject("response"), intent.getStringExtra("paymentAmount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDetails(JSONObject response, String paymentAmount) {
        try {
            txtId.setText(response.getString("id"));
            txtAmount.setText("$"+paymentAmount);
            txtStatus.setText(response.getString("state"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_calificar, menu);
        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_regresar:{
                Intent intent = new Intent(this, ListaServicios.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.calificar){
            Toast.makeText(getApplicationContext(), "Número de estrellas: "+ratingBar.getRating(), Toast.LENGTH_SHORT).show();
        }
    }
}
