package com.example.diego.spaservice;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.diego.spaservice.Config.Config;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class VistaUsuario extends AppCompatActivity implements View.OnClickListener{

    private TextView nombre, departamento, telefono, educacio, experienca, perfi, estado;
    private CircleImageView foto;
    private Switch domicilio;
    private CardView pedir, pagar;
    private ArrayList<String> servicios;
    Uri imagen;
    String amount="";
    public static final int PAYPAL_REQUEST_CODE = 7171;
    private static final PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_usuario);

        foto = (CircleImageView) findViewById(R.id.foto);
        nombre = (TextView) findViewById(R.id.user);
        departamento = (TextView) findViewById(R.id.lugar);
        educacio = (TextView) findViewById(R.id.educacion);
        experienca = (TextView) findViewById(R.id.experiencia);
        perfi = (TextView) findViewById(R.id.perfil);
        telefono = (TextView) findViewById(R.id.telefono);
        estado = (TextView) findViewById(R.id.estado);
        domicilio = (Switch) findViewById(R.id.disponible);
        pedir = (CardView) findViewById(R.id.pedir);
        pedir.setOnClickListener(this);
        pagar = (CardView) findViewById(R.id.pago);
        pagar.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras !=null){
            servicios = intent.getStringArrayListExtra("servicios");
        }

        nombre.setText("Marcela Carvajal Soto");
        departamento.setText("Medellín, Antioquia");
        telefono.setText("3125687456");
        educacio.setText("Graduada en Escuela de Belleza Mariela");
        experienca.setText("3 años en Viva Spa, 4 meses en Nirvana Spa");
        perfi.setText("Responsabilidad, higiene y excelente servicio");
        imagen = Uri.parse("https://firebasestorage.googleapis.com/v0/b/spaservice-216916.appspot.com/o/fotos%2F23311?alt=media&token=33e7d7c1-cc3f-44e6-a123-e6efaaec5cb3");
        /*Glide.with(VistaUsuario.this)
                .load(imagen)
                .into(foto);*/

        Intent intent1 = new Intent(this, PayPalService.class);
        intent1.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent1);
    }

    public int totalPagar(){
        int total=0;
        for(String servicio:servicios){
            if(servicio.equals("Alisado")){
                total += total+200000;
            }
            if(servicio.equals("Cejas")){
                total += total+70000;
            }
            if(servicio.equals("Chocolaterapia")){
                total += total+120000;
            }
            if(servicio.equals("Corporales")){
                total += total+90000;
            }
            if(servicio.equals("Depilación con cera")){
                total += total+45000;
            }
            if(servicio.equals("Digitopuntura")){
                total += total+80000;
            }
            if(servicio.equals("Faciales")){
                total += total+15000;
            }
            if(servicio.equals("Manicure")){
                total += total+15000;
            }
            if(servicio.equals("Maquillaje")){
                total += total+60000;
            }
            if(servicio.equals("Masajes descontracturantes")){
                total += total+120000;
            }
            if(servicio.equals("Masajes de relajación")){
                total += total+120000;
            }
            if(servicio.equals("Pedicures")){
                total += total+20000;
            }
            if(servicio.equals("Pestañas")){
                total += total+50000;
            }
            if(servicio.equals("Reflexología")){
                total += total+170000;
            }
            if(servicio.equals("Ozonoterapia")){
                total += total+250000;
            }
            if(servicio.equals("Peinado")){
                total += total+50000;
            }
            if(servicio.equals("Shoch revitalizante")){
                total += total+200000;
            }
            if(servicio.equals("Tratamiento reductivo")){
                total += total+300000;
            }
            if(servicio.equals("Tratamiento tonificante")){
                total += total+150000;
            }
        }
        return total;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.pedir){
            Toast.makeText(getApplicationContext(), "Solicitud realizada...", Toast.LENGTH_SHORT).show();
            estado.setText("En espera de confirmación");
        }
        if(v.getId() == R.id.pago){
            procesarPago();
        }
    }

    private void procesarPago() {
        Integer pago = totalPagar();
        amount = pago.toString();
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)),
                "USD", "Valor a pagar: ", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PAYPAL_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation != null){
                    try{
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        startActivity(new Intent(this, Calificacion.class)
                                .putExtra("paymentDetails", paymentDetails)
                                .putExtra("paymentAmount", amount));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if(resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Operación cancelada", Toast.LENGTH_SHORT).show();
            }
        }else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID){
            Toast.makeText(getApplicationContext(), "Operación inválida", Toast.LENGTH_SHORT).show();
        }
    }
}
