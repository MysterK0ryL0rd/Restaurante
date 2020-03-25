package com.gm3s.erp.gm3srest.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gm3s.erp.gm3srest.Service.Helper;
import com.gm3s.erp.gm3srest.Model.Articulo;
import com.gm3s.erp.gm3srest.Model.PersistentCookieStore;
import com.gm3s.erp.gm3srest.R;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Busqueda_Articulo extends AppCompatActivity {
    private SharedPreference sharedPreference;
    private static PersistentCookieStore pc;
    String server = "";
   static EditText ac_bus_art_nom, ac_bus_art_nomc, ac_bus_art_cod;
    Button ac_bus_art_btn_bus;
    TableLayout prices;
    static boolean validacion;
    List<Articulo> lista_art = new ArrayList<Articulo>();
    List<Articulo> lista_articulos = new ArrayList<Articulo>();
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda__articulo);
        pc = new PersistentCookieStore(this);
        sharedPreference = new SharedPreference();
        server = sharedPreference.getValue(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Buscar Articulo");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ac_bus_art_nom = (EditText) findViewById(R.id.ac_bus_art_nom);
        ac_bus_art_nomc = (EditText) findViewById(R.id.ac_bus_art_nomc);
        ac_bus_art_cod = (EditText) findViewById(R.id.ac_bus_art_cod);
        ac_bus_art_btn_bus = (Button) findViewById(R.id.ac_bus_art_btn_bus);

        prices = (TableLayout) findViewById(R.id.tabla_prueba);
        while (prices.getChildCount() != 1) {
            prices.removeViewAt(prices.getChildCount() - 1);
        }
        prices.setStretchAllColumns(true);
        prices.bringToFront();


        ac_bus_art_btn_bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ac_bus_art_nom.getText().toString().equals("") && ac_bus_art_nomc.getText().toString().equals("") && ac_bus_art_cod.getText().toString().equals("")) {

                } else {
                    HttpAsyncTask a = new HttpAsyncTask();
                    a.execute(server + "/medialuna/spring/util/genericos/busquedaArticulosApp/");

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
             convertirDatosArticulo(result);
        }
    }


    public static String POST(String url) {
        LinkedHashMap pagerOrden = new LinkedHashMap();
        pagerOrden.put("@class", LinkedHashMap.class.getName());

        Map pagerFiltros = new HashMap();
        pagerFiltros.put("@class", HashMap.class.getName());

        agregarCamposEntidad(ac_bus_art_nomc.getText().toString(), pagerFiltros, "nombreCorto");
        agregarCamposEntidad(ac_bus_art_nom.getText().toString(), pagerFiltros, "nombre");
        agregarCamposEntidad(ac_bus_art_cod.getText().toString(), pagerFiltros, "codigoSKU");

        Map entidad = new HashMap();
        entidad.put("@class", HashMap.class.getName());
        entidad.put("activos", true);
        entidad.put("maxResults", 15);
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

          /*  HttpURLConnection urlConnection = null;
            URL url2 = new URL(url);
            urlConnection = (HttpURLConnection) url2.openConnection();
            urlConnection.connect();
            is = urlConnection.getInputStream();
            */



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
            lista_art.clear();

            List<Object> arrayData = mapper.readValue(cadena, List.class);


            for (int i = 0; i < arrayData.size(); i++) {
                Map mapa1 = (Map) arrayData.get(i);

                Articulo art = new Articulo((Integer) mapa1.get("id"), (String) mapa1.get("nombreCorto"), Double.parseDouble(mapa1.get("precioBase").toString()), Integer.parseInt(mapa1.get("códigoUsuario").toString()));
                lista_art.add(art);


                counter++;
                art.setCounter(counter);
                lista_articulos.add(art);


            }
            crearTablaArticulos();

        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }



 public void crearTablaArticulos() {
     while (prices.getChildCount() != 1) {
         prices.removeViewAt(prices.getChildCount() - 1);
     }
        if (lista_art.isEmpty()) {
            TextView c1 = new TextView(Busqueda_Articulo.this);
            c1.setText("No se encontraron resultados");
            prices.addView(c1);
        } else {
            for (int j = 0; j < lista_art.size(); j++) {


                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(30, 30);

                // counter = counter +1;

                final Articulo art = lista_art.get(j);


                TableRow tr = new TableRow(Busqueda_Articulo.this);

                TextView c2 = (TextView) Busqueda_Articulo.this.getLayoutInflater().inflate(R.layout.renglones2, null);
                c2.setText(art.getNombreCorto());

                TextView c3 = (TextView) Busqueda_Articulo.this.getLayoutInflater().inflate(R.layout.renglones, null);
               c3.setText(art.getPrecioBase().toString());

                ImageView c4 = new ImageView(Busqueda_Articulo.this);
                c4.measure(100,100);
                c4.setImageResource(R.drawable.edit_icon);

                c4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Busqueda_Articulo.this, Informacion_Articulo.class);
                        intent.putExtra("opcion", "0");
                        intent.putExtra("idArticulo", art.getId().toString());
                        intent.putExtra("codigoUsuario", art.getCodigoUsuario().toString());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);

                    }
                });


                ImageView c5 = new ImageView (Busqueda_Articulo.this);
                c5.measure(100,100);
                c5.setImageResource(R.drawable.delete_icon);
                c5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        build_popup(art.getId());

                    }
                });



                tr.addView(c2);
                tr.addView(c3);
                tr.addView(c4);
                tr.addView(c5);


                prices.addView(tr);
            }
        }
    }


    private void build_popup(final Integer id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Busqueda_Articulo.this);
        builder.setTitle("Eliminar Articulo");
        builder.setMessage("Seguro que desea elimar este articulo?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                HttpAsyncTask2 a = new HttpAsyncTask2();
                a.execute(server + "/medialuna/spring/app/eliminarArticulo/"+ id);


            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog al = builder.create();
        al.show();
    }


    private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST2(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            HttpAsyncTask a = new HttpAsyncTask();
            a.execute(server + "/medialuna/spring/app/busquedaArticulos");
        }
    }


    public static String POST2(String url) {
       String result = "";
        InputStream inputStream = null;
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieStore().addCookie(pc.getCookies().get(0));
            HttpPost httppost = new HttpPost(url);
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

}
