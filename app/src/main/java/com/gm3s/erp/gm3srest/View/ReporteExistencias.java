package com.gm3s.erp.gm3srest.View;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.R;
import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ReporteExistencias extends AppCompatActivity {
    public static ImageView loader;
    TableLayout existencias;
    private SharedPreference sharedPreference;
    String server = "";
    private static PersistentCookieStore pc;

    ////localhost:8080/medialuna/spring/almacen/buscar/existencia/total/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_existencias);

        pc = new PersistentCookieStore(this);
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);

        existencias =(TableLayout) findViewById(R.id.main_table);
        existencias.setStretchAllColumns(true);
        existencias.bringToFront();

        loader = (ImageView) findViewById(R.id.loader);
        loader.setVisibility(View.INVISIBLE);
        loader.setVisibility(View.VISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Reporte de Existencias");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        HttpAsyncTask a = new HttpAsyncTask();
        a.execute(server + "/medialuna/spring/almacen/reporteExistencias/app/");
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            loader.setVisibility(View.INVISIBLE);
            System.out.println("Result: " + result);
            if(result.contains("{}")){

            }
            else {

                convertirDatos(result);
            }
        }
    }


    public static String POST(String url) {

        Map entidad = new HashMap();
        entidad.put("@class", HashMap.class.getName());




        String objectStr = JSONValue.toJSONString(entidad);


        String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("Accept", "application/json; text/javascript");
            httppost.setHeader("Content-Type", "application/json");
            StringEntity params = new StringEntity(objectStr);
            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(params);
            String json = "";

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


    public void convertirDatos(String cadena) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            HashMap<String, Object> arrayData = mapper.readValue(cadena, HashMap.class);

            existencias.removeAllViews();

            TableRow tr0 = new TableRow(this);
            TextView c01 = (TextView) this.getLayoutInflater().inflate(R.layout.cabezeras, null);
            c01.setText("Existencia");
            TextView c02 = (TextView) this.getLayoutInflater().inflate(R.layout.cabezeras, null);
            c02.setText(Helper.numberFormat((String)arrayData.get("e")).substring(1) + "  ");
            tr0.addView(c01);
            tr0.addView(c02);
            existencias.addView(tr0);

            TableRow tr1 = new TableRow(this);
            TextView c11 = (TextView) this.getLayoutInflater().inflate(R.layout.cabezeras, null);
            c11.setText("Costo");
            TextView c12 = (TextView) this.getLayoutInflater().inflate(R.layout.cabezeras, null);
            c12.setText(Helper.numberFormat((String)arrayData.get("c")) + "  ");
            tr1.addView(c11);
            tr1.addView(c12);
            existencias.addView(tr1);

            TableRow tr2 = new TableRow(this);
            TextView c21 = (TextView) this.getLayoutInflater().inflate(R.layout.cabezeras, null);
            c21.setText("Total");
            TextView c22 = (TextView) this.getLayoutInflater().inflate(R.layout.cabezeras, null);
            c22.setText(Helper.numberFormat((String)arrayData.get("tt")) + "  ");
            tr2.addView(c21);
            tr2.addView(c22);
            existencias.addView(tr2);

            TableRow tr3 = new TableRow(this);
            TextView c31 = (TextView) this.getLayoutInflater().inflate(R.layout.cabezeras, null);
            c31.setText("Costo Base");
            TextView c32 = (TextView) this.getLayoutInflater().inflate(R.layout.cabezeras, null);
            c32.setText(Helper.numberFormat((String)arrayData.get("cb")) + "  ");
            tr3.addView(c31);
            tr3.addView(c32);
            existencias.addView(tr3);

            TableRow tr4 = new TableRow(this);
            TextView c41 = (TextView) this.getLayoutInflater().inflate(R.layout.cabezeras, null);
            c41.setText("Total Base");
            TextView c42 = (TextView) this.getLayoutInflater().inflate(R.layout.cabezeras, null);
            c42.setText(Helper.numberFormat((String) arrayData.get("tb")) + "  ");
            tr4.addView(c41);
            tr4.addView(c42);
            existencias.addView(tr4);


        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }




}
