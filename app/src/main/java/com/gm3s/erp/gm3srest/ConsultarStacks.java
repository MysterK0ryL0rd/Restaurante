package com.gm3s.erp.gm3srest;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.Service.Helper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class ConsultarStacks extends AppCompatActivity {
    private static PersistentCookieStore pc;
    private SharedPreference sharedPreference;
    String server = "";
    TableLayout tabla_contenido;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_stacks);

        pc = new PersistentCookieStore(getApplicationContext());
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);
        tabla_contenido = (TableLayout) findViewById(R.id.table_consultarStacks);

        HttpAsyncTask a = new HttpAsyncTask();
        a.execute(server + "/medialuna/spring/binLocation/consultar/reporteRacks");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Consulta de Racks");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }


        @Override
        protected void onPostExecute(String result) {
            if (result.equals("[]") || result.equals("")) {
                Toast.makeText(getApplicationContext(), "No se encontraron resultados.", Toast.LENGTH_LONG).show();
            } else {
                System.out.println("result " + result);
                convertirDatoStack(result);

            }
        }
    }


    public static String GET(String url) {


        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpGet httppost = new HttpGet(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json");
            String json = "";

            try {

                HttpResponse httpResponse = httpclient.execute(httppost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null) {
                    result = Helper.convertInputStreamToString(inputStream);
                } else
                    result = "Did not work!";

                if (result.contains("GM3s Software Index")) {

                } else {

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



    public void convertirDatoStack(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            limpiar();
            List<Object> arrayData = mapper.readValue(cadena, List.class);
            for (int i = 0; i < arrayData.size(); i++) {


                TableRow tr = new TableRow(ConsultarStacks.this);

                TextView a = new TextView(this);
                a.setText("   " + (String) ((HashMap) arrayData.get(i)).get("denominacionRack") + "   ");
                a.setGravity(Gravity.CENTER);
                a.setTextSize(Float.parseFloat("15.0"));

                TextView b = new TextView(this);
                b.setText("   " + (String) ((HashMap) arrayData.get(i)).get("descripcionRack") + "   ");
                b.setGravity(Gravity.CENTER);
                b.setTextSize(Float.parseFloat("15.0"));

                TextView c = new TextView(this);
                c.setText("   " + (String) ((HashMap) arrayData.get(i)).get("denominacionUbicacion") + "   ");
                c.setGravity(Gravity.CENTER);
                c.setTextSize(Float.parseFloat("15.0"));

                TextView d = new TextView(this);
                d.setText("   " + (String) ((HashMap) arrayData.get(i)).get("bodega") + "   ");
                d.setGravity(Gravity.CENTER);
                d.setTextSize(Float.parseFloat("15.0"));

                TextView e = new TextView(this);
                e.setText("   " + (String) ((HashMap) arrayData.get(i)).get("nombreArticulo") + "   ");
                e.setGravity(Gravity.CENTER);
                e.setTextSize(Float.parseFloat("15.0"));

                tr.addView(a);
                tr.addView(b);
                tr.addView(c);
                tr.addView(d);
                tr.addView(e);

              //  tr.addView(ly);

                if(i%2==0){
                    tr.setBackgroundColor(Color.parseColor("#267F7FFF"));
                    // a.setTextColor(Color.parseColor("#FFFFFF"));
                }

                tabla_contenido.addView(tr);


                if(i==(arrayData.size()-1)){
                    TableRow tr1 = new TableRow(ConsultarStacks.this);

                    TextView a1 = new TextView(this);
                    a1.setText("   RACK   ");
                    a1.setGravity(Gravity.CENTER);
                    a1.setTextColor(Color.parseColor("#FFFFFF"));
                    a1.setTypeface(null, Typeface.BOLD);
                    a1.setTextSize(Float.parseFloat("20.0"));

                    TextView b1 = new TextView(this);
                    b1.setText("   DESCRIPCION RACK   ");
                    b1.setGravity(Gravity.CENTER);
                    b1.setTextColor(Color.parseColor("#FFFFFF"));
                    b1.setTypeface(null, Typeface.BOLD);
                    b1.setTextSize(Float.parseFloat("20.0"));

                    TextView c1 = new TextView(this);
                    c1.setText("   UBICACION   ");
                    c1.setGravity(Gravity.CENTER);
                    c1.setTextColor(Color.parseColor("#FFFFFF"));
                    c1.setTypeface(null, Typeface.BOLD);
                    c1.setTextSize(Float.parseFloat("20.0"));

                    TextView d1 = new TextView(this);
                    d1.setText("   BODEGA   ");
                    d1.setGravity(Gravity.CENTER);
                    d1.setTextColor(Color.parseColor("#FFFFFF"));
                    d1.setTypeface(null, Typeface.BOLD);
                    d1.setTextSize(Float.parseFloat("20.0"));

                    TextView e1 = new TextView(this);
                    e1.setText("   ARTICULO   ");
                    e1.setGravity(Gravity.CENTER);
                    e1.setTextColor(Color.parseColor("#FFFFFF"));
                    e1.setTypeface(null, Typeface.BOLD);
                    e1.setTextSize(Float.parseFloat("20.0"));

                    tr1.addView(a1);
                    tr1.addView(b1);
                    tr1.addView(c1);
                    tr1.addView(d1);
                    tr1.addView(e1);

                    tr1.setBackgroundColor(Color.parseColor("#3D6AB3"));

                    tabla_contenido.addView(tr1,0);

                }


            }
            if(arrayData.isEmpty())
                Toast.makeText(getApplicationContext(),"No se encontraron resultados", Toast.LENGTH_SHORT).show();
        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void limpiar(){

        if(tabla_contenido.getChildCount()>1) {
            tabla_contenido.removeViews(1, tabla_contenido.getChildCount()-1);
        }
    }
}
