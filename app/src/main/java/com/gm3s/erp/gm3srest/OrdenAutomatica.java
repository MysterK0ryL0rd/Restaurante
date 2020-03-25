package com.gm3s.erp.gm3srest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.Service.Helper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdenAutomatica extends AppCompatActivity {
    ImageButton nueva_orden_btn;
    Map<String,String> info = new HashMap<>();
    private SharedPreference sharedPreference;
    String server = "";
    private static PersistentCookieStore pc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orden_automatica);

        pc = new PersistentCookieStore(this);
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);
        nueva_orden_btn = (ImageButton) findViewById(R.id.nueva_orden_btn);

        HttpAsyncTask a = new HttpAsyncTask();
        a.execute(server + "/medialuna/spring/listar/serie/contar/pedidocliente/");

        nueva_orden_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrdenAutomatica.this, ContenedorFragmentos2.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("PedidoArt", JSONValue.toJSONString(""));
                intent.putExtra("PedidoMapa", JSONValue.toJSONString(""));
                intent.putExtra("InfoMapa", JSONValue.toJSONString(info));
                intent.putExtra("tipo_documento", "remisioncliente");
                intent.putExtra("comensales", "1");
                intent.putExtra("idComensal", "M"+1 + "C" + "1");//------------------------------------------------------------------------"
                intent.putExtra("idMesa", "1");
                intent.putExtra("esCaja", "auto");
                startActivity(intent);
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
            convertirDatos(result);
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
            Map<String, Object> arrayData0 = mapper.readValue(cadena, HashMap.class);
            if(arrayData0.containsKey("serie")) {
                HashMap<String, Object> arrayData = (HashMap<String, Object>) arrayData0.get("serie");
                info.put("serie", arrayData.get("nombre").toString());
                info.put("serie_id", arrayData.get("id").toString());

                if (arrayData.get("impuesto")!=null){
                    HashMap<String, Object> arrayData1 = (HashMap<String, Object>) arrayData.get("impuesto");
                    info.put("impuesto",  arrayData1.get("impuesto").toString());
                   }
                Object cliente1 = (Object) arrayData.get("cliente");
                Object bodega1 = (Object) arrayData.get("bodega");
                HashMap<String, Object> cliente = (HashMap<String, Object>) arrayData.get("cliente");
                HashMap<String, Object> bodega = (HashMap<String, Object>) arrayData.get("bodega");
                if (cliente1 == null) {
                   } else {
                    String nombreC = cliente.get("nombre").toString();
                    HashMap<String, Object> agente = (HashMap<String, Object>) cliente.get("agente");
                    String codigoA = agente.get("id").toString();
                    String nombreA = agente.get("nombre").toString();
                    List<Object> direcciones = (List) cliente.get("direcciones");
                    List<HashMap> direccionimpl = (List) direcciones.get(1);
                    HashMap direccion_impl_0 = direccionimpl.get(0);
                    info.put("idDireccion",((Integer) direccion_impl_0.get("id")).toString());
                    info.put("cliente", nombreC);
                    info.put("cliente_id", cliente.get("id").toString());
                    info.put("agente", nombreA);
                    info.put("agente_id", codigoA);
                    if (cliente.containsKey("moneda") && cliente.get("moneda") != null) {
                        Map mapa2 = (Map) cliente.get("moneda");
                        info.put("lprecio",((Integer) mapa2.get("id")).toString());
                    }
                }
                if(bodega1 == null){}
                else{
                    info.put("bodega",bodega.get("id").toString());
                }
                if (Integer.parseInt(arrayData0.get("total").toString()) >= 1 && arrayData.get("id").toString().equals("")) {
                    info.put("serie", arrayData.get("nombre").toString());
                    info.put("serie_id", arrayData.get("id").toString());
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
