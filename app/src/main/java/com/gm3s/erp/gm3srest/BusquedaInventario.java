package com.gm3s.erp.gm3srest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.view.View;
import com.gm3s.erp.gm3srest.Model.Articulo;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.Model.SharedPreference;
import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.View.Informacion_Articulo;
import com.gm3s.erp.gm3srest.View.MainActivity;
import com.gm3s.erp.gm3srest.View.NuevoArticulo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import android.support.v7.widget.Toolbar;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BusquedaInventario extends AppCompatActivity {
    private SharedPreference sharedPreference;
    private static PersistentCookieStore pc;
    String server = "";
static EditText sku_articulo;
private Timer timer = new Timer();
    private final long DELAY2 = 500;
    static boolean validacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_inventario);
        pc = new PersistentCookieStore(this);
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Busqueda Articulo Inventario");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusquedaInventario.this,MainActivity.class);
                startActivity(intent);
            }
        });


        sku_articulo = (EditText) findViewById(R.id.sku_articulo);
        sku_articulo.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {


                if (s.length() > 3) {
                    System.out.println(" ***** 0");
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    System.out.println(" ***** 1");


                                    System.out.println(" ***** 2");
                                    HttpAsyncTask a = new HttpAsyncTask();
                                    a.execute(server + "/medialuna/spring/util/genericos/busquedaArticulosApp/");


                                }
                            },
                            DELAY2
                    );

                }


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

            System.out.println("Resultado: "  + result);


            sku_articulo.setText("");
            if (result.equals("[]") || result.equals("")) {
                Intent intent = new Intent(BusquedaInventario.this, NuevoArticulo.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);


            }
            else {

                convertirDatosArticulo(result);
            }
        }
    }


    public static String POST(String url) {
        LinkedHashMap pagerOrden = new LinkedHashMap();
        pagerOrden.put("@class", LinkedHashMap.class.getName());

        Map pagerFiltros = new HashMap();
        pagerFiltros.put("@class", HashMap.class.getName());


        System.out.println("TEXTO:" + sku_articulo.getText().toString()+":");

        if(sku_articulo.getText().toString().contains("\n")){
           System.out.println("1");
        }
        if(sku_articulo.getText().toString().contains("\\n")){
            System.out.println("2");
        }
        if(sku_articulo.getText().toString().contains("\\\n")){
            System.out.println("3");
        }
        if(sku_articulo.getText().toString().contains("\\\\n")){
            System.out.println("4");
        }

        if(sku_articulo.getText().toString().contains("\n")){
            agregarCamposEntidad(sku_articulo.getText().toString().substring(0, sku_articulo.getText().toString().length() - 1), pagerFiltros, "codigoSKU");
        }
        else{
            agregarCamposEntidad(sku_articulo.getText().toString(), pagerFiltros, "codigoSKU");

        }



        Map entidad = new HashMap();
        entidad.put("@class", HashMap.class.getName());
        entidad.put("activos", true);
        entidad.put("maxResults", 1);
        entidad.put("firstResult", 0);
        entidad.put("pagerFiltros", pagerFiltros);
        entidad.put("pagerOrden", pagerOrden);

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

                if (result.contains("GM3s Software Index")) {
                    validacion = false;
                } else {
                    validacion = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    static void agregarCamposEntidad(String campo, Map entidad, String key) {

        if (campo.equals("")) {
        } else {
            entidad.put(key, campo);
        }
    }


    public void convertirDatosArticulo(String cadena) {

        ObjectMapper mapper = new ObjectMapper();
        try {


            List<Object> arrayData = mapper.readValue(cadena, List.class);
            Map mapa1 = (Map) arrayData.get(0);
            Articulo art = new Articulo((Integer) mapa1.get("id"), (String) mapa1.get("nombreCorto"), Double.parseDouble(mapa1.get("precioBase").toString()), Integer.parseInt(mapa1.get("códigoUsuario").toString()));

            System.out.println("Articulo: " + art.toString());

            Intent intent = new Intent(BusquedaInventario.this, Informacion_Articulo.class);
            intent.putExtra("opcion", "0");
            intent.putExtra("idArticulo", mapa1.get("id").toString());
            intent.putExtra("codigoUsuario", mapa1.get("códigoUsuario").toString());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);


        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
