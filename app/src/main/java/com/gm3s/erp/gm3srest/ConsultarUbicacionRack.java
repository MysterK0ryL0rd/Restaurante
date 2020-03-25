package com.gm3s.erp.gm3srest;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ConsultarUbicacionRack extends AppCompatActivity {
    private static PersistentCookieStore pc;
    private SharedPreference sharedPreference;
    String server = "";
    TableLayout tabla_contenido;
    Button consultar_ubicacion_rack_btn;
    EditText articulo_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_ubicacion_rack);

        pc = new PersistentCookieStore(getApplicationContext());
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);

        tabla_contenido = (TableLayout) findViewById(R.id.table_consultarUbicacion);



        articulo_et = (EditText) findViewById(R.id.articulo_et);

        consultar_ubicacion_rack_btn = (Button) findViewById(R.id.consultar_ubicacion_rack_btn);
        consultar_ubicacion_rack_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(articulo_et.getText().toString()!=null) {
                    HttpAsyncTask a = new HttpAsyncTask();
                    a.execute(server + "/medialuna/spring/binLocation/consultar/articulo/"+articulo_et.getText().toString());
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Consultar Ubicacion");
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
            return POST(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            tabla_contenido.removeAllViews();
            if(result.equals("")) {
                Toast.makeText(getApplicationContext(), "No se encontraron resultados", Toast.LENGTH_SHORT).show();
            }
            else
            convertirInformacionUbicacionArticulo(result);
        }
    }

    public static String POST(String url) {



        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpGet httppost = new HttpGet(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json");
            try {

                HttpResponse httpResponse = httpclient.execute(httppost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null) {
                    result = Helper.convertInputStreamToString(inputStream);
                } else
                    result = "Did not work!";
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

    public void convertirInformacionUbicacionArticulo(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {

            List<Object> arrayData = mapper.readValue(cadena, List.class);
            for (int i = 0; i < arrayData.size(); i++) {


                TableRow tr = new TableRow(ConsultarUbicacionRack.this);

                TextView a = new TextView(this);
                a.setText("   " + (String) ((HashMap) ((HashMap) arrayData.get(i)).get("rack")).get("denominacion") + "   ");
                a.setGravity(Gravity.CENTER);
                a.setTextSize(Float.parseFloat("15.0"));

                TextView c = new TextView(this);
                c.setText("   " + (String) ((HashMap) arrayData.get(i)).get("denominacion") + "   ");
                c.setGravity(Gravity.CENTER);
                c.setTextSize(Float.parseFloat("15.0"));

                TextView d = new TextView(this);
                d.setText("   " + (String) ((HashMap) ((HashMap) ((HashMap) arrayData.get(i)).get("rack")).get("bodega")).get("nombre") + "   ");
                d.setGravity(Gravity.CENTER);
                d.setTextSize(Float.parseFloat("15.0"));

                tr.addView(a);
                tr.addView(c);
                tr.addView(d);

                if(i%2==0){
                    tr.setBackgroundColor(Color.parseColor("#267F7FFF"));
                }

                tabla_contenido.addView(tr);


                if(i==(arrayData.size()-1)){
                    TableRow tr1 = new TableRow(ConsultarUbicacionRack.this);

                    TextView a1 = new TextView(this);
                    a1.setText("   RACK   ");
                    a1.setGravity(Gravity.CENTER);
                    a1.setTextColor(Color.parseColor("#FFFFFF"));
                    a1.setTypeface(null, Typeface.BOLD);
                    a1.setTextSize(Float.parseFloat("20.0"));

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

                    tr1.addView(a1);
                    tr1.addView(c1);
                    tr1.addView(d1);

                    tr1.setBackgroundColor(Color.parseColor("#3D6AB3"));

                    tabla_contenido.addView(tr1,0);

                }








            }

        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
