package com.gm3s.erp.gm3srest.View;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.R;
import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.Model.VentaTotalSerieVO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Admin on 04-06-2015.
 */
public class TotalesSerie2 extends AppCompatActivity {

    private EditText fromDateEtxt;
    private EditText toDateEtxt;
    TableLayout prices;
    public static ImageView loader;
    private static PersistentCookieStore pc;
    private static boolean validacion;
    long f_ini, f_fin;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    private Button consultar;
    String server = "";
    private SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_totales_serie_2);
        loader = (ImageView) findViewById(R.id.loader);
        loader.setVisibility(View.INVISIBLE);
        pc = new PersistentCookieStore(this);
        prices =(TableLayout)findViewById(R.id.main_table);
        prices.setStretchAllColumns(true);
        prices.bringToFront();
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);
        fromDateEtxt = (EditText) findViewById(R.id.tvDate);
        toDateEtxt = (EditText) findViewById(R.id.tvDate2);
        consultar = (Button) findViewById(R.id.consultar);
        final EditText[] a = {fromDateEtxt,toDateEtxt};
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Totales por Serie 2");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setDateTimeField();


        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT"));
        c.setTimeInMillis(c.getTimeInMillis() - 21600000);
        f_fin =  c.getTimeInMillis();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + 1);
        f_ini =  c.getTimeInMillis();



        fromDateEtxt.setText(dateFormatter.format(f_ini));
        toDateEtxt.setText(dateFormatter.format(f_fin));


        fromDateEtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                fromDatePickerDialog.show();

            }
        });

        toDateEtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                toDatePickerDialog.show();

            }
        });






        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (Helper.isConnected3(TotalesSerie2.this)) {
                    if(fromDateEtxt.getText().toString().equals("") || toDateEtxt.getText().toString().equals("")){
                        Toast.makeText(TotalesSerie2.this, "Favor de validar la(s) fecha(s)", Toast.LENGTH_LONG).show();
                    }
                    else{
                        loader.setVisibility(View.VISIBLE);
                        System.out.println(" Las fechas son: " + f_ini + "   " +  f_fin);
                        HttpAsyncTask a = new HttpAsyncTask();
                        a.execute(server + "/medialuna/spring/documento/buscar/total/venta/serie/" + f_ini + "/" + f_fin);
                    }

                }else {
                    Toast.makeText(TotalesSerie2.this, "No estas conectado a la red. Favor de verificar tu configuracion", Toast.LENGTH_LONG).show();
                    Intent localIntent = new Intent(TotalesSerie2.this, LogIn.class);
                    startActivity(localIntent);
                }


            }
        });


    }




    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST2(urls[0]);
            }

        @Override
        protected void onPostExecute(String result) {
          //  Toast.makeText(getActivity(), "VALIDACION: " + validacion, Toast.LENGTH_LONG).show();
            if (result.equals("[]")) {
                loader.setVisibility(View.INVISIBLE);
            } else {
                convertirDatos3(result);
                loader.setVisibility(View.INVISIBLE);


            }
        }
    }


    public static String POST2(String url) {
        String result = "";
        InputStream inputStream = null;
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
            Map<String,Object> b = new HashMap();
            b.put("@class", Map.class);
            JSONObject obj = new JSONObject(b);
            ObjectMapper c = new ObjectMapper();
            String json2 = c.writeValueAsString(b);
            StringEntity params =new StringEntity(json2);
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(params);
            String json = "";
            try {
                HttpResponse httpResponse = httpclient.execute(httppost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null){
                    result = Helper.convertInputStreamToString(inputStream);
                }
                else
                    result = "Did not work!";

                if (result.contains("GM3s Software Index")) {
                    validacion = false;
                    System.out.println("\nNO se  pudo ejecutar la funcion");
                } else {
                    validacion = true;
                    System.out.println("\nFUncion ejecutada");
                    System.out.println("RESULTADO post: " + result);
                }

            } catch (IOException e) {
                System.out.println("ERROR 1.1");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("ERROR 2.1");

            e.printStackTrace();
        }

        return result;
    }



    public void convertirDatos3 (String cadena){
        ObjectMapper mapper = new ObjectMapper();
        try {

            List<Object> arrayData = mapper.readValue(cadena, List.class);
            List <VentaTotalSerieVO> ventaTotal_lista = new ArrayList<>();
            List <BigDecimal> tmpDec = new ArrayList<>();

            for (int z = 0; z <10 ; z++){
                tmpDec.add(z,new BigDecimal(0));
            }

            for (int x=0; x<arrayData.size(); x++) {
                Map mapa1 = (Map) arrayData.get(x);
                List<Object> l2 = new ArrayList<Object>(mapa1.keySet());
                //[@class, id, subtotalTotal, ivaTotal, retencionesTotal, totalTotal, ivaFactura, ivaRemision, ivaNotaCredito, subtotalFactura, subtotalRemision, subtotalNotaCredito, retencionesFactura, retencionesRemision, retencionesNotaCredito, totalFactura, totalRemision, totalNotaCredito, nombre, nombreCorto]
                VentaTotalSerieVO ventaTotal = new VentaTotalSerieVO(mapa1.get("nombre").toString(), mapa1.get("nombreCorto").toString(), new BigDecimal(Double.parseDouble(mapa1.get("totalTotal").toString())).setScale(2,BigDecimal.ROUND_UP), new BigDecimal(Double.parseDouble(mapa1.get("subtotalTotal").toString())).setScale(2,BigDecimal.ROUND_UP),
                        new BigDecimal(Double.parseDouble(mapa1.get("ivaTotal").toString())).setScale(2,BigDecimal.ROUND_UP), new BigDecimal(Double.parseDouble(mapa1.get("subtotalFactura").toString())).setScale(2,BigDecimal.ROUND_UP), new BigDecimal(Double.parseDouble(mapa1.get("ivaFactura").toString())).setScale(2,BigDecimal.ROUND_UP),
                        new BigDecimal(Double.parseDouble(mapa1.get("totalFactura").toString())).setScale(2,BigDecimal.ROUND_UP), new BigDecimal(Double.parseDouble(mapa1.get("totalRemision").toString())).setScale(2,BigDecimal.ROUND_UP) , new BigDecimal(Double.parseDouble(mapa1.get("subtotalNotaCredito").toString())).setScale(2,BigDecimal.ROUND_UP),
                        new BigDecimal(Double.parseDouble(mapa1.get("ivaNotaCredito").toString())).setScale(2,BigDecimal.ROUND_UP), new BigDecimal(Double.parseDouble(mapa1.get("totalNotaCredito").toString())).setScale(2,BigDecimal.ROUND_UP));
                ventaTotal_lista.add(ventaTotal);

                BigDecimal bg1 = tmpDec.get(0).add(ventaTotal.getTotalTotal()); tmpDec.set(0, bg1);
                BigDecimal bg2 = tmpDec.get(1).add(ventaTotal.getSubtotalTotal()); tmpDec.set(1, bg2);
                BigDecimal bg3 = tmpDec.get(2).add(ventaTotal.getIvaTotal()); tmpDec.set(2, bg3);
                BigDecimal bg4 = tmpDec.get(3).add(ventaTotal.getSubtotalFactura()); tmpDec.set(3, bg4);
                BigDecimal bg5 = tmpDec.get(4).add(ventaTotal.getIvaFactura()); tmpDec.set(4, bg5);
                BigDecimal bg6 = tmpDec.get(5).add(ventaTotal.getTotalFactura());  tmpDec.set(5, bg6);
                BigDecimal bg7 = tmpDec.get(6).add(ventaTotal.getTotalRemision()); tmpDec.set(6, bg7);
                BigDecimal bg8 = tmpDec.get(7).add(ventaTotal.getSubtotalNotaCredito()); tmpDec.set(7, bg8);
                BigDecimal bg9 = tmpDec.get(8).add(ventaTotal.getIvaNotaCredito()); tmpDec.set(8, bg9);
                BigDecimal bg10 = tmpDec.get(9).add(ventaTotal.getTotalNotaCredito()); tmpDec.set(9, bg10);

            }

            VentaTotalSerieVO ventaTotalFinal = new VentaTotalSerieVO("  ", " " , tmpDec.get(0),tmpDec.get(1), tmpDec.get(2), tmpDec.get(3), tmpDec.get(4), tmpDec.get(5), tmpDec.get(6), tmpDec.get(7), tmpDec.get(8), tmpDec.get(9));
            ventaTotal_lista.add(ventaTotalFinal);
            prices.removeAllViews();
            String [] valores  = {"Nombre", "Nombre Corto", "VENTA TOTAL", "SUBTOTAL VENTA", "IVA VENTA", "Subtotal Factura", "IVA Factura", "Total Factura", "Total Remision", "Subtotal Nota de Credito", "IVA Nota de Credito", "Total Nota de Credito"};
            TableRow tr0 =  new TableRow(TotalesSerie2.this);
            for(int i = 0; i < valores.length; i++){
                TextView c00 = (TextView)TotalesSerie2.this.getLayoutInflater().inflate(R.layout.cabezeras_azul, null);
                c00.setText(valores[i]);
                tr0.addView(c00);
            }
            prices.addView(tr0);


            for(int i = 0; i < ventaTotal_lista.size(); i++){
                TableRow tr =  new TableRow(TotalesSerie2.this);
                TextView c0 = (TextView)TotalesSerie2.this.getLayoutInflater().inflate(R.layout.renglones_azul, null);
                c0.setText(ventaTotal_lista.get(i).getNombre());

                TextView c1 = (TextView)TotalesSerie2.this.getLayoutInflater().inflate(R.layout.renglones_azul, null);
                c1.setText(ventaTotal_lista.get(i).getNombreCorto());

                TextView c2 = (TextView)TotalesSerie2.this.getLayoutInflater().inflate(R.layout.renglones_azul, null);
                c2.setText(Helper.numberFormat(String.valueOf(ventaTotal_lista.get(i).getTotalTotal())));


                TextView c3 = (TextView)TotalesSerie2.this.getLayoutInflater().inflate(R.layout.renglones_azul, null);
                c3.setText(Helper.numberFormat(String.valueOf(ventaTotal_lista.get(i).getSubtotalTotal())));


                TextView c4 = (TextView)TotalesSerie2.this.getLayoutInflater().inflate(R.layout.renglones_azul, null);
                c4.setText(Helper.numberFormat(String.valueOf(ventaTotal_lista.get(i).getIvaTotal())));

                TextView c5 = (TextView)TotalesSerie2.this.getLayoutInflater().inflate(R.layout.renglones_azul, null);
                c5.setText(Helper.numberFormat(String.valueOf(ventaTotal_lista.get(i).getSubtotalFactura())));

                TextView c6 = (TextView)TotalesSerie2.this.getLayoutInflater().inflate(R.layout.renglones_azul, null);
                c6.setText(Helper.numberFormat(String.valueOf(ventaTotal_lista.get(i).getIvaFactura())));

                TextView c7 = (TextView)TotalesSerie2.this.getLayoutInflater().inflate(R.layout.renglones_azul, null);
                c7.setText(Helper.numberFormat(String.valueOf(ventaTotal_lista.get(i).getTotalFactura())));

                TextView c8 = (TextView)TotalesSerie2.this.getLayoutInflater().inflate(R.layout.renglones_azul, null);
                c8.setText(Helper.numberFormat(String.valueOf(ventaTotal_lista.get(i).getTotalRemision())));

                TextView c9 = (TextView)TotalesSerie2.this.getLayoutInflater().inflate(R.layout.renglones_azul, null);
                c9.setText(Helper.numberFormat(String.valueOf(ventaTotal_lista.get(i).getSubtotalNotaCredito())));

                TextView c10 = (TextView)TotalesSerie2.this.getLayoutInflater().inflate(R.layout.renglones_azul, null);
                c10.setText(Helper.numberFormat(String.valueOf(ventaTotal_lista.get(i).getIvaNotaCredito())));

                TextView c11 = (TextView)TotalesSerie2.this.getLayoutInflater().inflate(R.layout.renglones_azul, null);
                c11.setText(Helper.numberFormat(String.valueOf(ventaTotal_lista.get(i).getTotalNotaCredito())));


                tr.addView(c0);
                tr.addView(c1);
                tr.addView(c2);
                tr.addView(c3);
                tr.addView(c4);
                tr.addView(c5);
                tr.addView(c6);
                tr.addView(c7);
                tr.addView(c8);
                tr.addView(c9);
                tr.addView(c10);
                tr.addView(c11);;
                prices.addView(tr);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




   private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(TotalesSerie2.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                System.out.println("Fecha Inicial: " + dateFormatter.format(newDate.getTime()));
                fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
                newDate.set(year, monthOfYear, dayOfMonth, 6, 0, 0);
                f_ini= newDate.getTimeInMillis();
                System.out.println("Inicial " + f_ini);

            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));



        toDatePickerDialog = new DatePickerDialog(TotalesSerie2.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                toDateEtxt.setText(dateFormatter.format(newDate.getTime()));
                newDate.set(year, monthOfYear, dayOfMonth+1, 6, -1, 0);
                f_fin = newDate.getTimeInMillis();
                System.out.println("Final " + f_fin);
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }



}