package com.gm3s.erp.gm3srest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gm3s.erp.gm3srest.Model.Articulo;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.Service.Helper;

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

public class AsignacionDeStacks extends AppCompatActivity {
    Button btnguardarAsignacion;
    private static PersistentCookieStore pc;
    private SharedPreference sharedPreference;
    String server = "";
    static EditText articulo_et,ubicacion_et;
    static Articulo art;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asignacion_de_stacks);


        pc = new PersistentCookieStore(getApplicationContext());
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);

        articulo_et = (EditText) findViewById(R.id.articulo_et);
        ubicacion_et = (EditText) findViewById(R.id.ubicacion_et);

        btnguardarAsignacion = (Button) findViewById(R.id.btnguardarAsignacion);
        btnguardarAsignacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask a = new HttpAsyncTask();
                a.execute(server + "/medialuna/spring/binLocation/guardar/asignaciones/denominacion");
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Asignacion de Racks");
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
            if(result.equals("1"))
                Toast.makeText(getApplicationContext(), "Relacion creada exitosamente.", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "No se pudo crear la relacion.", Toast.LENGTH_LONG).show();

            ubicacion_et.setText("");
            articulo_et.setText("");

        }
    }

    public static String POST(String url) {

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("@class", Map.class.getName());
        map.put("skuArticulo",articulo_et.getText().toString());
        map.put("denominacion",ubicacion_et.getText().toString());




        String objectStr = JSONValue.toJSONString(map);

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





}
